package models;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Game {

	private char[][] tablero;

	private int size;

	private int turno = JUGADOR_ENEMIGO;

	static private char TABLERO_CASILLERO_VACIO = ' ';

	static private int JUGADOR_ENEMIGO = 0;
	static private int JUGADOR_GUARDIAS = 1;

	static private char TABLERO_TRONO = 'T';
	static private char TABLERO_CASTILLO = 'C';

	static private char TABLERO_REY = 'K';
	static private char TABLERO_GUARDIA = 'G';
	static private char TABLERO_ENEMIGO = 'E';

	public Game(int boardSize) {

		if (boardSize < 7 || boardSize > 19 || boardSize % 2 == 0) {
			throw new IllegalArgumentException(
					"Dimensiones del tablero invalidas: " + boardSize);
		}

		this.size = boardSize;

		this.tablero = new char[size][];

		for (int i = 0; i < size; i++) {

			tablero[i] = new char[size];
			for (int j = 0; j < size; j++) {
				tablero[i][j] = TABLERO_CASILLERO_VACIO;
			}

		}

		popularTablero();

	}

	public boolean esTableroLargo() {
		return size >= 13;
	}

	private void popularTablero() {

		int maxIndex = size - 1;
		int medio = maxIndex / 2;

		tablero[0][0] = tablero[0][maxIndex] = tablero[maxIndex][0] = tablero[maxIndex][maxIndex] = TABLERO_CASTILLO;

		tablero[medio][medio] = TABLERO_TRONO;

		tablero[1][medio] = TABLERO_ENEMIGO;
		tablero[medio][1] = TABLERO_ENEMIGO;

		tablero[maxIndex - 1][medio] = TABLERO_ENEMIGO;
		tablero[medio][maxIndex - 1] = TABLERO_ENEMIGO;

		int max = esTableroLargo() ? (5 - 1) / 2 : (3 - 1) / 2;

		for (int i = medio; i <= (medio + max); i++) {

			tablero[0][i] = TABLERO_ENEMIGO;
			tablero[i][0] = TABLERO_ENEMIGO;

			tablero[0][maxIndex - i] = TABLERO_ENEMIGO;
			tablero[maxIndex - i][0] = TABLERO_ENEMIGO;

			tablero[maxIndex][i] = TABLERO_ENEMIGO;
			tablero[i][maxIndex] = TABLERO_ENEMIGO;

			tablero[maxIndex][maxIndex - i] = TABLERO_ENEMIGO;
			tablero[maxIndex - i][maxIndex] = TABLERO_ENEMIGO;

		}

		for (int i = medio - 1; i <= medio + 1; i++) {
			for (int j = medio - 1; j <= medio + 1; j++) {
				if (i != medio || j != medio) {
					tablero[i][j] = TABLERO_GUARDIA;
				}

			}
		}

		if (esTableroLargo()) {

			tablero[medio][medio + 2] = TABLERO_GUARDIA;
			tablero[medio + 2][medio] = TABLERO_GUARDIA;
			tablero[medio][medio - 2] = TABLERO_GUARDIA;
			tablero[medio - 2][medio] = TABLERO_GUARDIA;

		}

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
						+ (tablero[i][j] == TABLERO_CASILLERO_VACIO ? " "
								: tablero[i][j]) + " ";

			}
			str += "|\n\n";
		}

		for (int i = 0; i < len; i++) {
			str += "_  ";
		}

		return str;

	}

	private boolean puntoEsValido(Punto point) {

		return point.getFila() < size && point.getColumna() < size;

	}

	public boolean esOponente(Punto point) {

		if (!puntoEsValido(point))
			return false;

		char piece = tablero[point.getFila()][point.getColumna()];

		if (piece == TABLERO_CASTILLO) {
			return true;
		}

		if (turno == JUGADOR_GUARDIAS && piece == TABLERO_ENEMIGO) {
			return true;
		}

		if (turno == JUGADOR_GUARDIAS
				&& (piece == TABLERO_REY || piece == TABLERO_GUARDIA)) {
			return true;
		}
		return false;
	}

	public boolean esAliado(Punto point) {

		if (!puntoEsValido(point))
			return false;

		char piece = tablero[point.getFila()][point.getColumna()];

		if (piece == TABLERO_CASTILLO) {
			return true;
		}

		if (turno == JUGADOR_GUARDIAS
				&& (piece == TABLERO_REY || piece == TABLERO_GUARDIA || piece == TABLERO_TRONO))
			return true;

		if (turno == JUGADOR_ENEMIGO && piece == TABLERO_ENEMIGO)
			return true;

		return false;
	}

	public int mover(Punto origen, Punto destino)
			throws MovimientoBloqueadoException,
			BoardPointOutOfBoundsException, MovimientoInvalidoException {

		int medio = (size - 1) / 2;

		char ficha = tablero[origen.getFila()][origen.getColumna()];

		/* El punto esta fuera del tablero */
		if (!puntoEsValido(origen)) {
			throw new BoardPointOutOfBoundsException(origen);
		}

		if (!puntoEsValido(destino)) {
			throw new BoardPointOutOfBoundsException(destino);
		}

		/* La fila ó columna de origen y destino no coinciden */
		if (origen.getFila() != destino.getFila()
				&& origen.getColumna() != destino.getColumna()) {
			throw new MovimientoInvalidoException();
		}

		if (!esAliado(origen)) {
			throw new MovimientoInvalidoException();

		}

		/* Verifica si el destino es un castillo y la ficha no es el rey */
		if (ficha != TABLERO_REY
				&& (destino.equals(new Punto(0, 0))
						|| destino.equals(new Punto(0, size - 1))
						|| destino.equals(new Punto(size - 1, 0)) || destino
							.equals(new Punto(size - 1, size - 1)))) {
			throw new MovimientoInvalidoException();

		}

		/* Tablero corto y el destino es el trono */
		if (!esTableroLargo() && ficha == TABLERO_REY
				&& destino.equals(new Punto(medio, medio))) {
			throw new MovimientoInvalidoException();

		}

		/* Se verifica que el camino este vacio, y sea valido */
		for (int fila = origen.getFila(); origen.getFila() < destino.getFila() ? fila <= destino
				.getFila() : fila >= destino.getFila(); fila += (origen
				.getFila() < destino.getFila() ? 1 : -1)) {

			for (int columna = origen.getColumna(); origen.getColumna() < destino
					.getColumna() ? columna <= destino.getColumna()
					: columna >= destino.getColumna(); columna += origen
					.getColumna() < destino.getColumna() ? 1 : -1) {

				if (origen.equals(new Punto(fila, columna))) {
					continue;
				}

				/*
				 * Verifica si el camino esta bloqueado por otra ficha o si es
				 * el tablero largo y no se es el enemigo
				 */
				if (tablero[fila][columna] != TABLERO_CASILLERO_VACIO
						|| (esTableroLargo()
								&& tablero[fila][columna] == TABLERO_TRONO && ficha != TABLERO_ENEMIGO)) {
					throw new MovimientoBloqueadoException();
				}
			}

		}

		// printf("[%d, %d] -> [%d, %d]\n",
		// origen.fila,origen.columna,destino.fila,destino.columna);

		if (origen.getFila() == medio && origen.getColumna() == medio) {
			tablero[origen.getFila()][origen.getColumna()] = TABLERO_TRONO;
		} else {
			tablero[origen.getFila()][origen.getColumna()] = TABLERO_CASILLERO_VACIO;
		}

		tablero[destino.getFila()][destino.getColumna()] = ficha;

		return verificar_bloqueos(destino);

	}

	int verificar_bloqueos(Punto destino) {

		int i = 0, fil_aux, col_aux;

		/* Itera sobre los puntos adyacentes */
		for (int columna = destino.getColumna() - 1; columna <= destino
				.getColumna() + 1; columna++) {
			for (int fila = destino.getFila() - 1; fila <= destino.getFila() + 1; fila++) {

				if ((fila != destino.getFila() && columna != destino
						.getColumna())
						|| (fila == destino.getFila() && columna == destino
								.getColumna()))
					continue;

				/* printf("%d, %d\n", fil,col); */

				if (esOponente(new Punto(fila, columna))) {

					// printf("Enemigo en "); PRINT_PUNTO(pos_enemigo);
					// putchar('\n');

					if (tablero[fila][columna] == TABLERO_REY) {

						// printf("Detectado rey enemigo en ");
						// PRINT_PUNTO(pos_enemigo); putchar('\n');

						/* Verifica que el rey este rodeado por 4 aliados */
						for (col_aux = columna - 1; col_aux <= columna + 1; col_aux++) {
							for (fil_aux = fila - 1; fil_aux <= fila + 1; fil_aux++) {

								// printf("Analizando [%d, %d]\n",
								// fil_aux,col_aux);

								if ((fil_aux != fila && col_aux != columna)
										|| (fil_aux == fila && col_aux == columna))
									continue;

								if (esAliado(new Punto(fil_aux, col_aux))) {
									i++;
									// printf("Rey atacado por ");
									// PRINT_PUNTO(pos_aliado); putchar('\n');
								}

							}
							/* El rey esta rodeado */
							if (i == 4)
								return 1;
						}

						continue;
					}

					/* La ficha oponente no es el rey, veo si esta capturada */

					if (esAliado(new Punto(destino.getFila()
							+ (fila - destino.getFila()) * 2,
							destino.getColumna()
									+ (columna - destino.getColumna()) * 2))) {

						// printf("Aliado en "); PRINT_PUNTO(pos_aliado);

						// printf("Ficha en posicion [%d, %d] capturada.\n",
						// fil,col);

						/* TODO: vector con fichas capturadas */

						tablero[fila][columna] = TABLERO_CASILLERO_VACIO;

					}

				}

			}

		}

		if (turno == JUGADOR_GUARDIAS)

			turno = JUGADOR_ENEMIGO;

		else if (turno == JUGADOR_ENEMIGO)

			turno = JUGADOR_GUARDIAS;

		return 0;

	}
}
