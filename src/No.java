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

	private Float latitude;
	private Float longitude;
	private String estado;
	private String cidade;
	private int id;

	public No(int id) {
		this.id = id;
	}
	
	public No(int id, String cidade, String estado, Float latitude, Float longitude) {
		this.id = id;
		this.cidade = cidade;
		this.estado = estado;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLogitude(Float logitude) {
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

	public void setLongitude(Float longitude) {
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
