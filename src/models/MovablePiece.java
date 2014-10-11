package models;

public abstract class MovablePiece extends Piece {

	protected int x;
	protected int y;

	public MovablePiece(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
