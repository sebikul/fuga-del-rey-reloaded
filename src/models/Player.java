package models;

public enum Player {

	GUARD(0), ENEMY(1);

	private final int index;

	public static Player fromInt(int turno) {

		if (turno == 1) {
			return ENEMY;
		} else if (turno == 2) {
			return GUARD;
		}

		throw new IllegalArgumentException();

	}

	Player(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
