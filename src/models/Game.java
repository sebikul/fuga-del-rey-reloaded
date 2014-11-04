package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.BlockedMoveException;
import exceptions.InvalidMoveException;

public class Game {

	private int[] pieceCount = new int[] { 0, 0 };

	private int size;

	private Piece[][] board;

	private Player turn = Player.GUARD;

	public Game(int boardSize) {

		if (boardSize < 7 || boardSize > 19 || boardSize % 2 == 0) {
			throw new IllegalArgumentException(
					"Dimensiones del tablero invalidas: " + boardSize);
		}

		this.size = boardSize;

		this.board = new Piece[size][size];

		populateBoard();

	}

	public Game(int size, Piece[][] board, Player turn) {

		this.size = size;
		this.board = board;
		this.turn = turn;

	}

	private Point findKing() {

		for (Point point : this.pieces()) {
			if (point.getPiece() == Piece.KING) {
				return point;
			}
		}

		return null;

	}

	private void switchPlayer() {

		if (turn == Player.GUARD) {
			turn = Player.ENEMY;
		} else if (turn == Player.ENEMY) {
			turn = Player.GUARD;
		}

	}

	public Game copy() {
		Game tmpgame = new Game(size, null, turn);

		tmpgame.board = new Piece[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				tmpgame.board[i][j] = board[i][j];

			}
		}

		tmpgame.pieceCount[Player.ENEMY.getIndex()] = pieceCount[Player.ENEMY
				.getIndex()];
		tmpgame.pieceCount[Player.GUARD.getIndex()] = pieceCount[Player.GUARD
				.getIndex()];

		return tmpgame;
	}

	public boolean isAlly(Point point) {

		return isAlly(point.getRow(), point.getColumn());
	}

	public boolean isAlly(int row, int column) {

		if (!isValidPoint(row, column))
			return false;

		Piece piece = board[row][column];

		if (piece == Piece.CASTLE) {
			return true;
		}

		if (turn == Player.GUARD
				&& (piece == Piece.KING || piece == Piece.GUARD || piece == Piece.THRONE))
			return true;

		if (turn == Player.ENEMY && piece == Piece.ENEMY)
			return true;

		return false;
	}

	public boolean isOpponent(Point point) {

		return isOpponent(point.getRow(), point.getColumn());
	}

	public boolean isOpponent(int row, int column) {

		if (!isValidPoint(row, column))
			return false;

		Piece piece = board[row][column];

		if (piece == Piece.CASTLE) {
			return true;
		}

		if (turn == Player.GUARD && piece == Piece.ENEMY) {
			return true;
		}

		if (turn == Player.ENEMY
				&& (piece == Piece.KING || piece == Piece.GUARD)) {
			return true;
		}
		return false;

	}

	public boolean isLongBoard() {
		return size >= 13;
	}

	public Piece getPieceAt(int row, int column)
			throws BoardPointOutOfBoundsException {

		if (!isValidPoint(row, column)) {
			throw new BoardPointOutOfBoundsException(row, column);
		}

		return board[row][column];

	}

	public List<Move> getPossibleMoves(Point origin) {

		List<Move> list = new ArrayList<Move>();

		int row, column;

		// abajo
		for (column = origin.getColumn(), row = origin.getRow() + 1; row < size
				&& board[row][column] == Piece.EMPTY; row++) {

			list.add(new Move(origin, new Point(row, column)));

		}

		// arriba
		for (column = origin.getColumn(), row = origin.getRow() - 1; row >= 0
				&& board[row][column] == Piece.EMPTY; row--) {

			list.add(new Move(origin, new Point(row, column)));

		}

		// derecha
		for (row = origin.getRow(), column = origin.getColumn() + 1; column < size
				&& board[row][column] == Piece.EMPTY; column++) {

			list.add(new Move(origin, new Point(row, column)));
		}

		// izquierda
		for (row = origin.getRow(), column = origin.getColumn() - 1; column >= 0
				&& board[row][column] == Piece.EMPTY; column--) {

			list.add(new Move(origin, new Point(row, column)));
		}

		return list;

	}

	public int getSize() {
		return size;
	}

	public char getTokenAt(int row, int column)
			throws BoardPointOutOfBoundsException {

		return getPieceAt(row, column).getToken();

	}

	public Player getTurn() {
		return turn;
	}

	public Player move(Move move) throws InvalidMoveException,
			BoardPointOutOfBoundsException, BlockedMoveException {
		return move(move.getOrigin(), move.getDestination());
	}

	/*
	 * * Ejecuta el movimiento de _origen a _destino* Valida el turno, el camino
	 * y los bloqueos
	 */
	public Player move(Point origin, Point destination)
			throws InvalidMoveException, BoardPointOutOfBoundsException,
			BlockedMoveException {

		int middle = (size - 1) / 2;

		Piece piece = board[origin.getRow()][origin.getColumn()];

		/* El punto esta fuera del tablero */
		if (!isValidPoint(origin) || !isValidPoint(destination)) {
			throw new BoardPointOutOfBoundsException(origin);
		}

		/* La fila ó columna de origen y destino no coinciden */
		if (origin.getRow() != destination.getRow()
				&& origin.getColumn() != destination.getColumn()) {
			throw new InvalidMoveException(origin, destination);
		}

		if (!isAlly(origin)) {
			throw new InvalidMoveException(origin, destination);
		}

		/* Verifica si el destino es un castillo y la ficha no es el rey */
		if (piece != Piece.KING
				&& (destination.equals(0, 0) || destination.equals(0, size - 1)
						|| destination.equals(size - 1, 0) || destination
							.equals(size - 1, size - 1))) {
			throw new InvalidMoveException(origin, destination);

		}

		/* Verifica si el destino es un castillo y la ficha es el rey */
		if (piece == Piece.KING
				&& (destination.equals(0, 0) || destination.equals(0, size - 1)
						|| destination.equals(size - 1, 0) || destination
							.equals(size - 1, size - 1))) {
			return Player.GUARD;
		}

		/* Chequea que no pueda ir una ficha que no sea el rey al trono */
		if (piece != Piece.KING && destination.equals(middle, middle)) {
			throw new InvalidMoveException(origin, destination);
		}

		/* La ficha no se puede quedar en el mismo lugar */
		if (origin.equals(destination)) {
			throw new InvalidMoveException(origin, destination);
		}

		/* Se verifica que el camino este vacio, y sea valido */
		for (int row = origin.getRow(); origin.getRow() < destination.getRow() ? row <= destination
				.getRow() : row >= destination.getRow(); row += (origin
				.getRow() < destination.getRow() ? 1 : -1)) {

			for (int column = origin.getColumn(); origin.getColumn() < destination
					.getColumn() ? column <= destination.getColumn()
					: column >= destination.getColumn(); column += origin
					.getColumn() < destination.getColumn() ? 1 : -1) {

				if (!origin.equals(row, column)) {
					/*
					 * Verifica si el camino esta bloqueado por otra ficha o si
					 * es el tablero largo y no se es el enemigo
					 */

					Piece tmpPiece = board[row][column];

					/*
					 * Verifica que todo el camino este libre, el caso del trono
					 * se analiza despues
					 */
					if (tmpPiece != Piece.EMPTY && tmpPiece != Piece.THRONE) {
						throw new BlockedMoveException(new Point(row, column));
					}

					/*
					 * Verifica que el tablero sea largo y los guardias no
					 * puedan pasar por el trono
					 */
					if (isLongBoard() && tmpPiece == Piece.THRONE
							&& piece == Piece.GUARD) {
						throw new BlockedMoveException(new Point(row, column));
					}

				}

			}

		}

		if (origin.equals(middle, middle)) {
			board[origin.getRow()][origin.getColumn()] = Piece.THRONE;
		} else {
			board[origin.getRow()][origin.getColumn()] = Piece.EMPTY;
		}

		board[destination.getRow()][destination.getColumn()] = piece;

		return verifyBlockings(destination);

	}

	private void populateBoard() {

		int maxIndex = size - 1;
		int middle = maxIndex / 2;

		board[0][0] = board[0][maxIndex] = board[maxIndex][0] = board[maxIndex][maxIndex] = Piece.CASTLE;

		board[middle][middle] = Piece.KING;
		pieceCount[Player.GUARD.getIndex()] += 1;

		board[1][middle] = Piece.ENEMY;
		board[middle][1] = Piece.ENEMY;

		board[maxIndex - 1][middle] = Piece.ENEMY;
		board[middle][maxIndex - 1] = Piece.ENEMY;
		pieceCount[Player.ENEMY.getIndex()] += 4;

		int max = isLongBoard() ? (5 - 1) / 2 : (3 - 1) / 2;

		for (int i = middle; i <= (middle + max); i++) {

			board[0][i] = Piece.ENEMY;
			board[i][0] = Piece.ENEMY;

			board[0][maxIndex - i] = Piece.ENEMY;
			board[maxIndex - i][0] = Piece.ENEMY;

			board[maxIndex][i] = Piece.ENEMY;
			board[i][maxIndex] = Piece.ENEMY;

			board[maxIndex][maxIndex - i] = Piece.ENEMY;
			board[maxIndex - i][maxIndex] = Piece.ENEMY;
			pieceCount[Player.ENEMY.getIndex()] += 8;

		}

		for (int i = middle - 1; i <= middle + 1; i++) {
			for (int j = middle - 1; j <= middle + 1; j++) {
				if (i != middle || j != middle) {
					board[i][j] = Piece.GUARD;
					pieceCount[Player.GUARD.getIndex()] += 1;

				}

			}
		}

		if (isLongBoard()) {

			board[middle][middle + 2] = Piece.GUARD;
			board[middle + 2][middle] = Piece.GUARD;
			board[middle][middle - 2] = Piece.GUARD;
			board[middle - 2][middle] = Piece.GUARD;
			pieceCount[Player.GUARD.getIndex()] += 4;

		}

		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (board[row][column] == null) {
					board[row][column] = Piece.EMPTY;
				}
			}
		}

	}

	private boolean isValidPoint(Point punto) {

		return isValidPoint(punto.getRow(), punto.getColumn());

	}

	private boolean isValidPoint(int fila, int columna) {

		return fila >= 0 && fila < size && columna < size && columna >= 0;

	}

	@Override
	public String toString() {

		String str = "        ";

		for (int i = 0; i < size; i++) {
			str += i + "    " + (i < 10 ? " " : "");
		}
		str += "\n";

		int len = str.length() / 3 - 1;
		for (int i = 0; i < len; i++) {
			str += "_  ";
		}
		str += "\n";

		for (int i = 0; i < size; i++) {

			str += i + " | \t";

			for (int j = 0; j < size; j++) {

				str += (j == 0 ? "" : "    ")
						+ (board[i][j] == Piece.EMPTY ? " " : board[i][j]
								.getToken()) + " ";

			}
			str += "|\n\n";
		}

		for (int i = 0; i < len; i++) {
			str += "_  ";
		}

		return str;

	}

	public int magicValue() {

		int valor = pieceCount[Player.ENEMY.getIndex()]
				- pieceCount[Player.GUARD.getIndex()];

		int blockings = 0;
		int lowerCuadrant = 0;
		int killTheKing = 0;

		// valor = valor * valor;

		Point king = findKing();

		if (king == null) {
			return Integer.MAX_VALUE;
		}

		for (int column = king.getColumn() - 2; column <= king.getColumn() + 2; column++) {
			for (int row = king.getRow() - 2; row <= king.getRow() + 2; row++) {

				if (row == king.getRow() && column == king.getColumn()) {
					continue;
				}

				if (isValidPoint(row, column)
						&& board[row][column] == Piece.ENEMY) {
					blockings++;
				}

				if (isValidPoint(row, column)
						&& board[row][column] == Piece.ENEMY) {

					if (lowerCuadrant(row, column, king.getRow(),
							king.getColumn())) {
						lowerCuadrant++;
					}

					if (isTrappingKing(row, column, king.getRow(),
							king.getColumn())) {
						killTheKing++;
					}

				}
			}

		}

		// System.out.println("Valor: " + valor);
		// System.out.println("LA CANTIDAD DE BLOQUEOS ES: " + bloqueos);
		// System.out.println("LA CANTIDAD DE CUADRANTEMENOR ES: " +
		// cuadranteMenor);
		// System.out.println("LA CANTIDAD DE MATARALREY ES: " + matarAlRey);
		int heuristica = valor + 2 * blockings + 3 * lowerCuadrant + 6
				* killTheKing;
		// System.out.println("EL VALORCITO ES : " + heuristica);
		return Math.abs(heuristica);
	}

	/*
	 * * Verifica si estoy en uno de los cuatro puntos validos para matar al Rey
	 */

	private boolean isTrappingKing(int currentRow, int currentColumn,
			int kingRow, int kingColumn) {
		return (currentRow == kingRow && currentColumn == kingColumn + 1)
				|| (currentRow == kingRow && currentColumn == kingColumn - 1)
				|| (currentRow + 1 == kingRow && currentColumn == kingColumn)
				|| (currentRow - 1 == kingRow && currentColumn == kingColumn);
	}

	/*
	 * * Verifica si estoy en uno de los cuatro puntos que no matan al rey, pero
	 * estan en el cuadrante mas chico
	 */

	private boolean lowerCuadrant(int currentRow, int currentColumn,
			int kingRow, int kingColumn) {
		return (currentRow == kingRow - 1 && currentColumn == kingColumn - 1)
				|| (currentRow == kingRow + 1 && currentColumn == kingColumn + 1)
				|| (currentRow == kingRow - 1 && currentColumn == kingColumn + 1)
				|| (currentRow == kingRow + 1 && currentColumn == kingColumn - 1);
	}

	/*
	 * * Verifica si la ficha movida hacia _destino produjo algun bloqueo
	 */
	private Player verifyBlockings(Point destination) {

		/* Itera sobre los puntos adyacentes */
		for (int column = destination.getColumn() - 1; column <= destination
				.getColumn() + 1; column++) {
			for (int row = destination.getRow() - 1; row <= destination
					.getRow() + 1; row++) {

				if ((row != destination.getRow() && column != destination
						.getColumn()) || destination.equals(row, column)) {
					continue;
				}

				if (isOpponent(row, column)) {

					if (board[row][column] == Piece.KING) {

						int blockings = 0;

						int blockingsByGuard = 0;

						/* Verifica que el rey este rodeado por 4 aliados */
						for (int tmpColumn = column - 1; tmpColumn <= column + 1; tmpColumn++) {
							for (int tmpRow = row - 1; tmpRow <= row + 1; tmpRow++) {

								if (!((tmpRow != row && tmpColumn != column) || (tmpRow == row && tmpColumn == column))) {

									if (isAlly(tmpRow, tmpColumn)
											|| !isValidPoint(tmpRow, tmpColumn)) {
										blockings++;

									} else if (isOpponent(tmpRow, tmpColumn)) {
										blockingsByGuard++;
									}
								}

							}

						}

						/* El rey esta rodeado */
						if (blockings == 4
								|| (blockings == 3 && blockingsByGuard == 1)) {
							board[row][column] = Piece.DEADKING;
							return Player.ENEMY;
						}

					} else {
						/*
						 * La ficha oponente no es el rey, veo si esta capturada
						 */

						if (isAlly(
								destination.getRow()
										+ (row - destination.getRow()) * 2,
								destination.getColumn()
										+ (column - destination.getColumn())
										* 2)) {

							pieceCount[board[row][column].getPlayer()
									.getIndex()]--;

							board[row][column] = Piece.EMPTY;

							if (pieceCount[Player.ENEMY.getIndex()] == 0) {
								return Player.GUARD;
							}

						}

					}
				}

			}

		}

		switchPlayer();

		return null;

	}

	public void setPieceCount(int enemies, int guards) {

		pieceCount[Player.ENEMY.getIndex()] = enemies;
		pieceCount[Player.GUARD.getIndex()] = guards;

	}

	public Iterable<Point> currentTurnPieces() {

		return new Iterable<Point>() {

			public Iterator<Point> iterator() {

				return new Iterator<Point>() {

					private Piece[][] board = Game.this.board;

					private int row = 0;
					private int column = 0;

					@Override
					public boolean hasNext() {

						Piece ficha;

						if (row == size) {
							return false;
						}

						if (column < size) {

							ficha = board[row][column];

							if ((turn == Player.ENEMY && ficha == Piece.ENEMY)
									|| (turn == Player.GUARD && (ficha == Piece.GUARD || ficha == Piece.KING))) {
								return true;
							} else {

								column++;
								return hasNext();
							}

						} else {
							column = column % size;
							row++;

							return hasNext();

						}

					}

					@Override
					public Point next() {

						Point point = new Point(row, column, board[row][column]);

						column++;

						return point;

					}

					@Override
					public void remove() {

					}

				};

			}
		};
	}

	public Iterable<Point> pieces() {

		return new Iterable<Point>() {

			public Iterator<Point> iterator() {

				return new Iterator<Point>() {

					private Piece[][] board = Game.this.board;

					private int row = 0;
					private int column = 0;

					@Override
					public boolean hasNext() {

						if (row == size) {
							return false;
						}

						if (column < size) {

							return true;

						} else {
							column = column % size;
							row++;

							return hasNext();

						}

					}

					@Override
					public Point next() {

						Point punto = new Point(row, column, board[row][column]);

						column++;

						return punto;

					}

					@Override
					public void remove() {

					}

				};

			}
		};
	}
}
