
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author augustomeira
 */
public class DesenhaPontos2 {

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

				for (No no : getNos()) {

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
			Logger.getLogger(DesenhaPontos2.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private static Connection conn = null;

	// leitura de pontos. Exemplo.
	public void carregaNos(boolean dropTables, boolean useMysql) {
		String dropNosString = "DROP TABLE IF EXISTS NOS";
		String dropArestasString = "DROP TABLE IF EXISTS ARESTAS";
		String createNosString = "CREATE TABLE NOS (ID INTEGER NOT NULL, CIDADE VARCHAR(50) NOT NULL, ESTADO VARCHAR(50) NOT NULL, LAT FLOAT NOT NULL, LON FLOAT NOT NULL)";
		String createArestasString = "CREATE TABLE ARESTAS (ID INTEGER NOT NULL, ID_NO_1 INTEGER NOT NULL, ID_NO_2 INTEGER NOT NULL, DISTANCIA FLOAT NOT NULL)";
		String alterArestasString = "ALTER TABLE ARESTAS ADD PRIMARY KEY (`ID_NO_1`, `ID_NO_2`);";


		String insert = "INSERT INTO NOS (ID, CIDADE, ESTADO, LAT, LON) VALUES (?,?,?,?,?)";
		int maxId = 0;
		try {
			if (useMysql)
				conn = MysqlConnectionManager.getInstance().getConnection();
			else
				conn = DerbyConnectionManager.getInstance().getConnection();

			Statement st = conn.createStatement();
			if (dropTables) {
				st.execute(dropNosString);
				st = conn.createStatement();
				st.execute(dropArestasString);
				st = conn.createStatement();
				st.execute(createNosString);
				st = conn.createStatement();
				st.execute(createArestasString);
				st = conn.createStatement();
				st.execute(alterArestasString);
			}
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM NOS");
			while (rs.next())
				maxId = rs.getInt(1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedReader b = null;
		try {
			b = new BufferedReader(new FileReader(new File("coordenadas.txt")));
			String linha = b.readLine();
			int id = 0;
			while (linha != null && linha.length() > 0) {
				if (id >= maxId) {

					String[] lista = linha.split(";");
					String cidade = lista[0];
					String estado = lista[2];
					Float lati = Float.parseFloat(lista[3]);
					Float longi = Float.parseFloat(lista[4]);

					int i = 0;
					PreparedStatement st = conn.prepareStatement(insert);
					st.setInt(++i, ++id);
					st.setString(++i, cidade);
					st.setString(++i, fixEstado(estado));
					st.setFloat(++i, lati);
					st.setFloat(++i, longi);
					st.execute();
				}

				linha = b.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Aresta> getArestas(String estado) {
		List<Aresta> arestas = new ArrayList<Aresta>();

		try {
			String select = "SELECT A.ID, A.ID_NO_1, A.ID_NO_2, A.DISTANCIA FROM ARESTAS A, NOS N WHERE A.ID_NO_1 = N.ID AND N.ESTADO = ? ORDER BY A.DISTANCIA";
			PreparedStatement st = conn.prepareStatement(select);
			st.setString(1, estado);
			ResultSet r = st.executeQuery();
			while (r.next()) {
				int i = 0;
				int id = r.getInt(++i);
				No no1 = new No(r.getInt(++i));
				No no2 = new No(r.getInt(i++));
				Float dist = r.getFloat(i++);

				Aresta a = new Aresta(id, no1, no2, dist);
				arestas.add(a);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arestas;
	}

	private static final ReentrantLock lock = new ReentrantLock();
	private static HashMap<String, Integer> ix = new HashMap<String, Integer>();

	private static int armazenados = 0;
	private static int descartados = 0;

	/**
	 * @param args
	 *            the command line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		DesenhaPontos2 m = new DesenhaPontos2();
		m.carregaNos(false, true);

		removeDirectory(Paths.get("distancias"));
		Files.createDirectory(Paths.get("distancias"));

		List<String> estados = separaCidadesPorEstado();

		Map<String, ExecutorService> threads = new HashMap<String, ExecutorService>();

		for (String estado : estados) {
			try {
				lock.lock();
				if (ix.get(estado) == null)
					ix.put(estado, 0);
			} finally {
				lock.unlock();
			}

			List<No> cidades = getNosPorEstado(estado);

			threads.put(estado, Executors.newSingleThreadExecutor());

			threads.get(estado).submit(() -> {
				System.out.println("Iniciando: " + estado);
				processaEstado(estado, cidades);
			});
		}

		finalizaThreads(estados, threads);

		// nos.stream().sorted(Comparator.comparing(No::getCidade));

		// m.imprimePontos();

	}

	private static void finalizaThreads(List<String> estados, Map<String, ExecutorService> threads) {
		for (String estado : estados) {

			threads.get(estado).shutdown();
			try {
				threads.get(estado).awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			threads.put(estado, Executors.newSingleThreadExecutor());

			threads.get(estado).submit(() -> {
				System.out.println("Resumindo: " + estado);
				//resumeArestas(estado, threads);
			});

			System.out.println("Finalizando: " + estado + " Armazenadas: " + armazenados + " Descartadas: "
					+ descartados + " Total: " + (armazenados + descartados));
		}
	}

	private static void resumeArestas(String estado, Map<String, ExecutorService> threads) {
		try {
			Path path = Paths.get("distancias/coordenadas-resumido-" + estado + ".txt");
			Files.deleteIfExists(path);
			Files.createFile(path);
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.WRITE)) {
				for (Aresta aresta : getArestas(estado)) {
					writer.write(aresta.toString());
				}
			}

			threads.get(estado).shutdown();
			try {
				threads.get(estado).awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processaEstado(String estado, List<No> cidades) {

		String insert = "INSERT INTO ARESTAS (ID, ID_NO_1, ID_NO_2, DISTANCIA) VALUES (?,?,?,?)";

		for (; ix.get(estado) < cidades.size();) {
			No n1 = null;
			lock.lock();
			try {
				n1 = cidades.get(ix.get(estado));
				ix.put(estado, ix.get(estado) + 1);
			} finally {
				lock.unlock();
			}
			List<No> nos = getNos();
			for (No n2 : nos) {

				if (n1.getId() != n2.getId()) {

					if (verificaFronteira(n1, n2)) {
						Float dist = calculaDistancia(n1, n2);
						Aresta a = new Aresta(++armazenados, n1, n2, dist);
						try {
							int i = 0;
							PreparedStatement st = conn.prepareStatement(insert);
							st.setInt(++i, a.getId());
							st.setInt(++i, n1.getId());
							st.setInt(++i, n2.getId());
							st.setFloat(++i, dist);
							st.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}

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
		}

	}

	private static List<String> separaCidadesPorEstado() {
		List<String> estados = new ArrayList<String>();

		try {
			String select = "SELECT DISTINCT ESTADO FROM NOS";
			Statement st = conn.createStatement();
			ResultSet r = st.executeQuery(select);
			while (r.next()) {
				estados.add(r.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return estados;
	}

	private static List<No> allNos = null;

	private static List<No> getNos() {
		if (allNos == null) {
			allNos = new ArrayList<No>();
			try {
				String select = "SELECT ID, CIDADE, ESTADO, LAT, LON FROM NOS";
				Statement st = conn.createStatement();
				ResultSet r = st.executeQuery(select);
				while (r.next()) {
					int i = 0;
					No no = new No(r.getInt(++i), r.getString(++i), r.getString(++i), r.getFloat(++i), r.getFloat(++i));
					allNos.add(no);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return allNos;
	}

	private static List<No> getNosPorEstado(String estado) {
		List<No> result = new ArrayList<No>();

		try {
			String select = "SELECT ID, CIDADE, ESTADO, LAT, LON FROM NOS WHERE ESTADO = ?";
			PreparedStatement st = conn.prepareStatement(select);
			st.setString(1, estado);
			ResultSet r = st.executeQuery();
			while (r.next()) {
				int i = 0;
				No no = new No(r.getInt(++i), r.getString(++i), r.getString(++i), r.getFloat(++i), r.getFloat(++i));
				result.add(no);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void removeDirectory(Path path) {
		try {
			if (Files.isDirectory(path)) {
				Files.list(path).forEach(s -> {
					removeDirectory(s);
				});
				Files.delete(path);
			} else

			{
				Files.delete(path);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

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
			return new String[] { "SP", "SC", "M" };
		case "SC":
			return new String[] { "SP", "SC", "M" };
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
