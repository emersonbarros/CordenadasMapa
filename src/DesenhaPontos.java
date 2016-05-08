/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author augustomeira
 */
public class DesenhaPontos {

    int[][][] figura;
    static final File dir =
            new File("");

    public void setPreto(int i, int j) {
        figura[i][j][0] = 0;
        figura[i][j][1] = 0;
        figura[i][j][2] = 0;
    }

    public void setBranco(int i, int j) {
        figura[i][j][0] = 255;
        figura[i][j][1] = 255;
        figura[i][j][2] = 255;
    }
    
    static List<No> nos = new ArrayList<No>();
    
  /*  public void linha(int x1, int y1, int x2, int x2){
        sqrt((x1-x2);
        //algoritmo de Bresenhan
        //draw line java
        //y=f(x)
        //Não precisa suavisar a reta
    
    Algoritimo de Kruskal
    1 - passo ordenar as arestas //Sort no Java Collections.sort
        implementar a interface comparable
        levar em conta o peso da aresta
        implementar o compareTo(other){
            return 0,1,-1
        }
    2 - minha solução T é um conjunto vazio
    3 - enquanto o n de arestas de T for menor que n-1
        remove a menor aresta da lista
        se essa menor aresta não forma ciclo {
            t=t união {e}
        }
    Saber se forma ciclo
    
        Estrutura de dados "Union-Find" busca e junção rápida
    
        Cada elemento é um apontador para ele mesmo.
    
        União(int u, int v){
            
        }
    
    }*/
    
    private static double calculaDistancia(No no1, No no2) {
        
        double lat1 = no1.getLatitude(); 
        double lng1 = no1.getLongitude();
        double lat2 = no2.getLatitude(); 
        double lng2 = no2.getLongitude();
        
        //double earthRadius = 3958.75;//miles
        double earthRadius = 6371;//kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist * 1000; //em metros
    }

    public void imprimePontos() {
        try {
            int xmax = Integer.MIN_VALUE;
            int ymax = Integer.MIN_VALUE;
            int xmin = Integer.MAX_VALUE;
            int ymin = Integer.MAX_VALUE;

            //primeira vez encontra os limites xmax, xmin, ymax, ymin
            //segunda vez imprime o arquivo ppm

            for (int vez = 0; vez < 2; vez++) {
                int dx = 0;
                int dy = 0;
                BufferedWriter out = null;
                if (vez == 1) {
                    dx = xmax - xmin + 1;
                    dy = ymax - ymin + 1;
                    //cada posicao da matrix eh uma coordenada x,y com tres
                    //dimensoes [x][y][0] [x][y][1] [x][y][2] para R-G-B
                    figura = new int[dx][dy][3];
                    out = new BufferedWriter(new FileWriter(new File( "aux.ppm")));
                    //cabecalho da figura ppm
                    //http://en.wikipedia.org/wiki/Netpbm_format#PPM_example
                    out.write("P3\n");
                    out.write(dy + " " + dx + "\n");
                    out.write("255\n");

                    //inicializa matriz quadriculada
                    for (int i = 0; i < dx; i++) {
                        for (int j = 0; j < dy; j++) {
                            if (i % 100 == 9 || j % 100 == 9) {
                                setPreto(i, j);
                            } else {
                                setBranco(i, j);
                            }

                        }
                    }

                }
                
                for (No no : nos) {
                    
                    double a1 = no.getLatitude();
                    double a2 = no.getLongitude();

                    //converte em inteiro com um fator de escala 30.
                    int x = (int) (a1 * 30);
                    int y = (int) (a2 * 30);

                    
                    if (vez == 0) {
                        //procura min  e max
                        if (x < xmin) {
                            xmin = x;
                        }
                        if (x > xmax) {
                            xmax = x;
                        }
                        if (y < ymin) {
                            ymin = y;
                        }
                        if (y > ymax) {
                            ymax = y;
                        }
                    } else {
                        //imprime latitude e longitude
                        setPreto(x - xmin, y - ymin);
                    }
                    assert (ymin <= ymax);
                }
                
                if (vez == 1) {
                    //imprime o arquivo.
                    for (int i = dx - 1; i >= 0; i--) {
                        for (int j = 0; j < dy; j++) {
                            out.write(figura[i][j][0] + " " +
                                    figura[i][j][1] + " " +
                                    figura[i][j][2] + " ");
                        }
                        out.write("\n");
                    }

                    out.write("\n");
                    out.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DesenhaPontos.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    //leitura de pontos. Exemplo.
    public void le(){
        File dir =
            new File("");
        BufferedReader b = null;
        try{
            b = new BufferedReader(
                    new FileReader(
                    new File("coordenadas.txt")));
            String linha = b.readLine();
            while (linha != null && linha.length() > 0) {
                String[] lista = linha.split(";");
                for(int i=0;i<lista.length;i++){
                    System.out.println(lista[i]+" ");
                }
                String estado = lista[2];
                double lati = Double.parseDouble(lista[3]);
                double longi = Double.parseDouble(lista[4]);

                nos.add(new No(estado, lati, longi));
                
                linha = b.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }       
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DesenhaPontos m = new DesenhaPontos();
        m.le();
        
        No no1 = nos.get(0);
        No no2 = nos.get(1);
        double dist = calculaDistancia(no1, no2);
        
        m.imprimePontos();
    }
}
