package br.ifsp.edu.br.controller;

import java.util.*;

public class CampoMinado {
    private int tamTabuleiro;
    private int numBombas;
    //logica
    private int[][] tabuleiro;
    //oq o jogador vê
    private String[][] tabuleiroString;
    private String posicaoBombas = "";

    public String getPosicaoBombas() {
        return posicaoBombas;
    }

    public CampoMinado(int tamTabuleiro, int numBombas) {
        this.tamTabuleiro = tamTabuleiro;
        this.numBombas = numBombas;
        tabuleiro = new int[tamTabuleiro][tamTabuleiro];
        tabuleiroString = new String[tamTabuleiro][tamTabuleiro];
        gerarTabuleiro();
        System.out.println("Bem-vindo ao Campo Minado!");
       
    }
    //gera o tabuleiro e coloca os campos sem bombas(com valor 0)
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
    
    //coloca as bombas(posicoes com 1) e armazena sua posicao(coordenada)
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

    //mostra tabuleiro visível 
    public void mostrarTabuleiro() {
        for (int i = 0; i < tamTabuleiro; i++) {
            for (int j = 0; j < tamTabuleiro; j++) {
                System.out.print(tabuleiroString[i][j] + " ");
            }
            System.out.println();
        }
    }

    //logica do jogo, jogadas, verificacao de bomba e vitoria
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

    
    //realiza jogada remota e retorna o status dela
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

    //retorna -1 se há bomba
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

    
    
    public int getTam() {
        return tamTabuleiro;
    }

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
    
    //verifica se as pocicoes sem a bomba ja foram todas abertas(condicao para vitoria)
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
            System.out.println("💣 PERDEU! Você acertou uma bomba.");
        } else {
            System.out.println("🎉 GANHOU! Você evitou todas as bombas.");
        }
    }
}
