package br.ifsp.edu.br.persistencia;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.time.LocalDate;
import java.util.List;


public class Historico {
    private static final String ARQUIVO = "historico.xml";
    
    public static void salvarGame(List<JogadorResultado> jogadores){
        try{
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fac.newDocumentBuilder();
            
            Document doc;
            Element caminho;
            
            File arq = new File(ARQUIVO);
            if(arq.exists()){
                doc = builder.parse(arq);
                caminho = doc.getDocumentElement();
            }else{
                doc = builder.newDocument();
                caminho = doc.createElement("partidas");
                doc.appendChild(caminho);
            }
            
            Element game = doc.createElement("partida");
            game.setAttribute("data", LocalDate.now().toString());
           
            
            for(JogadorResultado jogador : jogadores){
                Element j = doc.createElement("jogador");
                j.setAttribute("nome", jogador.nome);
                j.setAttribute("venceu", String.valueOf(jogador.venceu));
                game.appendChild(j);
            }
            
            caminho.appendChild(game);
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(arq));
            
            
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }
    
    
    
    
    public static class JogadorResultado{
        public String nome;
        public boolean venceu;
        
        public JogadorResultado(String nome, boolean venceu){
            this.nome=nome;
            this.venceu=venceu;
        }
    }
}
