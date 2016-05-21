
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author augustomeira
 */
public class DesenhaPontos {

	static List<No> nos = new ArrayList<No>();

	int[][][] figura;
	static final File dir = new File("");

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

	/*
	 * public void linha(int x1, int y1, int x2, int x2){ sqrt((x1-x2);
	 * //algoritmo de Bresenhan //draw line java //y=f(x) //Não precisa suavisar
	 * a reta
	 */
	public void line(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		double m = h / (double) w;
		double j = y;
		for (int i = x; i <= x2; i++) {
			// putpixel(i,(int)j,color) ;
			j += m;
		}
	}

	/*
	 * Algoritimo de Kruskal 1 - passo ordenar as arestas //Sort no Java
	 * Collections.sort implementar a interface comparable levar em conta o peso
	 * da aresta implementar o compareTo(other){ return 0,1,-1 } 2 - minha
	 * solução T é um conjunto vazio 3 - enquanto o n de arestas de T for menor
	 * que n-1 remove a menor aresta da lista se essa menor aresta não forma
	 * ciclo { t=t união {e} } Saber se forma ciclo
	 * 
	 * Estrutura de dados "Union-Find" busca e junção rápida
	 * 
	 * Cada elemento é um apontador para ele mesmo.
	 * 
	 * União(int u, int v){
	 * 
	 * }
	 * 
	 * }
	 */

	private static double calculaDistancia(No no1, No no2) {

		double lat1 = no1.getLatitude();
		double lng1 = no1.getLongitude();
		double lat2 = no2.getLatitude();
		double lng2 = no2.getLongitude();

		// double earthRadius = 3958.75;//miles
		double earthRadius = 6371;// kilometers
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2)
				+ Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}

	public void imprimePontos() {
		try {
			int xmax = Integer.MIN_VALUE;
			int ymax = Integer.MIN_VALUE;
			int xmin = Integer.MAX_VALUE;
			int ymin = Integer.MAX_VALUE;

			// primeira vez encontra os limites xmax, xmin, ymax, ymin
			// segunda vez imprime o arquivo ppm

			for (int vez = 0; vez < 2; vez++) {
				int dx = 0;
				int dy = 0;
				BufferedWriter out = null;
				if (vez == 1) {
					dx = xmax - xmin + 1;
					dy = ymax - ymin + 1;
					// cada posicao da matrix eh uma coordenada x,y com tres
					// dimensoes [x][y][0] [x][y][1] [x][y][2] para R-G-B
					figura = new int[dx][dy][3];
					out = new BufferedWriter(new FileWriter(new File("aux.ppm")));
					// cabecalho da figura ppm
					// http://en.wikipedia.org/wiki/Netpbm_format#PPM_example
					out.write("P3\n");
					out.write(dy + " " + dx + "\n");
					out.write("255\n");

					// inicializa matriz quadriculada
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

					// converte em inteiro com um fator de escala 30.
					int x = (int) (a1 * 30);
					int y = (int) (a2 * 30);

					if (vez == 0) {
						// procura min e max
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
						// imprime latitude e longitude
						setPreto(x - xmin, y - ymin);
					}
					assert (ymin <= ymax);
				}

				if (vez == 1) {
					// imprime o arquivo.
					for (int i = dx - 1; i >= 0; i--) {
						for (int j = 0; j < dy; j++) {
							out.write(figura[i][j][0] + " " + figura[i][j][1] + " " + figura[i][j][2] + " ");
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

	// leitura de pontos. Exemplo.
	public void le() {
		BufferedReader b = null;
		try {
			b = new BufferedReader(new FileReader(new File("coordenadas.txt")));
			String linha = b.readLine();
			int id = 0;
			while (linha != null && linha.length() > 0) {
				String[] lista = linha.split(";");
				for (int i = 0; i < lista.length; i++) {
					System.out.println(lista[i] + " ");
				}
				String cidade = lista[0];
				String estado = lista[2];
				double lati = Double.parseDouble(lista[3]);
				double longi = Double.parseDouble(lista[4]);

				nos.add(new No(++id, cidade, estado, lati, longi));

				linha = b.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int ix = 0;
	private static int jx = 0;
	private static int armazenados = 0;
	private static int descartados = 0;

	/**
	 * @param args
	 *            the command line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		DesenhaPontos m = new DesenhaPontos();
		m.le();

		nos.stream().sorted(Comparator.comparing(No::getEstado));

		/*
		 * Map<String, ArrayList<No>> estados = new HashMap<String,
		 * ArrayList<No>>(); for (No n1 : nos) { if
		 * (!estados.containsKey(n1.getEstado())) { estados.put(n1.getEstado(),
		 * new ArrayList<No>()); estados.get(n1.getEstado()).add(n1); }
		 * estados.get(n1.getEstado()).add(n1); }
		 * 
		 * 
		 * 
		 * for (String estado : estados.keySet()) {
		 * 
		 * }
		 */

		// ExecutorService es = Executors.newFixedThreadPool(1);

		if (!Files.exists(Paths.get("distancias")))
			Files.createDirectory(Paths.get("distancias"));

		for (No n : nos) {
			fixEstado(n);
		}

		Path pathStatus = Paths.get("distancias/status.txt");
		if (Files.exists(pathStatus)) {
			List<String> line = Files.readAllLines(pathStatus);
			if (line.size() > 0) {
				String[] status = line.get(line.size() - 1).split(",");
				ix = Integer.parseInt(status[0]);
				jx = Integer.parseInt(status[1]);
			}
		} else {
			Files.createFile(pathStatus);
		}

		for (; ix < nos.size(); ix++) {
			No n1 = nos.get(ix);

			Path path = Paths.get("distancias/coordenadas-" + n1.getEstado() + ".txt");
			if (!Files.exists(path))
				Files.createFile(path);

			// es.submit(() -> {
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.WRITE)) {
				for (; jx < nos.size(); jx++) {
					No n2 = nos.get(jx);

					if (n1.getId() != n2.getId()) {
						try {
							if (verificaFronteira(n1, n2)) {
								double dist = calculaDistancia(n1, n2);
								Aresta a = new Aresta();
								a.setId(++armazenados);
								a.setNo1(n1);
								a.setNo2(n2);
								a.setDistancia(dist);
								writer.write(a.toString());
							} else {
								descartados++;
							}
						} catch (Exception e) {
							descartados++;
						}
					}
					try (BufferedWriter writerStatus = Files.newBufferedWriter(pathStatus, StandardOpenOption.WRITE)) {
						writerStatus.write(ix + "," + jx);
						System.out.println("" + ix + "|" + jx);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				jx = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
			// });

			System.out.println("Armazenadas: " + armazenados + " Descartadas: " + descartados + " Total: "
					+ (armazenados + descartados));
			System.gc();
		}
		ix = 0;
		
		m.imprimePontos();

		/*
		 * ArrayList<Aresta> arestas = new ArrayList<Aresta>(); for (No n1 :
		 * nos) {
		 * 
		 * if (!estados.contains(n1.getEstado())) { estados.add(n1.getEstado());
		 * }
		 * 
		 * es.submit(() -> { for (No n2 : nos) { if (n1.getId() != n2.getId()) {
		 * try { if (verificaFronteira(n1, n2)) { double dist =
		 * calculaDistancia(n1, n2); if (dist < 300) { Aresta a = new Aresta();
		 * a.setNo1(n1); a.setNo1(n2); a.setDistancia(dist); arestas.add(a); }
		 * else descartados++; } else { descartados++; } } catch (Exception e) {
		 * descartados++; } } } System.out.println(arestas.size() + " + " +
		 * descartados + " Total:" + (arestas.size() + descartados)); });
		 * 
		 * }
		 */

	}

	private static void fixEstado(No n) {
		switch (n.getEstado()) {
		case "M":
			n.setEstado("MS");
			break;
		case "N":
			n.setEstado("GO");
			break;
		default:
			break;
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private static boolean verificaFronteira(No no1, No no2) throws Exception {
		if (no1.getEstado().equals(no2.getEstado()))
			return true;
		return Arrays.asList(getFronteiras(no1)).contains(no2);
	}

	private static String[] getFronteiras(No no) throws Exception {
		switch (no.getEstado()) {
		case "SP":
			return new String[] { "RJ", "PR", "MS", "MG" };
		/*
		 * case "RJ": return new String[] { "SP", "MG", "ES" }; case "ES":
		 * return new String[] { "BA", "MG", "RJ" }; case "MG": return new
		 * String[] { "SP", "MS", "GO", "ES", "BA" }; case "PR": return new
		 * String[] { "SP", "SC", "M" }; case "SC": return new String[] { "SP",
		 * "SC", "M" }; case "RS": return new String[] { "SC" }; case "AM":
		 * return new String[] { "AC", "RR", "AP", "PA", "MT" }; case "M":
		 * return new String[] { "GO", "MT", "SP", "PR" };
		 */
		default:
			break;
		}
		throw new Exception("Estado: " + no.getEstado() + " - " + no.getCidade() + " não encontrado.");
	}
}
