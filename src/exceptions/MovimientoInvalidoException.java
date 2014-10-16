package exceptions;

import models.Punto;

@SuppressWarnings("serial")
public class MovimientoInvalidoException extends Exception {

	private final Punto origen;
	private final Punto destino;

	public MovimientoInvalidoException(Punto origen, Punto destino) {
		this.origen = origen;
		this.destino = destino;
	}

	public Punto getOrigen() {
		return origen;
	}

	public Punto getDestino() {
		return destino;
	}

}
