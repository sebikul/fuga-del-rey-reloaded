package models;

public class Punto {

	private final int columna;

	private final int fila;

	private final Ficha ficha;

	public Punto(int fila, int columna) {
		this(fila, columna, null);
	}

	public Punto(int fila, int columna, Ficha ficha) {
		this.fila = fila;
		this.columna = columna;
		this.ficha = ficha;
	}

	public boolean equals(int fila, int columna) {
		return fila == this.fila && columna == this.columna;
	}

	public int getColumna() {
		return columna;
	}

	public int getFila() {
		return fila;
	}

	@Override
	public String toString() {
		return "(" + fila + ", " + columna + ")";
	}

	public Ficha getFicha() {
		return ficha;
	}

	public boolean equals(Punto punto) {
		return equals(punto.fila, punto.columna);
	}

}
