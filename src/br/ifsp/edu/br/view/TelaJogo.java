package br.ifsp.edu.br.view;

import br.ifsp.edu.br.config.Config;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TelaJogo extends JFrame {

    private JButton[][] botoesTabuleiro;
    private JTextArea mensagemArea;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int tamanhoTabuleiro;

    // Construtor da GUI do Cliente
    public TelaJogo() {
        super("Campo Minado em Rede - Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        tamanhoTabuleiro = Config.getTamanhoTabuleiro();
        // --- Painel Principal da Janela ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Área de Mensagens (Parte Superior) ---
        mensagemArea = new JTextArea(5, 30);
        mensagemArea.setEditable(false);
        mensagemArea.setLineWrap(true);
        mensagemArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(mensagemArea);
        mainPanel.add(scrollPane, BorderLayout.NORTH);

        // --- Painel do Tabuleiro (Centro) ---
        JPanel tabuleiroPanel = new JPanel(new GridLayout(tamanhoTabuleiro, tamanhoTabuleiro, 2, 2));
        botoesTabuleiro = new JButton[tamanhoTabuleiro][tamanhoTabuleiro];

        // Cria e configura cada botão do tabuleiro
        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                JButton button = new JButton("*");
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setFocusPainted(false);

                final int row = i;
                final int col = j;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        enviarJogada(row, col);
                    }
                });
                botoesTabuleiro[i][j] = button;
                tabuleiroPanel.add(button);
            }
        }
        mainPanel.add(tabuleiroPanel, BorderLayout.CENTER);

        // Adiciona o painel principal à janela
        add(mainPanel);
        setVisible(true);

        // Inicia a conexão de rede em uma thread separada para não travar
        iniciarConexaoRede();
    }

    // Método para iniciar a comunicação de rede em segundo plano
    private void iniciarConexaoRede() {
        // SwingWorker é ideal para tarefas longas
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Tenta conectar ao servidor usando as configurações
                    socket = new Socket(Config.getIp(), Config.getPorta());
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    // Publica uma mensagem para ser exibida na área de mensagens
                    publish("Conectado ao servidor: " + socket.getInetAddress().getHostAddress());

                    // Loop principal de leitura de mensagens do servidor
                    while (true) {
                        Object mensagem = in.readObject(); // Lê a mensagem do servidor
                        if (mensagem instanceof String) {
                            String texto = (String) mensagem;
                            // Publica a mensagem para ser processada na Event Dispatch Thread (EDT)
                            publish(texto);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    // Em caso de erro na rede, publica a mensagem de erro
                    publish("Erro na conexão: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // Fecha o socket quando o loop termina ou ocorre um erro
                    try {
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            // Este método é executado na EDT e recebe as mensagens publicadas por doInBackground
            protected void process(java.util.List<String> chunks) {
                for (String texto : chunks) {
                    if (texto.startsWith("Tabuleiro:\n")) {
                        // Se a mensagem é um tabuleiro, atualiza a GUI do tabuleiro
                        atualizarTabuleiroGUI(texto.substring("Tabuleiro:\n".length()));
                    } else if (texto.startsWith("Sua vez")) {
                        // Se é a vez do jogador, habilita os botões para clique
                        mensagemArea.append("\n" + texto);
                        setBotoesHabilitados(true);
                    } else if (texto.toLowerCase().contains("deseja jogar novamente")) {
                        // Se o servidor pergunta sobre revanche, abre um pop-up
                        int resposta = JOptionPane.showConfirmDialog(TelaJogo.this,
                                "Você deseja jogar novamente?", "Novo Jogo", JOptionPane.YES_NO_OPTION);
                        try {
                            // Envia a resposta do pop-up para o servidor
                            out.writeObject((resposta == JOptionPane.YES_OPTION) ? "sim" : "nao");
                            out.flush();
                        } catch (IOException ex) {
                            mensagemArea.append("\nErro ao enviar resposta de novo jogo: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else if (texto.toLowerCase().contains("jogo encerrado")) { // Condição adicionada
                        mensagemArea.append("\n" + texto);
                        setBotoesHabilitados(false);
                        // --- Adicionado para fechar a janela do cliente ---
                        dispose();
                        // --------------------------------------------------
                    } else {
                        // Para todas as outras mensagens, apenas adiciona à área de mensagens
                        mensagemArea.append("\n" + texto);
                        // Se o jogo termina, desabilita os botões
                        if (texto.toLowerCase().contains("fim de jogo") || texto.toLowerCase().contains("venceu")) {
                            setBotoesHabilitados(false);
                        }
                    }
                }
            }
        }.execute(); // Inicia o SwingWorker
    }

    // Método para enviar a jogada (coordenadas x e y) para o servidor
    private void enviarJogada(int x, int y) {
        // Verifica se as streams estão prontas e se o botão clicado está habilitado
        if (out != null && botoesTabuleiro[x][y].isEnabled()) {
            try {
                out.writeObject(x + " " + y); // Envia as coordenadas como "x y"
                out.flush(); 
                setBotoesHabilitados(false);
            } catch (IOException e) {
                mensagemArea.append("\nErro ao enviar jogada: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Método para atualizar o texto e a cor dos botões do tabuleiro na GUI
    private void atualizarTabuleiroGUI(String tabuleiroString) {
        String[] linhas = tabuleiroString.trim().split("\n");
        for (int i = 0; i < linhas.length; i++) {
            String[] colunas = linhas[i].trim().split(" ");
            for (int j = 0; j < colunas.length; j++) {
                botoesTabuleiro[i][j].setText(colunas[j]);
                if (colunas[j].equals("0")) {
                    botoesTabuleiro[i][j].setBackground(Color.LIGHT_GRAY);
                } else if (colunas[j].equals("*")) {
                    botoesTabuleiro[i][j].setBackground(null);
                }
            }
        }
    }

    // Método para habilitar ou desabilitar todos os botões do tabuleiro
    private void setBotoesHabilitados(boolean habilitar) {
        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                botoesTabuleiro[i][j].setEnabled(habilitar); // Define o estado de habilitação do botão
            }
        }
    }

}
