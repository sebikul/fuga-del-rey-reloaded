package models;

public enum Piece {
	KING('R', Player.GUARD), CASTLE('C', null), THRONE('T', null), GUARD(
			'G', Player.GUARD), ENEMY('N', Player.ENEMY), EMPTY(' ',
			null), DEADKING('*', null);

	private final char token;
	private final Player player;

	Piece(char token, Player player) {
		this.token = token;
		this.player = player;
	}

	public char getToken() {
		return token;
	}

	static public Piece fromChar(char c) {

		switch (c) {

		case 'R':
			return KING;

		case 'C':
			return CASTLE;

		case 'T':
			return THRONE;

		case 'G':
			return GUARD;

		case 'N':
			return ENEMY;

		case '0':
		case ' ':
			return EMPTY;

		case '*':
			return DEADKING;

		default:
			throw new IllegalArgumentException();

		}

	}

	public Player getPlayer() {
		return player;
	}
}
