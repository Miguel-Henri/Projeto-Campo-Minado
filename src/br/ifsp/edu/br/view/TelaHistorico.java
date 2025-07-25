package br.ifsp.edu.br.view;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TelaHistorico extends JFrame {
    
    public TelaHistorico(){
        setTitle("Histórico de Vitórias");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
        
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        JLabel titulo = new JLabel("  Histórico de Vitórias", SwingConstants.CENTER);
       titulo.setFont(new Font("Arial", Font.BOLD, 22));
       titulo.setForeground(new Color(33, 33, 33));
       add(titulo, BorderLayout.NORTH);
       
       
       JTextArea areaTxt = new JTextArea();
       areaTxt.setEditable(false);
       areaTxt.setFont(new Font("Monospaced", Font.PLAIN, 15));
       areaTxt.setBackground(new Color(245, 245, 245));
       areaTxt.setMargin(new Insets(10, 10, 10, 10));
       areaTxt.setLineWrap(true);
       areaTxt.setWrapStyleWord(true);
       
       JScrollPane scroll = new JScrollPane(areaTxt);
       scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
       add(scroll, BorderLayout.CENTER);
       
       JButton btnFechar = new JButton("Fechar");
       btnFechar.setFont(new Font("SansSerif", Font.PLAIN, 16));
       btnFechar.setFocusPainted(false);
       btnFechar.setBackground(new Color(230, 230, 250));
       btnFechar.setForeground(Color.BLACK);
       btnFechar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
       btnFechar.addActionListener(e -> dispose());
       
       JPanel painelInferior = new JPanel();
       painelInferior.add(btnFechar);
       add(painelInferior, BorderLayout.SOUTH);
       
       carregarHistorico(areaTxt);
       
       setVisible(true);
        
    }
    
    
    private void carregarHistorico(JTextArea areaTxt){
        File arq = new File("historico.xml");
                
        if(!arq.exists()){
            areaTxt.setText("Nenhum histórico disponível");
            return;
        }
        
        
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arq);
            doc.getDocumentElement().normalize();

            NodeList partidas = doc.getElementsByTagName("partida");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < partidas.getLength(); i++) {
                Element partida = (Element) partidas.item(i);
                String data = partida.getAttribute("data");

                sb.append("📅 Partida ").append(i + 1).append(" (").append(data).append("):\n");

                NodeList jogadores = partida.getElementsByTagName("jogador");
                for (int j = 0; j < jogadores.getLength(); j++) {
                    Element jogador = (Element) jogadores.item(j);
                    String nome = jogador.getAttribute("nome");
                    String venceu = jogador.getAttribute("venceu");

                    sb.append("  - ").append(nome);
                    if ("true".equalsIgnoreCase(venceu)) {
                        sb.append(" 🏆 (Vencedor)");
                    } else {
                        sb.append(" (Derrotado)");
                    }
                    sb.append("\n");
                }

                sb.append("\n");
            }

            areaTxt.setText(sb.toString());
            
        }catch(Exception e){
            areaTxt.setText("Erro ao carregar histórico do jogo" + e.getMessage());
            e.printStackTrace();
        }
            
        
        
    }
    
}
