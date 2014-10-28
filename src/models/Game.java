package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Game {

	private int[] cantidadDeFichas = new int[] { 0, 0 };

	private int size;

	private Ficha[][] tablero;

	private Jugador turno = Jugador.GUARDIA;

	public Game(int boardSize) {

		if (boardSize < 7 || boardSize > 19 || boardSize % 2 == 0) {
			throw new IllegalArgumentException(
					"Dimensiones del tablero invalidas: " + boardSize);
		}

		this.size = boardSize;

		this.tablero = new Ficha[size][size];

		popularTablero();

	}

	public Game(int size, Ficha[][] tablero, Jugador turno) {

		this.size = size;
		this.tablero = tablero;
		this.turno = turno;

	}

	private Punto buscarAlRey() {

		for (Punto punto : this.fichas()) {
			if (punto.getFicha() == Ficha.REY) {
				return punto;
			}
		}

		return null;

	}

	private void cambiarJugador() {

		if (turno == Jugador.GUARDIA) {
			turno = Jugador.ENEMIGO;
		} else if (turno == Jugador.ENEMIGO) {
			turno = Jugador.GUARDIA;
		}

	}

	public Game copiar() {
		Game tmpgame = new Game(size, null, turno);

		tmpgame.tablero = new Ficha[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				tmpgame.tablero[i][j] = tablero[i][j];

			}
		}

		tmpgame.cantidadDeFichas[Jugador.ENEMIGO.getIndice()] = cantidadDeFichas[Jugador.ENEMIGO
				.getIndice()];
		tmpgame.cantidadDeFichas[Jugador.GUARDIA.getIndice()] = cantidadDeFichas[Jugador.GUARDIA
				.getIndice()];

		return tmpgame;
	}

	public boolean esAliado(Punto punto) {

		return esAliado(punto.getFila(), punto.getColumna());
	}

	public boolean esAliado(int fila, int columna) {

		if (!puntoEsValido(fila, columna))
			return false;

		Ficha ficha = tablero[fila][columna];

		if (ficha == Ficha.CASTILLO) {
			return true;
		}

		if (turno == Jugador.GUARDIA
				&& (ficha == Ficha.REY || ficha == Ficha.GUARDIA || ficha == Ficha.TRONO))
			return true;

		if (turno == Jugador.ENEMIGO && ficha == Ficha.ENEMIGO)
			return true;

		return false;
	}

	public boolean esOponente(Punto punto) {

		return esOponente(punto.getFila(), punto.getColumna());
	}

	public boolean esOponente(int fila, int columna) {

		if (!puntoEsValido(fila, columna))
			return false;

		Ficha ficha = tablero[fila][columna];

		if (ficha == Ficha.CASTILLO) {
			return true;
		}

		if (turno == Jugador.GUARDIA && ficha == Ficha.ENEMIGO) {
			return true;
		}

		if (turno == Jugador.ENEMIGO
				&& (ficha == Ficha.REY || ficha == Ficha.GUARDIA)) {
			return true;
		}
		return false;

	}

	public boolean esTableroLargo() {
		return size >= 13;
	}

	public Ficha getFichaAt(int fila, int columna)
			throws BoardPointOutOfBoundsException {

		if (!puntoEsValido(fila, columna)) {
			throw new BoardPointOutOfBoundsException(fila, columna);
		}

		return tablero[fila][columna];

	}

	public List<Movida> getPosiblesMovidas(Punto origen) {

		List<Movida> lista = new ArrayList<Movida>();

		int fila, columna;

		// abajo
		for (columna = origen.getColumna(), fila = origen.getFila() + 1; fila < size
				&& tablero[fila][columna] == Ficha.VACIO; fila++) {

			lista.add(new Movida(origen, new Punto(fila, columna)));

		}

		// arriba
		for (columna = origen.getColumna(), fila = origen.getFila() - 1; fila >= 0
				&& tablero[fila][columna] == Ficha.VACIO; fila--) {

			lista.add(new Movida(origen, new Punto(fila, columna)));

		}

		// derecha
		for (fila = origen.getFila(), columna = origen.getColumna() + 1; columna < size
				&& tablero[fila][columna] == Ficha.VACIO; columna++) {

			lista.add(new Movida(origen, new Punto(fila, columna)));
		}

		// izquierda
		for (fila = origen.getFila(), columna = origen.getColumna() - 1; columna >= 0
				&& tablero[fila][columna] == Ficha.VACIO; columna--) {

			lista.add(new Movida(origen, new Punto(fila, columna)));
		}

		return lista;

	}

	public int getSize() {
		return size;
	}

	public char getTokenAt(int fila, int columna)
			throws BoardPointOutOfBoundsException {

		return getFichaAt(fila, columna).getToken();

	}

	public Jugador getTurno() {
		return turno;
	}

	public Jugador mover(Movida movida) throws MovimientoInvalidoException,
			BoardPointOutOfBoundsException, MovimientoBloqueadoException {
		return mover(movida.getOrigen(), movida.getDestino());
	}

	/*
	 * * Ejecuta el movimiento de _origen a _destino* Valida el turno, el camino
	 * y los bloqueos
	 */
	public Jugador mover(Punto origen, Punto destino)
			throws MovimientoInvalidoException, BoardPointOutOfBoundsException,
			MovimientoBloqueadoException {

		int medio = (size - 1) / 2;

		Ficha ficha = tablero[origen.getFila()][origen.getColumna()];

		/* El punto esta fuera del tablero */
		if (!puntoEsValido(origen) || !puntoEsValido(destino)) {
			throw new BoardPointOutOfBoundsException(origen);
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
				&& (destino.equals(0, 0) || destino.equals(0, size - 1)
						|| destino.equals(size - 1, 0) || destino.equals(
						size - 1, size - 1))) {
			throw new MovimientoInvalidoException(origen, destino);

		}

		/* Verifica si el destino es un castillo y la ficha es el rey */
		if (ficha == Ficha.REY
				&& (destino.equals(0, 0) || destino.equals(0, size - 1)
						|| destino.equals(size - 1, 0) || destino.equals(
						size - 1, size - 1))) {
			return Jugador.GUARDIA;
		}

		/* Chequea que no pueda ir una ficha que no sea el rey al trono */
		if (ficha != Ficha.REY && destino.equals(medio, medio)) {
			throw new MovimientoInvalidoException(origen, destino);
		}

		/* La ficha no se puede quedar en el mismo lugar */
		if (origen.equals(destino)) {
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

				if (!origen.equals(fila, columna)) {
					/*
					 * Verifica si el camino esta bloqueado por otra ficha o si
					 * es el tablero largo y no se es el enemigo
					 */

					Ficha tmpFicha = tablero[fila][columna];

					/*
					 * Verifica que todo el camino este libre, el caso del trono
					 * se analiza despues
					 */
					if (tmpFicha != Ficha.VACIO && tmpFicha != Ficha.TRONO) {
						throw new MovimientoBloqueadoException(new Punto(fila,
								columna));
					}

					/*
					 * Verifica que el tablero sea largo y los guardias no
					 * puedan pasar por el trono
					 */
					if (esTableroLargo() && tmpFicha == Ficha.TRONO
							&& ficha == Ficha.GUARDIA) {
						throw new MovimientoBloqueadoException(new Punto(fila,
								columna));
					}

				}

			}

		}

		if (origen.equals(medio, medio)) {
			tablero[origen.getFila()][origen.getColumna()] = Ficha.TRONO;
		} else {
			tablero[origen.getFila()][origen.getColumna()] = Ficha.VACIO;
		}

		tablero[destino.getFila()][destino.getColumna()] = ficha;

		return verificarBloqueos(destino);

	}

	private void popularTablero() {

		int maxIndex = size - 1;
		int medio = maxIndex / 2;

		tablero[0][0] = tablero[0][maxIndex] = tablero[maxIndex][0] = tablero[maxIndex][maxIndex] = Ficha.CASTILLO;

		tablero[medio][medio] = Ficha.REY;
		cantidadDeFichas[Jugador.GUARDIA.getIndice()] += 1;

		tablero[1][medio] = Ficha.ENEMIGO;
		tablero[medio][1] = Ficha.ENEMIGO;

		tablero[maxIndex - 1][medio] = Ficha.ENEMIGO;
		tablero[medio][maxIndex - 1] = Ficha.ENEMIGO;
		cantidadDeFichas[Jugador.ENEMIGO.getIndice()] += 4;

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
			cantidadDeFichas[Jugador.ENEMIGO.getIndice()] += 8;

		}

		for (int i = medio - 1; i <= medio + 1; i++) {
			for (int j = medio - 1; j <= medio + 1; j++) {
				if (i != medio || j != medio) {
					tablero[i][j] = Ficha.GUARDIA;
					cantidadDeFichas[Jugador.GUARDIA.getIndice()] += 1;

				}

			}
		}

		if (esTableroLargo()) {

			tablero[medio][medio + 2] = Ficha.GUARDIA;
			tablero[medio + 2][medio] = Ficha.GUARDIA;
			tablero[medio][medio - 2] = Ficha.GUARDIA;
			tablero[medio - 2][medio] = Ficha.GUARDIA;
			cantidadDeFichas[Jugador.GUARDIA.getIndice()] += 4;

		}

		for (int fila = 0; fila < size; fila++) {
			for (int columna = 0; columna < size; columna++) {
				if (tablero[fila][columna] == null) {
					tablero[fila][columna] = Ficha.VACIO;
				}
			}
		}

	}

	private boolean puntoEsValido(Punto punto) {

		return puntoEsValido(punto.getFila(), punto.getColumna());

	}

	private boolean puntoEsValido(int fila, int columna) {

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

	public int valorMagico() {

		int valor = cantidadDeFichas[Jugador.ENEMIGO.getIndice()]
				- cantidadDeFichas[Jugador.GUARDIA.getIndice()];

		int bloqueos = 0;
		int cuadranteMenor = 0;
		int matarAlRey = 0;

		// valor = valor * valor;

		Punto rey = buscarAlRey();

		if (rey == null) {
			return Integer.MAX_VALUE;
		}

		for (int columna = rey.getColumna() - 2; columna <= rey.getColumna() + 2; columna++) {
			for (int fila = rey.getFila() - 2; fila <= rey.getFila() + 2; fila++) {

				if (fila == rey.getFila() && columna == rey.getColumna()) {
					continue;
				}

				if (puntoEsValido(fila, columna)
						&& tablero[fila][columna] == Ficha.ENEMIGO) {
					bloqueos++;
				}

				if (puntoEsValido(fila, columna)
						&& tablero[fila][columna] == Ficha.ENEMIGO) {

					if (cuadranteMenor(fila, columna, rey.getFila(),
							rey.getColumna())) {
						cuadranteMenor++;
					}

					if (estaEncerrandoAlRey(fila, columna, rey.getFila(),
							rey.getColumna())) {
						matarAlRey++;
					}

				}
			}

		}

		// System.out.println("Valor: " + valor);
		// System.out.println("LA CANTIDAD DE BLOQUEOS ES: " + bloqueos);
		// System.out.println("LA CANTIDAD DE CUADRANTEMENOR ES: " +
		// cuadranteMenor);
		// System.out.println("LA CANTIDAD DE MATARALREY ES: " + matarAlRey);
		int heuristica = valor + 2 * bloqueos + 3 * cuadranteMenor + 6
				* matarAlRey;
		// System.out.println("EL VALORCITO ES : " + heuristica);
		return Math.abs(heuristica);
	}

	/*
	 * * Verifica si estoy en uno de los cuatro puntos validos para matar al Rey
	 */

	private boolean estaEncerrandoAlRey(int filaActual, int columnaActual,
			int filaRey, int columnaRey) {
		return (filaActual == filaRey && columnaActual == columnaRey + 1)
				|| (filaActual == filaRey && columnaActual == columnaRey - 1)
				|| (filaActual + 1 == filaRey && columnaActual == columnaRey)
				|| (filaActual - 1 == filaRey && columnaActual == columnaRey);
	}

	/*
	 * * Verifica si estoy en uno de los cuatro puntos que no matan al rey, pero
	 * estan en el cuadrante mas chico
	 */

	private boolean cuadranteMenor(int filaActual, int columnaActual,
			int filaRey, int columnaRey) {
		return (filaActual == filaRey - 1 && columnaActual == columnaRey - 1)
				|| (filaActual == filaRey + 1 && columnaActual == columnaRey + 1)
				|| (filaActual == filaRey - 1 && columnaActual == columnaRey + 1)
				|| (filaActual == filaRey + 1 && columnaActual == columnaRey - 1);
	}

	/*
	 * * Verifica si la ficha movida hacia _destino produjo algun bloqueo
	 */
	private Jugador verificarBloqueos(Punto destino) {

		/* Itera sobre los puntos adyacentes */
		for (int columna = destino.getColumna() - 1; columna <= destino
				.getColumna() + 1; columna++) {
			for (int fila = destino.getFila() - 1; fila <= destino.getFila() + 1; fila++) {

				if ((fila != destino.getFila() && columna != destino
						.getColumna()) || destino.equals(fila, columna)) {
					continue;
				}

				if (esOponente(fila, columna)) {

					if (tablero[fila][columna] == Ficha.REY) {

						int bloqueos = 0;

						int bloqueosPorGurdias = 0;

						/* Verifica que el rey este rodeado por 4 aliados */
						for (int tmpCol = columna - 1; tmpCol <= columna + 1; tmpCol++) {
							for (int tmpFil = fila - 1; tmpFil <= fila + 1; tmpFil++) {

								if (!((tmpFil != fila && tmpCol != columna) || (tmpFil == fila && tmpCol == columna))) {

									if (esAliado(tmpFil, tmpCol)
											|| !puntoEsValido(tmpFil, tmpCol)) {
										bloqueos++;

									} else if (esOponente(tmpFil, tmpCol)) {
										bloqueosPorGurdias++;
									}
								}

							}

						}

						/* El rey esta rodeado */
						if (bloqueos == 4
								|| (bloqueos == 3 && bloqueosPorGurdias == 1)) {
							tablero[fila][columna] = Ficha.REYMUERTO;
							return Jugador.ENEMIGO;
						}

					} else {
						/*
						 * La ficha oponente no es el rey, veo si esta capturada
						 */

						if (esAliado(
								destino.getFila() + (fila - destino.getFila())
										* 2, destino.getColumna()
										+ (columna - destino.getColumna()) * 2)) {

							cantidadDeFichas[tablero[fila][columna]
									.getJugador().getIndice()]--;

							tablero[fila][columna] = Ficha.VACIO;

							if (cantidadDeFichas[Jugador.ENEMIGO.getIndice()] == 0) {
								return Jugador.GUARDIA;
							}

						}

					}
				}

			}

		}

		cambiarJugador();

		return null;

	}

	public void setCantidadDeFichas(int enemigos, int guardias) {

		cantidadDeFichas[Jugador.ENEMIGO.getIndice()] = enemigos;
		cantidadDeFichas[Jugador.GUARDIA.getIndice()] = guardias;

	}

	public Iterable<Punto> fichasDelBandoActual() {

		return new Iterable<Punto>() {

			public Iterator<Punto> iterator() {

				return new Iterator<Punto>() {

					private Ficha[][] tablero = Game.this.tablero;

					private int fila = 0;
					private int columna = 0;

					@Override
					public boolean hasNext() {

						Ficha ficha;

						if (fila == size) {
							return false;
						}

						if (columna < size) {

							ficha = tablero[fila][columna];

							if ((turno == Jugador.ENEMIGO && ficha == Ficha.ENEMIGO)
									|| (turno == Jugador.GUARDIA && (ficha == Ficha.GUARDIA || ficha == Ficha.REY))) {
								return true;
							} else {

								columna++;
								return hasNext();
							}

						} else {
							columna = columna % size;
							fila++;

							return hasNext();

						}

					}

					@Override
					public Punto next() {

						Punto punto = new Punto(fila, columna,
								tablero[fila][columna]);

						columna++;

						return punto;

					}

					@Override
					public void remove() {

					}

				};

			}
		};
	}

	public Iterable<Punto> fichas() {

		return new Iterable<Punto>() {

			public Iterator<Punto> iterator() {

				return new Iterator<Punto>() {

					private Ficha[][] tablero = Game.this.tablero;

					private int fila = 0;
					private int columna = 0;

					@Override
					public boolean hasNext() {

						if (fila == size) {
							return false;
						}

						if (columna < size) {

							return true;

						} else {
							columna = columna % size;
							fila++;

							return hasNext();

						}

					}

					@Override
					public Punto next() {

						Punto punto = new Punto(fila, columna,
								tablero[fila][columna]);

						columna++;

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
