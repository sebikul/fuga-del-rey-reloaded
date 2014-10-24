package models;

public class Movida {

	private final Punto destino;

	private final Punto origen;

	private int valor;

	@Override
	public String toString() {
		return "Movida [destino=" + destino + ", origen=" + origen + ", valor="
				+ valor + "]";
	}

	public Movida(Punto origen, Punto destino) {
		this(origen, destino, 0);
	}

	public Movida(Punto origen, Punto destino, int valor) {
		this.origen = origen;
		this.destino = destino;
		this.valor = valor;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movida other = (Movida) obj;
		if (destino == null) {
			if (other.destino != null)
				return false;
		} else if (!destino.equals(other.destino))
			return false;
		if (origen == null) {
			if (other.origen != null)
				return false;
		} else if (!origen.equals(other.origen))
			return false;
		return true;
	}

	public Punto getDestino() {
		return destino;
	}

	public Punto getOrigen() {
		return origen;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destino == null) ? 0 : destino.hashCode());
		result = prime * result + ((origen == null) ? 0 : origen.hashCode());
		return result;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

}
