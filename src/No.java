/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author emerson
 */
public class No implements Comparable<Object> {

	private Double latitude;
	private Double longitude;
	private String estado;
	private String cidade;
	private int id;

	public No(int id, String cidade, String estado, Double latitude, Double longitude) {
		this.id = id;
		this.cidade = cidade;
		this.estado = estado;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLogitude(Double logitude) {
		this.longitude = logitude;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(Object o) {

		No other = (No) o;

		int i = this.getLatitude().compareTo(other.getLatitude());
		if (i != 0)
			return i;

		i = this.getLongitude().compareTo(other.getLongitude());
		if (i != 0)
			return i;

		return 0;
	}

	@Override
	public String toString() {
		return latitude + ";" + longitude + ";" + estado + ";" + cidade + ";" + id;
	}

}
