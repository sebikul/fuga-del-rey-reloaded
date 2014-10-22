package models;

public enum Ficha {
	REY('R', Jugador.GUARDIA), CASTILLO('C', null), TRONO('T', null), GUARDIA(
			'G', Jugador.GUARDIA), ENEMIGO('N', Jugador.ENEMIGO), VACIO(' ',
			null), REYMUERTO('*', null);

	private final char token;
	private final Jugador jugador;

	Ficha(char token, Jugador jugador) {
		this.token = token;
		this.jugador = jugador;
	}

	public char getToken() {
		return token;
	}

	static public Ficha fromChar(char c) {

		switch (c) {

		case 'R':
			return REY;

		case 'C':
			return CASTILLO;

		case 'T':
			return TRONO;

		case 'G':
			return GUARDIA;

		case 'N':
			return ENEMIGO;

		case '0':
		case ' ':
			return VACIO;

		case '*':
			return REYMUERTO;

		default:
			throw new IllegalArgumentException();

		}

	}

	public Jugador getJugador() {
		return jugador;
	}
}
