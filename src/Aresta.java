public class Aresta implements Comparable<Object> {

	public Aresta(Integer id, No no1, No no2, Float distancia) {
		this.id = id;
		this.no1 = no1;
		this.no2 = no2;
		this.distancia = distancia;
	}

	private Integer id;

	private No no1;

	private No no2;

	private Float distancia;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public No getNo1() {
		return no1;
	}

	public void setNo1(No no1) {
		this.no1 = no1;
	}

	public No getNo2() {
		return no2;
	}

	public void setNo2(No no2) {
		this.no2 = no2;
	}

	public Float getDistancia() {
		return distancia;
	}

	public void setDistancia(Float distancia) {
		this.distancia = distancia;
	}

	@Override
	public String toString() {
		return "" + id + ";" + no1 + ";" + no2 + ";" + distancia + "\n";
	}

	@Override
	public int compareTo(Object o) {

		Aresta other = (Aresta) o;

		return this.getDistancia().compareTo(other.getDistancia());
	}

}
