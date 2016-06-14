
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {

	private static Kruskal instance = null;

	public static Kruskal getInstance() {
		if (instance == null) {
			instance = new Kruskal();
		}
		return instance;
	}

	private Kruskal() {
	}

	/**
	 *
	 * This algorithm generates a MST giving priority to the lower weight. It
	 * will try to add an edge to the MST LinkedList only if it won't form a
	 * cycle.
	 *
	 */
	public ArrayList<Aresta> generateMST(List<Aresta> arestas) {

		Collections.sort(arestas);

		int i = 0;
		ArrayList<Aresta> mst = new ArrayList<Aresta>();
		
		UnionFind uf = new UnionFind(arestas.size() < 100000 ? 100000 : arestas.size());
		
		for (Aresta aresta : arestas) {
			int v1 = aresta.getNo1().getId();
			int v2 = aresta.getNo2().getId();

			if (uf.find(v1) != uf.find(v2)) {
				mst.add(arestas.get(i));
				uf.union(v1, v2);
			}
			i++;
		}
		return mst;
	}
}