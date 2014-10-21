package models;

public enum Ficha {
	REY('R'), CASTILLO('C'), TRONO('T'), GUARDIA('G'), ENEMIGO('N'), VACIO(' '), REYMUERTO(
			'*');

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
}
