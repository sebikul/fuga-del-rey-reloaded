package models;

public class Punto {

	private final int columna;

	private final int fila;

	public Punto(int fila, int columna) {
		this.fila = fila;
		this.columna = columna;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Punto other = (Punto) obj;
		if (fila != other.fila)
			return false;
		if (columna != other.columna)
			return false;
		return true;
	}

	public int getColumna() {
		return columna;
	}

	public int getFila() {
		return fila;
	}

	@Override
	public String toString() {
		return "Punto [fila=" + fila + ", columna=" + columna + "]";
	}

}
