package br.ifsp.edu.br;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.out;
import java.net.*;

public class Servidor{
    
    public static void main(String[] args) throws Exception{
        ServerSocket servidor;
        servidor = new ServerSocket( Config.getPorta(), 2, InetAddress.getByName(Config.getIp() ) );
        System.out.println("Servidor Campo Minado Inicializado ( " + servidor + " ).\n");
        
        
        Socket jogador1;        
        System.out.println( "Esperando por Conexão (Jogador 1)." );
        jogador1 =  servidor.accept();
        System.out.println( "Conexão Recebida: " + jogador1.toString() + ":" + jogador1.getPort() + "\n" );
    }
   
    
}

