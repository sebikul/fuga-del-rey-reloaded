package exceptions;

import models.Point;

@SuppressWarnings("serial")
public class BlockedMoveException extends Exception {

	private final Point point;

	public BlockedMoveException(Point point) {
		this.point = point;
	}

	public Point getPunto() {
		return point;
	}

}
