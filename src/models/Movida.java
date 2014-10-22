package models;

public class Movida implements Comparable<Movida> {

	private final Punto origen;

	private final Punto destino;

	private int valor;

	public void setValor(int valor) {
		this.valor = valor;
	}

	public Movida(Punto origen, Punto destino, int valor) {
		this.origen = origen;
		this.destino = destino;
		this.valor = valor;
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
	public int compareTo(Movida o) {

		return valor - o.getValor();
	}

}
