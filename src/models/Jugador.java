package models;

public enum Jugador {

	GUARDIA(0), ENEMIGO(1);

	private final int indice;

	public static Jugador fromInt(int turno) {

		if (turno == 1) {
			return ENEMIGO;
		} else if (turno == 2) {
			return GUARDIA;
		}

		throw new IllegalArgumentException();

	}

	Jugador(int indice) {
		this.indice = indice;
	}

	public int getIndice() {
		return indice;
	}

}
