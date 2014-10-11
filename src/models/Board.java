package models;

public class Board {

	private Piece[][] board;

	private int size;

	public Board(int size) {

		if (size < 7 || size > 19 || size % 2 == 0) {
			throw new IllegalArgumentException(
					"Dimensiones del tablero invalidas");
		}

		this.size = size;

		generateBoard();

		populateBoard();

	}

	private void addEnemy(int x, int y) {
		board[y][x] = new Enemy(x, y);
		board[x][y] = new Enemy(y, x);
	}

	private void addGuard(int x, int y) {
		board[y][x] = new Guard(x, y);
	}

	private void generateBoard() {
		board = new Piece[size][];

		for (int i = 0; i < size; i++) {

			board[i] = new Piece[size];

		}
	}

	public boolean isBigBoard() {
		return size >= 13;
	}

	private void populateBoard() {

		int maxIndex = size - 1;
		int middle = maxIndex / 2;

		board[0][0] = board[0][maxIndex] = board[maxIndex][0] = board[maxIndex][maxIndex] = new Castle();

		board[middle][middle] = new King(middle, middle);

		addEnemy(1, middle);
		// addEnemy(middle, 1);

		addEnemy(maxIndex - 1, middle);
		// addEnemy(middle, size - 2);

		int max = isBigBoard() ? (5 - 1) / 2 : (3 - 1) / 2;

		for (int i = middle; i <= (middle + max); i++) {

			addEnemy(0, i);
			// addEnemy(i, 0);

			addEnemy(0, maxIndex - i);
			// addEnemy(size - 1 - i, 0);

			addEnemy(maxIndex, i);
			// addEnemy(i, size - 1);

			addEnemy(maxIndex, maxIndex - i);
			// addEnemy(size - 1 - i, size - 1);

		}

		for (int i = middle - 1; i <= middle + 1; i++) {
			for (int j = middle - 1; j <= middle + 1; j++) {
				if (i != middle || j != middle) {
					addGuard(i, j);
				}

			}
		}

		if (isBigBoard()) {

			addGuard(middle, middle + 2);
			addGuard(middle + 2, middle);

			addGuard(middle, middle - 2);
			addGuard(middle - 2, middle);

		}

	}

	@Override
	public String toString() {

		String str = "\t ";

		for (int i = 0; i < size; i++) {
			str += i + "    " + (i < 10 ? " " : "");
		}
		str += "\n";

		for (int i = 0; i < size; i++) {

			str += i + "\t";

			for (int j = 0; j < size; j++) {

				str += (j == 0 ? "" : "   ")
						+ (board[i][j] == null ? "   " : " " + board[i][j]
								+ " ");

			}
			str += "\n\n";
		}

		return str;

	}

	private boolean isValidPoint(Point point) {

	}

	public boolean isOponent(Point point){

		

		if(!isValidPoint(point))
			return false;

		Piece piece=board[point.getY()][point.getX()];

		if(piece instanceof Castle)
			return true;

		if(juego->jugador==TABLERO_JUGADOR_GUARDIA && ficha==TABLERO_JUGADOR_ENEMIGO)
			return 1;

		if(juego->jugador==TABLERO_JUGADOR_ENEMIGO && (ficha==TABLERO_JUGADOR_REY || ficha==TABLERO_JUGADOR_GUARDIA))
			return 1;

		return 0;
	}
}
