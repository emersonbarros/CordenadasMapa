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
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

	private int dy = 0;
	private int dx = 0;

	public void linha(int x1, int y1, int x2, int y2) {

		int d = 0;

		int dy = Math.abs(y2 - y1);
		int dx = Math.abs(x2 - x1);

		int dy2 = (dy << 1);
		int dx2 = (dx << 1);

		int ix = x1 < x2 ? 1 : -1;
		int iy = y1 < y2 ? 1 : -1;
		
		if (dy <= dx) {
			for (;;) {
				setPreto(x1 - xmin, y1 - ymin);
				if (x1 == x2)
					break;
				x1 += ix;
				d += dy2;
				if (d > dx) {
					y1 += iy;
					d -= dx2;
				}
			}
		} else {
			for (;;) {
				setPreto(x1 - xmin, y1 - ymin);
				if (y1 == y2)
					break;
				y1 += iy;
				d += dx2;
				if (d > dy) {
					x1 += ix;
					d -= dy2;
				}
			}
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

	public static Float calculaDistancia(No no1, No no2) {

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

	private BufferedWriter out = null;

	public void inicializaArquivo(String arquivo) {
		try {
			xmax = Integer.MIN_VALUE;
			ymax = Integer.MIN_VALUE;
			xmin = Integer.MAX_VALUE;
			ymin = Integer.MAX_VALUE;
			dx = 0;
			dy = 0;

			// primeira vez encontra os limites xmax, xmin, ymax, ymin
			// segunda vez imprime o arquivo ppm

			for (int vez = 0; vez < 2; vez++) {

				if (vez == 1) {
					dx = xmax - xmin + 1;
					dy = ymax - ymin + 1;
					// cada posicao da matrix eh uma coordenada x,y com tres
					// dimensoes [x][y][0] [x][y][1] [x][y][2] para R-G-B
					figura = new int[dx][dy][3];
					out = new BufferedWriter(new FileWriter(new File(arquivo)));
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

				// coordenadas.
				BufferedReader b = new BufferedReader(new FileReader(new File("coordenadas.txt")));

				// leitura
				String linha = b.readLine();

				while (linha != null && linha.length() > 0) {
					String[] lista = linha.split(";");

					Float a1 = Float.parseFloat(lista[3]);
					Float a2 = Float.parseFloat(lista[4]);

					// converte em inteiro com um fator de escala 30.
					int x = (int) (a1 * 30);
					int y = (int) (a2 * 30);

					if (vez == 0) {
						// procura min e max
						if (x < xmin)
							xmin = x;
						if (x > xmax)
							xmax = x;
						if (y < ymin)
							ymin = y;
						if (y > ymax)
							ymax = y;
					}
					assert (xmin <= xmax);
					assert (ymin <= ymax);

					linha = b.readLine();
				}
			}

			// vertices = new ArrayList<Vertice>(total);
		} catch (IOException ex) {
			Logger.getLogger(DesenhaPontos.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void imprimePontos() throws IOException {
		for (int i = dx - 1; i >= 0; i--) {
			for (int j = 0; j < dy; j++) {
				out.write(figura[i][j][0] + " " + figura[i][j][1] + " " + figura[i][j][2] + " ");
			}
			out.write("\n");
		}
	}

	static int xmax = Integer.MIN_VALUE;
	static int ymax = Integer.MIN_VALUE;
	static int xmin = Integer.MAX_VALUE;
	static int ymin = Integer.MAX_VALUE;

	public void carregaNos() {
		allEstados = new HashSet<String>();
		allNos = new ArrayList<No>();

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

	/**
	 * @param args
	 *            the command line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		DesenhaPontos m = new DesenhaPontos();

		m.processaEstados();
	}

	private void processaEstados() {

		carregaNos();
		try {
			inicializaArquivo("aux3.ppm");
			encontraCaminhoDijkstra();
			imprimePontos();
			finalizaArquivo();
		} catch (Exception e) {
			e.printStackTrace();
		}

		carregaNos();
		try {
			inicializaArquivo("aux2.ppm");
			for (String estado : allEstados) {
				encontraMST(estado);

			}
			imprimePontos();
			finalizaArquivo();
		} catch (Exception e) {
			e.printStackTrace();
		}

		carregaNos();
		try {
			inicializaArquivo("aux1.ppm");
			for (String estado : allEstados) {

				try {
					lock.lock();
					if (ix.get(estado) == null)
						ix.put(estado, 0);
				} finally {
					lock.unlock();
				}
				processaEstado(estado);

			}
			imprimePontos();
			finalizaArquivo();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void encontraCaminhoDijkstra() {
		List<Integer> list = Dijkstras.getDijkstra(this.allNos);
		List<No> result = new ArrayList<No>();
		for (int i = 0; i < list.size() - 1; i++) {
			int line = i;
			result.add(allNos.stream().filter(p -> p.getId() == list.get(line)).limit(1).collect(Collectors.toList())
					.get(0));
		}

		for (int i = 0; i < result.size() - 1; i++) {

			No no1 = result.get(i);
			No no2 = result.get(i + 1);

			int lat1 = (int) (no1.getLatitude() * 30);
			int lon1 = (int) (no1.getLongitude() * 30);

			int lat2 = (int) (no2.getLatitude() * 30);
			int lon2 = (int) (no2.getLongitude() * 30);

			linha(lat1, lon1, lat2, lon2);

			System.out.println(no1);
			System.out.println(no2);
		}

	}

	private void finalizaArquivo() throws IOException {
		out.write("\n");
		out.close();
	}

	private Map<String, Aresta> arestas = new HashMap<String, Aresta>();

	private void processaEstado(final String estado) throws Exception {

		List<No> cidades = buscaNosPorEstado(estado);

		for (; ix.get(estado) < cidades.size();) {

			List<Aresta> arestasCidade = new ArrayList<Aresta>();

			for (No n2 : allNos) {
				No n1 = cidades.get(ix.get(estado));
				if (verificaFronteira(n1, n2)) {
					Float dist = calculaDistancia(n1, n2);
					lock.lock();
					try {
						Aresta a = new Aresta(++armazenados, n1, n2, dist);
						arestasCidade.add(a);
					} finally {
						lock.unlock();
					}
				}
			}

			lock.lock();
			try {
				ix.put(estado, ix.get(estado) + 1);
			} finally {
				lock.unlock();
			}

			Collections.sort(arestasCidade);
			if (arestasCidade.size() == 0) {
				No no = cidades.get(ix.get(estado));
				System.out.println("Não existe ligação para a cidade: " + no.getCidade() + " - " + no.getEstado());
			} else {
				int lat1 = (int) (arestasCidade.get(0).getNo1().getLatitude() * 30);
				int lon1 = (int) (arestasCidade.get(0).getNo1().getLongitude() * 30);

				int lat2 = (int) (arestasCidade.get(0).getNo2().getLatitude() * 30);
				int lon2 = (int) (arestasCidade.get(0).getNo2().getLongitude() * 30);

				linha(lat1, lon1, lat2, lon2);
			}

			boolean temAresta = false;
			for (int i = 0; i < arestasCidade.size(); i++) {
				Aresta a = arestasCidade.get(i);
				if (arestas.get(a.getKey()) != null) {
					continue;
				} else {
					arestas.put(a.getKey(), a);
					temAresta = true;
					break;
				}
			}
			if (!temAresta)
				System.out.println(cidades.get(ix.get(estado)));
		}

	}

	public void encontraMST(String estado) throws Exception {

		List<Aresta> arestas = new ArrayList<Aresta>();

		List<No> cidades = buscaNosPorEstado(estado);

		for (int i = 0; i < cidades.size(); i++) {
			for (int j = 0; j < cidades.size(); j++) {

				if (i == j)
					continue;

				No a = cidades.get(i);
				No b = cidades.get(j);

				Aresta aresta = new Aresta(++armazenados, a, b, calculaDistancia(a, b));
				arestas.add(aresta);
			}
		}

		Collections.sort(arestas);

		List<Aresta> tree = Kruskal.getInstance().generateMST(arestas);

		for (int i = 0; i < tree.size(); i++) {
			Aresta a = tree.get(i);

			int lat1 = (int) (a.getNo1().getLatitude() * 30);
			int lon1 = (int) (a.getNo1().getLongitude() * 30);

			int lat2 = (int) (a.getNo2().getLatitude() * 30);
			int lon2 = (int) (a.getNo2().getLongitude() * 30);

			linha(lat1, lon1, lat2, lon2);
		}

	}

	private List<No> buscaNosPorEstado(String estado) throws Exception {
		return allNos.stream().filter(p -> p.getEstado().equals(estado)).collect(Collectors.toList());

	}

	private Set<String> allEstados;
	private List<No> allNos;

	private boolean verificaFronteira(No no1, No no2) throws Exception {
		if (no1.getId() != no2.getId()) {
			if (no1.getEstado().equals(no2.getEstado()))
				return true;

			return Arrays.asList(getFronteiras(no1.getEstado())).contains(no2.getEstado());
		}
		return false;
	}

	private String fixEstado(String estado) {
		switch (estado) {
		case "M":
			return "MS";
		case "N":
			return "GO";
		case "NA":
			return "GO";
		}
		return estado;
	}

	private String[] getFronteiras(String estado) throws Exception {
		switch (estado) {
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
		return new String[] {};
	}
}
