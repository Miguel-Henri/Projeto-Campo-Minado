package br.ifsp.edu.br;
import java.util.*;

public class CampoMinado {
    private int TamTabuleiro = 7;
    private int[][] tabuleiro;
    private String[][] tabuleiroString;
    private String posicaoBombas = "";
    private int valorResultado;

    public String getPosicaoBombas() {
        return posicaoBombas;
    }

    public CampoMinado() {
        tabuleiro = new int[TamTabuleiro][TamTabuleiro];
        tabuleiroString = new String[TamTabuleiro][TamTabuleiro];
        gerarTabuleiro();
        mostrarTabuleiro();
        resultado(iniciarJogo());
    }

    public void gerarTabuleiro() {
        gerarBombas();
        for (int i = 0; i < TamTabuleiro; i++) {
            for (int j = 0; j < TamTabuleiro; j++) {
                if (tabuleiro[i][j] != 1) {
                    tabuleiro[i][j] = 0;
                }
                tabuleiroString[i][j] = "*";
            }
        }
    }

    private void gerarBombas() {
        int valorAleatorioX;
        int valorAleatorioY;
        int NumBombas = 5;
        Random aleatorio = new Random();
        for (int i = 0; i < NumBombas; i++) {
            valorAleatorioX = aleatorio.nextInt(7);
            valorAleatorioY = aleatorio.nextInt(7);
            if (tabuleiro[valorAleatorioX][valorAleatorioY] != 1) {
                tabuleiro[valorAleatorioX][valorAleatorioY] = 1;
                posicaoBombas = posicaoBombas + valorAleatorioX + ";" + valorAleatorioY + " ";
            } else {
                i--;
            }
        }
    }

    public void mostrarTabuleiro() {
        for (int i = 0; i < TamTabuleiro; i++) {
            for (int j = 0; j < TamTabuleiro; j++) {
                if (tabuleiroString[i][j] != null) {
                    System.out.print(tabuleiroString[i][j] + " ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }

    public int iniciarJogo() {
        Scanner leitor = new Scanner(System.in);
        int TerminouJogo = 0;
        int X, Y;

        do {
            System.out.println("Tabuleiro atual:");
            mostrarTabuleiro();

            System.out.println("Escolha o valor de X:");
            X = leitor.nextInt();
            System.out.println("Escolha o valor de Y:");
            Y = leitor.nextInt();

            if (X < 0 || X >= TamTabuleiro || Y < 0 || Y >= TamTabuleiro) {
                System.out.println("Posição inválida!");
                continue;
            }

            if (tabuleiroString[X][Y] != null) {
                TerminouJogo = verificaBomba(X, Y);
                if (TerminouJogo == 0) {
                    tabuleiroString[X][Y] = "0";
                }
            }

            if (terminouTabuleiro() == 0) {
                TerminouJogo = 1;
            }

        } while (TerminouJogo == 0);

        return TerminouJogo;
    }

    private int verificaBomba(int X, int Y) {
        int retorno = 0;
        String valores[] = posicaoBombas.trim().split(" ");
        for (String pos : valores) {
            String[] coord = pos.split(";");
            int valorX = Integer.parseInt(coord[0]);
            int valorY = Integer.parseInt(coord[1]);

            if (X == valorX && Y == valorY) {
                retorno = -1;
                break;
            }
        }
        return retorno;
    }

    private int terminouTabuleiro() {
        int retorno = 0;
        for (int i = 0; i < TamTabuleiro; i++) {
            for (int j = 0; j < TamTabuleiro; j++) {
                if (tabuleiroString[i][j] != null && !tabuleiroString[i][j].equals("*")) {
                    retorno = 1;
                    break;
                }
            }
        }
        return retorno;
    }

    private void resultado(int valor) {
        if (valor < 0) {
            System.out.println("PERDEU");
        } else {
            System.out.println("GANHOU");
        }
    }
}
