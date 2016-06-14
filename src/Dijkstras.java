import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Dijkstras {

	public static List<Integer> getDijkstra(final List<No> allNos) {
		Graph g = new Graph();
		for (No no1 : allNos) {

			if (verificaEstado(no1.getEstado())) {
				List<Vertex> vrtz = new ArrayList<>();
				for (No no2 : allNos) {
					if (verificaEstado(no2.getEstado())) {
						if (no1.getId() != no2.getId()) {
							Integer dist = DesenhaPontos.calculaDistancia(no1, no2).intValue();
							vrtz.add(new Vertex(no2.getId(), dist * dist));
						}
					}
				}
				g.addVertex(no1.getId(), vrtz);

			}
		}
		return g.getShortestPath(23872, 2822);
	}

	private static boolean verificaEstado(String estado) {
		String[] estados = { "RS", "SC", "PR", "GO", "TO", "PA", "AP" };
		return Arrays.asList(estados).contains(estado);
	}

}

class Vertex implements Comparable<Vertex> {

	private Integer id;
	private Integer distance;

	public Vertex(Integer id, Integer distance) {
		super();
		this.id = id;
		this.distance = distance;
	}

	public Integer getId() {
		return id;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vertex [id=" + id + ", distance=" + distance + "]";
	}

	@Override
	public int compareTo(Vertex o) {
		if (this.distance < o.distance)
			return -1;
		else if (this.distance > o.distance)
			return 1;
		else
			return this.getId().compareTo(o.getId());
	}

}

class Graph {

	private final Map<Integer, List<Vertex>> vertices;

	public Graph() {
		this.vertices = new HashMap<Integer, List<Vertex>>();
	}

	public void addVertex(Integer character, List<Vertex> vertex) {
		this.vertices.put(character, vertex);
	}

	public List<Integer> getShortestPath(int start, int finish) {
		final Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
		final Map<Integer, Vertex> previous = new HashMap<Integer, Vertex>();
		PriorityQueue<Vertex> nodes = new PriorityQueue<Vertex>();

		for (Integer vertex : vertices.keySet()) {
			if (vertex == start) {
				distances.put(vertex, 0);
				nodes.add(new Vertex(vertex, 0));
			} else {
				distances.put(vertex, Integer.MAX_VALUE);
				nodes.add(new Vertex(vertex, Integer.MAX_VALUE));
			}
			previous.put(vertex, null);
		}

		while (!nodes.isEmpty()) {
			Vertex smallest = nodes.poll();
			if (smallest.getId().intValue() == finish) {
				final List<Integer> path = new ArrayList<Integer>();
				while (previous.get(smallest.getId()) != null) {
					path.add(smallest.getId());
					smallest = previous.get(smallest.getId());
				}
				return path;
			}

			if (distances.get(smallest.getId()) == Integer.MAX_VALUE) {
				break;
			}

			for (Vertex neighbor : vertices.get(smallest.getId())) {
				Integer alt = distances.get(smallest.getId()) + neighbor.getDistance();
				if (alt < distances.get(neighbor.getId())) {
					distances.put(neighbor.getId(), alt);
					previous.put(neighbor.getId(), smallest);

					forloop: for (Vertex n : nodes) {
						if (n.getId().intValue() == neighbor.getId().intValue()) {
							nodes.remove(n);
							n.setDistance(alt);
							nodes.add(n);
							break forloop;
						}
					}
				}
			}
		}

		return new ArrayList<Integer>(distances.keySet());
	}

}