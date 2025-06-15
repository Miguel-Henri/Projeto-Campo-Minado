package br.ifsp.edu.br;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class Cliente {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(Config.getIp(), Config.getPorta());
            JOptionPane.showMessageDialog(null, "Conectado ao servidor: " + socket);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            boolean jogoRolando = true;

            while (jogoRolando) {
                Object msg = in.readObject();
                if (msg instanceof String) {
                    String texto = (String) msg;
                    JOptionPane.showMessageDialog(null, texto);

                    if (texto.toLowerCase().contains("informe coordenadas")) {
                        String entrada = JOptionPane.showInputDialog("Digite as coordenadas X e Y separadas por espaço:");
                        out.writeObject(entrada);
                    }


                    if (texto.toLowerCase().contains("fim de jogo") 
                        || texto.toLowerCase().contains("venceu")) {
                    	jogoRolando = false;
                    }
                }
            }

            socket.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na conexão: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
