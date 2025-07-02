package br.ifsp.edu.br.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import br.ifsp.edu.br.controller.CampoMinado;
        
        
public class TelaInicial extends JFrame {
    
    
    public TelaInicial(){
        
        
        setTitle("Campo Minado");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JLabel titulo = new JLabel("🚀 Campo Minado", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);
        
        JPanel botoes = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton btnCliente = new JButton("Iniciar como Cliente");
        JButton btnServidor = new JButton("Iniciar como Servidor");
        JButton btnLocal = new JButton("Jogar Localmente"); 
        JButton btnSair = new JButton("Sair");
        
        botoes.add(btnCliente);
        botoes.add(btnServidor);
        botoes.add(btnSair);
        add(botoes, BorderLayout.CENTER);
        
       
        
        
        btnCliente.addActionListener(e -> iniciarCliente());
        btnServidor.addActionListener(e -> iniciarServidor());
        btnSair.addActionListener(e -> System.exit(0));
        
        setVisible(true);
    }
    
    
     private void iniciarCliente() {
        new Thread(() -> {
            try {
                br.ifsp.edu.br.network.Cliente.main(new String[]{});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao iniciar cliente: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }

    private void iniciarServidor() {
        new Thread(() -> {
            try {
                br.ifsp.edu.br.network.Servidor.main(new String[]{});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao iniciar servidor: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }
}
