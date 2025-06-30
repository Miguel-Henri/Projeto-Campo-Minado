package br.ifsp.edu.br;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(Config.getIp(), Config.getPorta());
            System.out.println("Conectado ao servidor: " + socket.getInetAddress().getHostAddress());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Scanner teclado = new Scanner(System.in);

            boolean continuar = true;

            while (continuar) {
                Object mensagem = in.readObject();

                if (mensagem instanceof String) {
                    String texto = (String) mensagem;
                    System.out.println(texto);

                    if (texto.startsWith("Sua vez")) {
                        String jogada;
                        while (true) {
                            System.out.print("Digite coordenadas (x y): ");
                            jogada = teclado.nextLine().trim();

                            String[] partes = jogada.split("\\s+");
                            if (partes.length == 2) {
                                try {
                                    Integer.parseInt(partes[0]);
                                    Integer.parseInt(partes[1]);
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.println("❌ Por favor, digite dois números inteiros válidos.");
                                }
                            } else {
                                System.out.println("❌ Formato inválido. Use: número número (ex: 1 2)");
                            }
                        }

                        out.writeObject(jogada);
                        out.flush();
                    }

                    // Detectar pergunta sobre novo jogo
                    else if (texto.toLowerCase().contains("deseja jogar novamente")) {
                        System.out.print("Digite sua resposta (sim/nao): ");
                        String resposta = teclado.nextLine().trim().toLowerCase();
                        out.writeObject(resposta);
                        out.flush();
                    }

                    else if (texto.toLowerCase().contains("jogo encerrado")) {
                        continuar = false;
                    }
                }
            }

            socket.close();
            teclado.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
