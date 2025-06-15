package br.ifsp.edu.br;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class Servidor {

    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        JOptionPane.showMessageDialog(null, "Servidor Campo Minado Inicializado (" + servidor + ").");

        Socket jogador1, jogador2;

        JOptionPane.showMessageDialog(null, "Esperando por Conexão (Jogador 1).");
        jogador1 = servidor.accept();
        JOptionPane.showMessageDialog(null, "Jogador 1 Conectado: " + jogador1.getInetAddress().getHostAddress());

        JOptionPane.showMessageDialog(null, "Esperando por Conexão (Jogador 2).");
        jogador2 = servidor.accept();
        JOptionPane.showMessageDialog(null, "Jogador 2 Conectado: " + jogador2.getInetAddress().getHostAddress());

        ObjectOutputStream out1 = new ObjectOutputStream(jogador1.getOutputStream());
        ObjectOutputStream out2 = new ObjectOutputStream(jogador2.getOutputStream());

        ObjectInputStream in1 = new ObjectInputStream(jogador1.getInputStream());
        ObjectInputStream in2 = new ObjectInputStream(jogador2.getInputStream());

        CampoMinado jogo = new CampoMinado(
            Config.getTamanhoTabuleiro(),
            Config.getNumBombas()
        );

        out1.writeObject("Bem-vindo ao Campo Minado - Jogador 1");
        out2.writeObject("Bem-vindo ao Campo Minado - Jogador 2");

        boolean jogoRolando = true;
        int jogadorAtual = 1;

        while (jogoRolando == true) {
            ObjectOutputStream atualOut = (jogadorAtual == 1) ? out1 : out2;
            ObjectInputStream atualIn = (jogadorAtual == 1) ? in1 : in2;

            atualOut.writeObject("Sua vez. Informe coordenadas X e Y (separadas por espaço):");
            String entrada = ((String) atualIn.readObject()).trim();
            String[] partes = entrada.split(" ");
            int x = Integer.parseInt(partes[0]);
            int y = Integer.parseInt(partes[1]);

            if (x < 0 || x >= jogo.getTam() || y < 0 || y >= jogo.getTam()) {
                atualOut.writeObject("Coordenadas inválidas. Tente novamente.");
                continue;
            }

            int resultado = jogo.jogadaRede(x, y);

            if (resultado == -1) {
                atualOut.writeObject("Você acertou uma bomba! Fim de jogo.");

                if (jogadorAtual == 1) {
                    out2.writeObject("O Jogador 1 perdeu. Você venceu!");
                } else {
                    out1.writeObject("O Jogador 2 perdeu. Você venceu!");
                }

                jogoRolando = false;
                JOptionPane.showMessageDialog(null, "Jogador " + jogadorAtual + " acertou uma bomba. Fim de jogo.");

            } else if (jogo.verificaVitoria()) {
                atualOut.writeObject("Você venceu o jogo!");

                if (jogadorAtual == 1) {
                    out2.writeObject("O Jogador 1 venceu o jogo.");
                } else {
                    out1.writeObject("O Jogador 2 venceu o jogo.");
                }

                jogoRolando = false;
                JOptionPane.showMessageDialog(null, "Jogador " + jogadorAtual + " venceu o jogo!");

            } else {
                String tabuleiro = jogo.getTabuleiroString();

                out1.writeObject("Tabuleiro:\n" + tabuleiro);
                out2.writeObject("Tabuleiro:\n" + tabuleiro);

                if (jogadorAtual == 1) {
                    jogadorAtual = 2;
                } else {
                    jogadorAtual = 1;
                }

                JOptionPane.showMessageDialog(null, "Turno do jogador " + jogadorAtual);
            }
        }

        jogador1.close();
        jogador2.close();
        servidor.close();
    }
}
