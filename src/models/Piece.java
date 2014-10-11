package models;

public abstract class Piece {

	public abstract String getToken();


	@Override
	public String toString() {
		return getToken();
	}

}
