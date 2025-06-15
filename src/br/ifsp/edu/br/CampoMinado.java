package br.ifsp.edu.br;
import java.util.*;

import javax.swing.JOptionPane;

public class CampoMinado {
    private int tamTabuleiro;
    private int numBombas;
    private int[][] tabuleiro;
    private String[][] tabuleiroString;
    private String posicaoBombas = "";

    public String getPosicaoBombas() {
        return posicaoBombas;
    }
    
    
    
    

    // Construtor com parâmetros
    public CampoMinado(int tamTabuleiro, int numBombas) {
        this.tamTabuleiro = tamTabuleiro;
        this.numBombas = numBombas;
        tabuleiro = new int[tamTabuleiro][tamTabuleiro];
        tabuleiroString = new String[tamTabuleiro][tamTabuleiro];
        gerarTabuleiro();
        JOptionPane.showMessageDialog(null, "Bem-vindo ao Campo Minado!");
        resultado(iniciarJogo());
    }
    
    
    
    

    public void gerarTabuleiro() {
        gerarBombas();
        for (int i = 0; i < tamTabuleiro; i++) {
            for (int j = 0; j < tamTabuleiro; j++) {
                if (tabuleiro[i][j] != 1) {
                    tabuleiro[i][j] = 0;
                }
                tabuleiroString[i][j] = "*";
            }
        }
    }
    
    
    

    private void gerarBombas() {
        Random aleatorio = new Random();
        int bombasColocadas = 0;

        while (bombasColocadas < numBombas) {
            int x = aleatorio.nextInt(tamTabuleiro);
            int y = aleatorio.nextInt(tamTabuleiro);

            if (tabuleiro[x][y] != 1) {
                tabuleiro[x][y] = 1;
                posicaoBombas += x + ";" + y + " ";
               
                bombasColocadas++;
            }
        }
    }

    
    
    
    
    
    public void mostrarTabuleiro() {
        StringBuilder tab = new StringBuilder();
        for (int i = 0; i < tamTabuleiro; i++) {
            for (int j = 0; j < tamTabuleiro; j++) {
                tab.append(tabuleiroString[i][j]).append(" ");
            }
            tab.append("\n");
        }
        JOptionPane.showMessageDialog(null, tab.toString(), "Tabuleiro Atual", JOptionPane.PLAIN_MESSAGE);
    }


    
    
    
    
    public int iniciarJogo() {
        Scanner leitor = new Scanner(System.in);
        int terminouJogo = 0;
        int x, y;

        do {
            System.out.println("\nTabuleiro atual:");
            mostrarTabuleiro();

            String entradaX = JOptionPane.showInputDialog("Escolha o valor de X (0 a " + (tamTabuleiro - 1) + "):");
            x = Integer.parseInt(entradaX);
            String entradaY = JOptionPane.showInputDialog("Escolha o valor de Y (0 a " + (tamTabuleiro - 1) + "):");
            y = Integer.parseInt(entradaY);

            if (x < 0 || x >= tamTabuleiro || y < 0 || y >= tamTabuleiro) {
            	JOptionPane.showMessageDialog(null, "Posição inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (tabuleiroString[x][y].equals("*")) {
                terminouJogo = verificaBomba(x, y);
                if (terminouJogo == 0) {
                    tabuleiroString[x][y] = "0";
                }
            } else {
            	JOptionPane.showMessageDialog(null, "Essa posição já foi escolhida!", "Aviso", JOptionPane.WARNING_MESSAGE);	
            }

            if (verificaVitoria()) {
                terminouJogo = 1;
            }

        } while (terminouJogo == 0);

        return terminouJogo;
    }
    
    
    
    
    
    //criada
    public int jogadaRede(int x, int y) {
        if (x < 0 || x >= tamTabuleiro || y < 0 || y >= tamTabuleiro) {
            return -2;
        }

        if (!tabuleiroString[x][y].equals("*")) {
            return -3; 
        }

        if (verificaBomba(x, y) == -1) {
            return -1; 
        } else {
            tabuleiroString[x][y] = "0"; 
            return 0; 
        }
    }

    
    
    
    private int verificaBomba(int x, int y) {
        String[] valores = posicaoBombas.trim().split(" ");
        for (String pos : valores) {
            String[] coord = pos.split(";");
            int bombaX = Integer.parseInt(coord[0]);
            int bombaY = Integer.parseInt(coord[1]);

            if (x == bombaX && y == bombaY) {
                return -1; 
            }
        }
        return 0;
    }
    
    
    //criada
    public int getTam() {
        return tamTabuleiro;
    }
    
    
    //criada
    public String getTabuleiroString() {
        StringBuilder tab = new StringBuilder();
        for (int i = 0; i < tamTabuleiro; i++) {
            for (int j = 0; j < tamTabuleiro; j++) {
                tab.append(tabuleiroString[i][j]).append(" ");
            }
            tab.append("\n");
        }
        return tab.toString();
    }
    
    
    
    
    

    public boolean verificaVitoria() {
        int totalEspacos = tamTabuleiro * tamTabuleiro;
        int espacosDescobertos = 0;

        for (int i = 0; i < tamTabuleiro; i++) {
            for (int j = 0; j < tamTabuleiro; j++) {
                if (!tabuleiroString[i][j].equals("*")) {
                    espacosDescobertos++;
                }
            }
        }

        return espacosDescobertos == (totalEspacos - numBombas);
    }

    
    
    
    
    private void resultado(int valor) {
        if (valor < 0) {
        	JOptionPane.showMessageDialog(null, "💣 PERDEU! Você acertou uma bomba.", "Fim de jogo", JOptionPane.ERROR_MESSAGE);
        } else {
        	JOptionPane.showMessageDialog(null, "🎉 GANHOU! Você evitou todas as bombas.", "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

   
   
}
