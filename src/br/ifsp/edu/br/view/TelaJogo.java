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
    
    public TelaJogo() {
        super("Campo Minado em Rede - Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        tamanhoTabuleiro = Config.getTamanhoTabuleiro();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mensagemArea = new JTextArea(5, 30);
        mensagemArea.setEditable(false);
        mensagemArea.setLineWrap(true);
        mensagemArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(mensagemArea);
        mainPanel.add(scrollPane, BorderLayout.NORTH);

        JPanel tabuleiroPanel = new JPanel(new GridLayout(tamanhoTabuleiro, tamanhoTabuleiro, 2, 2));
        botoesTabuleiro = new JButton[tamanhoTabuleiro][tamanhoTabuleiro];

        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                JButton button = new JButton("*");
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setFocusPainted(false);
                
                final int row = i;
                final int int_col = j; 
                
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        enviarJogada(row, int_col);
                    }
                });
                botoesTabuleiro[i][j] = button;
                tabuleiroPanel.add(button);
            }
        }
        mainPanel.add(tabuleiroPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        iniciarConexaoRede();
    }

    private void iniciarConexaoRede() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    socket = new Socket(Config.getIp(), Config.getPorta());
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    
                    publish("Conectado ao servidor: " + socket.getInetAddress().getHostAddress());
                    
                    while (true) {
                        Object mensagem = in.readObject();
                        if (mensagem instanceof String) {
                            String texto = (String) mensagem;
                            publish(texto);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    publish("Erro na conexão: " + e.getMessage());
                    e.printStackTrace();
                } finally {
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
            protected void process(java.util.List<String> chunks) {
                for (String texto : chunks) {
                    if (texto.startsWith("Tabuleiro:\n")) {
                        atualizarTabuleiroGUI(texto.substring("Tabuleiro:\n".length()));
                    } else if (texto.startsWith("Sua vez")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "É a sua vez de jogar! 🎲", "Sua Vez", JOptionPane.INFORMATION_MESSAGE);
                        // mensagemArea.append("\n" + texto); // Removido para não duplicar no JTextArea
                        setBotoesHabilitados(true); 
                    } else if (texto.toLowerCase().contains("deseja jogar novamente")) {
                        int resposta = JOptionPane.showConfirmDialog(TelaJogo.this,
                                "Você deseja jogar novamente?", "Novo Jogo", JOptionPane.YES_NO_OPTION);
                        try {
                            out.writeObject((resposta == JOptionPane.YES_OPTION) ? "sim" : "nao");
                            out.flush();
                        } catch (IOException ex) {
                            mensagemArea.append("\nErro ao enviar resposta de novo jogo: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else if (texto.toLowerCase().contains("você acertou uma bomba! fim de jogo.")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "💣 " + texto, "Fim de Jogo", JOptionPane.ERROR_MESSAGE);
                        setBotoesHabilitados(false); 
                    } else if (texto.toLowerCase().contains("o jogador 1 perdeu. você venceu!")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "🎉 " + texto, "Vitória!", JOptionPane.INFORMATION_MESSAGE);
                        setBotoesHabilitados(false);
                    } else if (texto.toLowerCase().contains("o jogador 2 perdeu. você venceu!")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "🎉 " + texto, "Vitória!", JOptionPane.INFORMATION_MESSAGE);
                        setBotoesHabilitados(false);
                    } else if (texto.toLowerCase().contains("você venceu o jogo!")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "🏆 " + texto, "Vitória!", JOptionPane.INFORMATION_MESSAGE);
                        setBotoesHabilitados(false);
                    } else if (texto.toLowerCase().contains("o jogador 1 venceu o jogo.")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "👍 " + texto, "Resultado do Jogo", JOptionPane.INFORMATION_MESSAGE);
                        setBotoesHabilitados(false);
                    } else if (texto.toLowerCase().contains("o jogador 2 venceu o jogo.")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, "👍 " + texto, "Resultado do Jogo", JOptionPane.INFORMATION_MESSAGE);
                        setBotoesHabilitados(false);
                    } else if (texto.toLowerCase().contains("jogo encerrado")) {
                        JOptionPane.showMessageDialog(TelaJogo.this, texto, "Jogo Encerrado", JOptionPane.INFORMATION_MESSAGE);
                        setBotoesHabilitados(false); 
                        dispose(); 
                    }
                    else { 
                        mensagemArea.append("\n" + texto);
                    }
                }
            }
        }.execute();
    }

    private void enviarJogada(int x, int y) {
        if (out != null && botoesTabuleiro[x][y].isEnabled()) { 
            try {
                out.writeObject(x + " " + y);
                out.flush();
                setBotoesHabilitados(false); 
            } catch (IOException e) {
                mensagemArea.append("\nErro ao enviar jogada: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

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

    private void setBotoesHabilitados(boolean habilitar) {
        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                botoesTabuleiro[i][j].setEnabled(habilitar);
            }
        }
    }
}