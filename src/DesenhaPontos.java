
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author augustomeira
 */
public class DesenhaPontos {

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
	public void linha(int x, int y, int x2, int y2) {
		int w = x2 - x;
		int h = y2 - y;
		double m = h / (double) w;
		double j = y;
		for (int i = x; i <= x2; i++) {
			setPreto(i, (int) j);
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

	private static Float calculaDistancia(No no1, No no2) {

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
		Float dist = new Float(earthRadius * c);

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

				for (No no : allNos) {

					double a1 = no.getLatitude();
					double a2 = no.getLongitude();
					double b1 = -1;
					double b2 = -1;

					No no2 = null;
					List<Aresta> nosLigados = arestas.values().stream().filter(p -> p.getNo1().getId() == no.getId())
							.collect(Collectors.toList());
					if (nosLigados.size() > 0) {
						no2 = nosLigados.get(0).getNo2();
						b1 = no2.getLatitude();
						b2 = no2.getLongitude();
					}

					// converte em inteiro com um fator de escala 30.
					int x = (int) (a1 * 30);
					int y = (int) (a2 * 30);

					int x2 = (int) (b1 * 30);
					int y2 = (int) (b2 * 30);

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

						// procura min e max
						if (x2 < xmin) {
							xmin = x2;
						}
						if (x2 > xmax) {
							xmax = x2;
						}
						if (y2 < ymin) {
							ymin = y2;
						}
						if (y2 > ymax) {
							ymax = y2;
						}
					} else {
						// imprime latitude e longitude
						setPreto(x - xmin, y - ymin);

						if (b1 != -1 && b2 != -1)
							linha(x - xmin, y - ymin, x2 - xmin, y2 - ymin);
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

	public void carregaNos() {

		BufferedReader b = null;
		try {
			b = new BufferedReader(new FileReader(new File("coordenadas.txt")));
			String linha = b.readLine();
			int id = 0;
			while (linha != null && linha.length() > 0) {
				String[] lista = linha.split(";");
				String cidade = lista[0];
				String estado = fixEstado(lista[2]);
				Float lati = Float.parseFloat(lista[3]);
				Float longi = Float.parseFloat(lista[4]);

				No no = new No(++id, cidade, estado, lati, longi);
				allNos.add(no);
				allEstados.add(estado);
				linha = b.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final ReentrantLock lock = new ReentrantLock();
	private static HashMap<String, Integer> ix = new HashMap<String, Integer>();

	private static int armazenados = 0;
	private static int descartados = 0;

	private static Map<String, ExecutorService> threads = new HashMap<String, ExecutorService>();

	/**
	 * @param args
	 *            the command line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		DesenhaPontos m = new DesenhaPontos();
		m.carregaNos();

		processaEstados();

		m.imprimePontos();

	}

	private static void processaEstados() {

		for (String estado : allEstados) {
			try {
				lock.lock();
				if (ix.get(estado) == null)
					ix.put(estado, 0);
			} finally {
				lock.unlock();
			}

			threads.put(estado, Executors.newSingleThreadExecutor());

			threads.get(estado).submit(() -> {
				System.out.println("Iniciando: " + estado);
				processaEstado(estado);
			});

		}

		for (String estado : allEstados) {

			threads.get(estado).shutdown();
			try {
				threads.get(estado).awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Finalizando: " + estado + " Armazenadas: " + armazenados + " Descartadas: "
					+ descartados + " Total: " + (armazenados + descartados));
		}
	}

	private static Map<String, Aresta> arestas = new HashMap<String, Aresta>();

	private static void processaEstado(String estado) {

		List<No> cidades = allNos.stream().filter(p -> p.getEstado().equals(estado)).collect(Collectors.toList());

		for (; ix.get(estado) < cidades.size();) {

			List<Aresta> arestasCidade;
			lock.lock();
			try {
				arestasCidade = new ArrayList<Aresta>();
				ix.put(estado, ix.get(estado) + 1);
			} finally {
				lock.unlock();
			}

			/*
			 * List<No> nos = (List<No>) allNos.stream() .filter(p ->
			 * p.getLatitude() >= allNos.get(ix.get(estado)).getLatitude() &&
			 * p.getLongitude() >= allNos.get(ix.get(estado)).getLongitude())
			 * .collect(Collectors.toList());
			 */
			for (No n2 : allNos) {

				if (allNos.get(ix.get(estado)).getId() != n2.getId()) {

					if (verificaFronteira(allNos.get(ix.get(estado)), n2)) {
						Float dist = calculaDistancia(allNos.get(ix.get(estado)), n2);
						Aresta a = new Aresta(++armazenados, allNos.get(ix.get(estado)), n2, dist);
						arestasCidade.add(a);
					} else {
						lock.lock();
						try {
							descartados++;
						} finally {
							lock.unlock();
						}
					}
				}
			}
			Collections.sort(arestasCidade);
			for (int i = 0; i < arestasCidade.size(); i++) {
				Aresta a = arestasCidade.get(i);
				if (a.getDistancia() > 2000) {
					System.out.println("Cidade inconsistente: " + a.toString());
				}
				if (arestas.get(a.getKey()) != null) {
					continue;
				} else {
					arestas.put(a.getKey(), a);
					break;
				}
			}
		}

	}

	private static Set<String> allEstados = new HashSet<String>();
	private static List<No> allNos = new ArrayList<No>();

	private static boolean verificaFronteira(No no1, No no2) {
		try {
			if (no1.getEstado().equals(no2.getEstado()))
				return true;
			return Arrays.asList(getFronteiras(no1)).contains(no2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String fixEstado(String estado) {
		switch (estado) {
		case "M":
			return "MS";
		case "N":
			return "GO";
		}
		return estado;
	}

	private static String[] getFronteiras(No no) throws Exception {
		switch (no.getEstado()) {
		case "SP":
			return new String[] { "RJ", "PR", "MS", "MG" };
		case "RJ":
			return new String[] { "SP", "MG", "ES" };
		case "ES":
			return new String[] { "BA", "MG", "RJ" };
		case "MG":
			return new String[] { "SP", "MS", "GO", "ES", "BA" };
		case "PR":
			return new String[] { "SP", "SC", "MS" };
		case "SC":
			return new String[] { "SP", "SC", "MS" };
		case "RS":
			return new String[] { "SC" };
		case "AM":
			return new String[] { "AC", "RR", "AP", "PA", "MT" };
		case "MS":
			return new String[] { "GO", "MT", "SP", "PR" };
		case "AP":
			return new String[] { "PA" };
		case "PA":
			return new String[] { "MA", "TO", "MT", "AM", "RR" };
		case "MT":
			return new String[] { "RO", "AM", "PA", "TO", "GO", "MS" };
		case "GO":
			return new String[] { "MT", "TO", "BA", "MG", "MS" };
		case "AL":
			return new String[] { "PE", "SE", "BA" };
		case "PE":
			return new String[] { "PB", "CE", "AL", "SE" };
		case "AC":
			return new String[] { "AM" };
		case "CE":
			return new String[] { "PI", "PE", "PB", "RN" };
		case "MA":
			return new String[] { "PA", "PI", "TO" };
		case "BA":
			return new String[] { "TO", "PI", "SE", "AL", "PE", "MG", "ES" };
		case "RO":
			return new String[] { "AM", "PA" };
		case "RN":
			return new String[] { "CE", "PB", "SE", "AL" };
		case "TO":
			return new String[] { "PA", "MT", "MA", "PI", "BA", "MG", "GO" };
		case "PI":
			return new String[] { "MA", "CE", "PE", "BA", "TO" };
		case "RR":
			return new String[] { "AM" };
		case "PB":
			return new String[] { "RN", "CE", "PE" };
		case "SE":
			return new String[] { "AL", "BA" };
		case "DF":
			return new String[] { "GO" };
		default:
			break;
		}
		throw new Exception("Estado: " + no.getEstado() + " - " + no.getCidade() + " não encontrado.");
	}
}
