package models;

public enum Jugador {

	GUARDIA, ENEMIGO;

	public static Jugador fromInt(int turno) {

		if (turno == 1) {
			return ENEMIGO;
		} else if (turno == 2) {
			return GUARDIA;
		}

		throw new IllegalArgumentException();

	}

}
