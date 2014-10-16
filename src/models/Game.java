package models;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Game {

	private int size;

	private Ficha[][] tablero;

	private Turno turno = Turno.ENEMIGOS;

	public Game(int boardSize) {

		if (boardSize < 7 || boardSize > 19 || boardSize % 2 == 0) {
			throw new IllegalArgumentException(
					"Dimensiones del tablero invalidas: " + boardSize);
		}

		this.size = boardSize;

		this.tablero = new Ficha[size][];

		for (int i = 0; i < size; i++) {

			tablero[i] = new Ficha[size];
			for (int j = 0; j < size; j++) {
				tablero[i][j] = Ficha.VACIO;

			}

		}

		popularTablero();

	}

	public Game(int size, Ficha[][] tablero, Turno turno) {

		this.size = size;
		this.tablero = tablero;
		this.turno = turno;

	}

	public boolean esAliado(Punto point) {

		if (!puntoEsValido(point))
			return false;

		Ficha ficha = tablero[point.getFila()][point.getColumna()];

		if (ficha == Ficha.CASTILLO) {
			return true;
		}

		if (turno == Turno.GUARDIAS
				&& (ficha == Ficha.REY || ficha == Ficha.GUARDIA || ficha == Ficha.TRONO))
			return true;

		if (turno == Turno.ENEMIGOS && ficha == Ficha.ENEMIGO)
			return true;

		return false;
	}

	public boolean esOponente(Punto point) {

		if (!puntoEsValido(point))
			return false;

		Ficha ficha = tablero[point.getFila()][point.getColumna()];

		if (ficha == Ficha.CASTILLO) {
			return true;
		}

		if (turno == Turno.GUARDIAS && ficha == Ficha.ENEMIGO) {
			return true;
		}

		if (turno == Turno.ENEMIGOS
				&& (ficha == Ficha.REY || ficha == Ficha.GUARDIA)) {
			return true;
		}
		return false;
	}

	public boolean esTableroLargo() {
		return size >= 13;
	}

	public int getSize() {
		return size;
	}

	public char getTokenAt(int fila, int columna)
			throws BoardPointOutOfBoundsException {

		return getFichaAt(fila, columna).getToken();

	}

	public Ficha getFichaAt(int fila, int columna)
			throws BoardPointOutOfBoundsException {

		Punto punto = new Punto(fila, columna);

		if (!puntoEsValido(punto)) {
			throw new BoardPointOutOfBoundsException(punto);
		}

		return tablero[fila][columna];

	}

	public Turno getTurno() {
		return turno;
	}

	public int mover(Punto origen, Punto destino)
			throws MovimientoBloqueadoException,
			BoardPointOutOfBoundsException, MovimientoInvalidoException {

		int medio = (size - 1) / 2;

		Ficha ficha = tablero[origen.getFila()][origen.getColumna()];

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
			throw new MovimientoInvalidoException(origen, destino);
		}

		if (!esAliado(origen)) {
			throw new MovimientoInvalidoException(origen, destino);

		}

		/* Verifica si el destino es un castillo y la ficha no es el rey */
		if (ficha != Ficha.REY
				&& (destino.equals(new Punto(0, 0))
						|| destino.equals(new Punto(0, size - 1))
						|| destino.equals(new Punto(size - 1, 0)) || destino
							.equals(new Punto(size - 1, size - 1)))) {
			throw new MovimientoInvalidoException(origen, destino);

		}

		/* Tablero corto y el destino es el trono */
		if (!esTableroLargo() && ficha == Ficha.REY
				&& destino.equals(new Punto(medio, medio))) {
			throw new MovimientoInvalidoException(origen, destino);

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
				if (tablero[fila][columna] != Ficha.VACIO
						|| (esTableroLargo()
								&& tablero[fila][columna] == Ficha.TRONO && ficha != Ficha.ENEMIGO)) {
					throw new MovimientoBloqueadoException(new Punto(fila,
							columna));
				}
			}

		}

		System.out.println("[" + origen.getFila() + ", " + origen.getColumna()
				+ "] -> [" + destino.getFila() + ", " + destino.getColumna()
				+ "]");

		if (origen.getFila() == medio && origen.getColumna() == medio) {
			tablero[origen.getFila()][origen.getColumna()] = Ficha.TRONO;
		} else {
			tablero[origen.getFila()][origen.getColumna()] = Ficha.VACIO;
		}

		tablero[destino.getFila()][destino.getColumna()] = ficha;

		return verificar_bloqueos(destino);

	}

	private void popularTablero() {

		int maxIndex = size - 1;
		int medio = maxIndex / 2;

		tablero[0][0] = tablero[0][maxIndex] = tablero[maxIndex][0] = tablero[maxIndex][maxIndex] = Ficha.CASTILLO;

		tablero[medio][medio] = Ficha.REY;

		tablero[1][medio] = Ficha.ENEMIGO;
		tablero[medio][1] = Ficha.ENEMIGO;

		tablero[maxIndex - 1][medio] = Ficha.ENEMIGO;
		tablero[medio][maxIndex - 1] = Ficha.ENEMIGO;

		int max = esTableroLargo() ? (5 - 1) / 2 : (3 - 1) / 2;

		for (int i = medio; i <= (medio + max); i++) {

			tablero[0][i] = Ficha.ENEMIGO;
			tablero[i][0] = Ficha.ENEMIGO;

			tablero[0][maxIndex - i] = Ficha.ENEMIGO;
			tablero[maxIndex - i][0] = Ficha.ENEMIGO;

			tablero[maxIndex][i] = Ficha.ENEMIGO;
			tablero[i][maxIndex] = Ficha.ENEMIGO;

			tablero[maxIndex][maxIndex - i] = Ficha.ENEMIGO;
			tablero[maxIndex - i][maxIndex] = Ficha.ENEMIGO;

		}

		for (int i = medio - 1; i <= medio + 1; i++) {
			for (int j = medio - 1; j <= medio + 1; j++) {
				if (i != medio || j != medio) {
					tablero[i][j] = Ficha.GUARDIA;
				}

			}
		}

		if (esTableroLargo()) {

			tablero[medio][medio + 2] = Ficha.GUARDIA;
			tablero[medio + 2][medio] = Ficha.GUARDIA;
			tablero[medio][medio - 2] = Ficha.GUARDIA;
			tablero[medio - 2][medio] = Ficha.GUARDIA;

		}

	}

	private boolean puntoEsValido(Punto point) {

		return point.getFila() >= 0 && point.getFila() < size
				&& point.getColumna() < size && point.getColumna() >= 0;

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
						+ (tablero[i][j] == Ficha.VACIO ? " " : tablero[i][j]
								.getToken()) + " ";

			}
			str += "|\n\n";
		}

		for (int i = 0; i < len; i++) {
			str += "_  ";
		}

		return str;

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

					if (tablero[fila][columna] == Ficha.REY) {

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

						tablero[fila][columna] = Ficha.VACIO;

					}

				}

			}

		}

		if (turno == Turno.GUARDIAS)

			turno = Turno.ENEMIGOS;

		else if (turno == Turno.ENEMIGOS)

			turno = Turno.GUARDIAS;

		return 0;

	}
}
