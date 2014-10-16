package exceptions;

import models.Punto;

@SuppressWarnings("serial")
public class MovimientoBloqueadoException extends Exception {

	private final Punto punto;

	public MovimientoBloqueadoException(Punto punto) {
		this.punto = punto;
	}

	public Punto getPunto() {
		return punto;
	}

}
