package models;

public enum Ficha {
	REY('R'), CASTILLO('C'), TRONO('T'), GUARDIA('G'), ENEMIGO('E'), VACIO(' ');

	private final char token;

	Ficha(char token) {
		this.token = token;
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

		case 'E':
			return ENEMIGO;

		case ' ':
			return VACIO;

		default:
			throw new IllegalArgumentException();

		}

	}
}
