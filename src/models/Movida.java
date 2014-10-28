package models;

public class Movida {

	private final Punto destino;

	private boolean elegida = false;
	private boolean podada = false;

	private final Punto origen;

	public Movida(Punto origen, Punto destino) {
		this.origen = origen;
		this.destino = destino;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// Movida other = (Movida) obj;
	// if (destino == null) {
	// if (other.destino != null)
	// return false;
	// } else if (!destino.equals(other.destino))
	// return false;
	// if (origen == null) {
	// if (other.origen != null)
	// return false;
	// } else if (!origen.equals(other.origen))
	// return false;
	// return true;
	// }
	
	

	public Punto getDestino() {
		return destino;
	}

	public boolean isPodada() {
		return podada;
	}

	public void setPodada(boolean podada) {
		this.podada = podada;
	}

	public Punto getOrigen() {
		return origen;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((destino == null) ? 0 : destino.hashCode());
	// result = prime * result + ((origen == null) ? 0 : origen.hashCode());
	// return result;
	// }

	public boolean isElegida() {
		return elegida;
	}

	public void setElegida(boolean elegida) {
		this.elegida = elegida;
	}

	@Override
	public String toString() {
		return "" + origen + destino;
	}

}
