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
