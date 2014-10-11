package models;

public class Enemy extends MovablePiece {

	public Enemy(int x, int y) {
		super(x, y);
	}

	@Override
	public String getToken() {
		return "E";
	}

}
