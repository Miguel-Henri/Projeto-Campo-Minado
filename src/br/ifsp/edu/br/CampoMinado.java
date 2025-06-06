package br.ifsp.edu.br;
import java.util.*;

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
        for (int i = 0; i < tamTabuleiro; i++) {
            for (int j = 0; j < tamTabuleiro; j++) {
                System.out.print(tabuleiroString[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int iniciarJogo() {
        Scanner leitor = new Scanner(System.in);
        int terminouJogo = 0;
        int x, y;

        do {
            System.out.println("\nTabuleiro atual:");
            mostrarTabuleiro();

            System.out.print("Escolha o valor de X (0 a " + (tamTabuleiro - 1) + "): ");
            x = leitor.nextInt();
            System.out.print("Escolha o valor de Y (0 a " + (tamTabuleiro - 1) + "): ");
            y = leitor.nextInt();

            if (x < 0 || x >= tamTabuleiro || y < 0 || y >= tamTabuleiro) {
                System.out.println("Posição inválida!");
                continue;
            }

            if (tabuleiroString[x][y].equals("*")) {
                terminouJogo = verificaBomba(x, y);
                if (terminouJogo == 0) {
                    tabuleiroString[x][y] = "0";
                }
            } else {
                System.out.println("Essa posição já foi escolhida!");
            }

            if (verificaVitoria()) {
                terminouJogo = 1;
            }

        } while (terminouJogo == 0);

        return terminouJogo;
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

    private boolean verificaVitoria() {
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
            System.out.println("\n💣 PERDEU! Você acertou uma bomba.");
        } else {
            System.out.println("\n🎉 GANHOU! Você evitou todas as bombas.");
        }
    }

   
   
}
