package br.ifsp.edu.br.network;

import br.ifsp.edu.br.config.Config;
import br.ifsp.edu.br.controller.CampoMinado;
import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        System.out.println("Servidor Campo Minado Inicializado (" + servidor + ").");

        System.out.println("Esperando por Conexão (Jogador 1)...");
        Socket jogador1 = servidor.accept();
        System.out.println("Jogador 1 Conectado: " + jogador1.getInetAddress().getHostAddress());

        System.out.println("Esperando por Conexão (Jogador 2)...");
        Socket jogador2 = servidor.accept();
        System.out.println("Jogador 2 Conectado: " + jogador2.getInetAddress().getHostAddress());

        ObjectOutputStream out1 = new ObjectOutputStream(jogador1.getOutputStream());
        ObjectOutputStream out2 = new ObjectOutputStream(jogador2.getOutputStream());

        ObjectInputStream in1 = new ObjectInputStream(jogador1.getInputStream());
        ObjectInputStream in2 = new ObjectInputStream(jogador2.getInputStream());

        out1.writeObject("Bem-vindo ao Campo Minado - Jogador 1");
        out2.writeObject("Bem-vindo ao Campo Minado - Jogador 2");

        boolean continuarJogando = true;

        while (continuarJogando) {
            CampoMinado jogo = new CampoMinado(Config.getTamanhoTabuleiro(), Config.getNumBombas());
            boolean jogoRolando = true;
            int jogadorAtual = 1;

            while (jogoRolando) {
                ObjectOutputStream atualOut = (jogadorAtual == 1) ? out1 : out2;
                ObjectInputStream atualIn = (jogadorAtual == 1) ? in1 : in2;
                int terminouJogada = 0;
                int resultado = 0;

                do {
                    String tabuleiro = jogo.getTabuleiroString();
                    out1.writeObject("Tabuleiro:\n" + tabuleiro);
                    out2.writeObject("Tabuleiro:\n" + tabuleiro);

                    atualOut.writeObject("Sua vez. Informe coordenadas X e Y (separadas por espaço):");
                    String entrada = ((String) atualIn.readObject()).trim();
                    String[] partes = entrada.split(" ");

                    if (partes.length == 2) {
                        int x = Integer.parseInt(partes[0]);
                        
                        int y = Integer.parseInt(partes[1]);
                        if(x <= jogo.getTam() && y <= jogo.getTam()){
                            resultado = jogo.jogadaRede(x, y);
                            terminouJogada++;
                        }
                        
                    }
                } while (terminouJogada == 0);

                if (resultado == -1) {
                    atualOut.writeObject("Você acertou uma bomba! Fim de jogo.");
                    if (jogadorAtual == 1) {
                        out2.writeObject("O Jogador 1 perdeu. Você venceu!");
                    } else {
                        out1.writeObject("O Jogador 2 perdeu. Você venceu!");
                    }
                    jogoRolando = false;

                } else if (jogo.verificaVitoria()) {
                    atualOut.writeObject("Você venceu o jogo!");
                    if (jogadorAtual == 1) {
                        out2.writeObject("O Jogador 1 venceu o jogo.");
                    } else {
                        out1.writeObject("O Jogador 2 venceu o jogo.");
                    }
                    jogoRolando = false;

                } else {
                    jogadorAtual = (jogadorAtual == 1) ? 2 : 1;
                }
            }

          
            out1.writeObject("Você deseja jogar novamente? (sim/nao)");
            out2.writeObject("Você deseja jogar novamente? (sim/nao)");

            String resposta1 = ((String) in1.readObject()).trim().toLowerCase();
            String resposta2 = ((String) in2.readObject()).trim().toLowerCase();

            if (resposta1.equals("sim") && resposta2.equals("sim")) {
                continuarJogando = true;
                out1.writeObject("Novo jogo iniciando...");
                out2.writeObject("Novo jogo iniciando...");
            } else {
                continuarJogando = false;
                out1.writeObject("Jogo encerrado. Obrigado por jogar!");
                out2.writeObject("Jogo encerrado. Obrigado por jogar!");
            }
        }

        jogador1.close();
        jogador2.close();
      
    }
}
