package models;

public enum Turno {

	GUARDIAS, ENEMIGOS;

	public static Turno fromInt(int turno) {

		if (turno == 1) {
			return ENEMIGOS;
		} else if (turno == 2) {
			return GUARDIAS;
		}

		throw new IllegalArgumentException();

	}

}
