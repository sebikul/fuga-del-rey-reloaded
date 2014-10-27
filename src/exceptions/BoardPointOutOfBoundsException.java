package exceptions;

import models.Punto;

@SuppressWarnings("serial")
public class BoardPointOutOfBoundsException extends Exception {

	private final Punto punto;

	public BoardPointOutOfBoundsException(Punto punto) {
		this.punto = punto;
	}

	public BoardPointOutOfBoundsException(int fila, int columna) {
		this(new Punto(fila, columna));
	}

	public Punto getPunto() {
		return punto;
	}

}
