package br.ifsp.edu.br.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import br.ifsp.edu.br.view.TelaJogo;

import br.ifsp.edu.br.network.Servidor;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class TelaInicial extends JFrame {

    public TelaInicial() {

        setTitle("Campo Minado");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        setLocationRelativeTo(null);

        // Ajuste no padding do painel principal
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("🚀 Campo Minado", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28)); // Fonte maior
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnCliente = new JButton("Iniciar como Cliente");
        btnCliente.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JButton btnServidor = new JButton("Iniciar como Servidor");
        btnServidor.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("SansSerif", Font.PLAIN, 16));

        botoes.add(btnCliente);
        botoes.add(btnServidor);
        botoes.add(btnSair);
        add(botoes, BorderLayout.CENTER);

        btnCliente.addActionListener(e -> iniciarCliente());
        btnServidor.addActionListener(e -> iniciarServidor());
        btnSair.addActionListener(e -> System.exit(0));

        pack();
        setVisible(true);
    }

    private void iniciarCliente() {
        // Inicia a GUI do cliente em uma nova Thread, mas na EDT (via SwingUtilities.invokeLater)
        // O trabalho de rede (conexão, leitura) será dentro da CampoMinadoClienteGUI,
        // usando SwingWorker para não travar a GUI.
        SwingUtilities.invokeLater(() -> {
            new TelaJogo();
            dispose(); // Fecha a tela inicial após abrir a tela do jogo
        });
    }

    private void iniciarServidor() {
        // Inicia o servidor em uma thread separada para não travar a GUI principal
        new Thread(() -> {
            try {
                br.ifsp.edu.br.network.Servidor.main(new String[]{});
            } catch (Exception ex) {
                // Exibe o erro na EDT
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Erro ao iniciar servidor: " + ex.getMessage(), "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                });
                ex.printStackTrace();
            }
        }).start();

        JOptionPane.showMessageDialog(this, "Servidor iniciado em segundo plano. Inicie os clientes.", "Servidor", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

}