package models;

public class Point {

	private final int row, column;

	private final Piece piece;

	public Point(int row, int column) {
		this(row, column, null);
	}

	public Point(int row, int column, Piece piece) {
		this.row = row;
		this.column = column;
		this.piece = piece;
	}

	public boolean equals(int fila, int columna) {
		return fila == this.row && columna == this.column;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	@Override
	public String toString() {
		return "(" + row + ", " + column + ")";
	}

	public Piece getPiece() {
		return piece;
	}

	public boolean equals(Point point) {
		return equals(point.row, point.column);
	}

}
