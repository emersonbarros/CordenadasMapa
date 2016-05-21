
public class Aresta {

	private long id;

	private No no1;

	private No no2;

	private Double distancia;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Double getDistancia() {
		return distancia;
	}

	public void setDistancia(Double distancia) {
		this.distancia = distancia;
	}

	@Override
	public String toString() {
		return "" + id + "," + no1 + "," + no2 + "," + distancia +"\n";
	}

}
