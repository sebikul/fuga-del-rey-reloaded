package exceptions;

import models.Point;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {

	private final Point origin;
	private final Point destination;

	public InvalidMoveException(Point origin, Point destination) {
		this.origin = origin;
		this.destination = destination;
	}

	public Point getOrigen() {
		return origin;
	}

	public Point getDestino() {
		return destination;
	}

}
