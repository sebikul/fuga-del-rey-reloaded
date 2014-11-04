package exceptions;

import models.Point;

@SuppressWarnings("serial")
public class BoardPointOutOfBoundsException extends Exception {

	private final Point point;

	public BoardPointOutOfBoundsException(Point point) {
		this.point = point;
	}

	public BoardPointOutOfBoundsException(int row, int column) {
		this(new Point(row, column));
	}

	public Point getPunto() {
		return point;
	}

}
