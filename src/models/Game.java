package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Game {

	private int size;

	private Ficha[][] tablero;

	private Jugador turno = Jugador.GUARDIA;

	private int[] cantidadDeFichas = new int[] { 0, 0 };

	public Game(int boardSize) {

		if (boardSize < 7 || boardSize > 19 || boardSize % 2 == 0) {
			throw new IllegalArgumentException(
					"Dimensiones del tablero invalidas: " + boardSize);
		}

		this.size = boardSize;

		this.tablero = new Ficha[size][size];

		popularTablero();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int medio = (size - 1) / 2;

		int result = 1;
		result = prime * result + Arrays.hashCode(tablero[0]);

		result = prime * result + Arrays.hashCode(tablero[medio - 1]);
		result = prime * result + Arrays.hashCode(tablero[medio]);
		result = prime * result + Arrays.hashCode(tablero[medio + 1]);

		result = prime * result + Arrays.hashCode(tablero[size - 1]);

		result = prime * result + ((turno == null) ? 0 : turno.hashCode());
		return result;
	}

	public int valorMagico() {

		int valor = cantidadDeFichas[Jugador.ENEMIGO.getIndice()]
				- cantidadDeFichas[Jugador.GUARDIA.getIndice()];

		int bloqueos = 0;

		Punto rey = buscarAlRey();

		if (rey == null) {
			return Integer.MAX_VALUE;
		}

		for (int columna = rey.getColumna() - 1; columna <= rey.getColumna() + 1; columna++) {
			for (int fila = rey.getFila() - 1; fila <= rey.getFila() + 1; fila++) {

				if (fila == rey.getFila() && columna == rey.getColumna()) {
					continue;
				}

				try {
					if (getFichaAt(fila, columna) == Ficha.ENEMIGO) {
						bloqueos++;
					}
				} catch (BoardPointOutOfBoundsException e) {
					e.printStackTrace();
				}
			}

		}

		return valor + 2 * bloqueos;
	}

	private Punto buscarAlRey() {

		for (int fila = 0; fila < size; fila++) {
			for (int columna = 0; columna < size; columna++) {
				if (tablero[fila][columna] == Ficha.REY) {
					return new Punto(fila, columna);
				}
			}
		}

		return null;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (size != other.size)
			return false;
		if (!Arrays.deepEquals(tablero, other.tablero))
			return false;
		if (turno != other.turno)
			return false;
		return true;
	}

	public Game(int size, Ficha[][] tablero, Jugador turno) {

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

		if (turno == Jugador.GUARDIA
				&& (ficha == Ficha.REY || ficha == Ficha.GUARDIA || ficha == Ficha.TRONO))
			return true;

		if (turno == Jugador.ENEMIGO && ficha == Ficha.ENEMIGO)
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
				&& (destino.equals(new Punto(0, 0))
						|| destino.equals(new Punto(0, size - 1))
						|| destino.equals(new Punto(size - 1, 0)) || destino
							.equals(new Punto(size - 1, size - 1)))) {
			throw new MovimientoInvalidoException(origen, destino);

		}

		/* Verifica si el destino es un castillo y la ficha es el rey */
		if (ficha == Ficha.REY
				&& (destino.equals(new Punto(0, 0))
						|| destino.equals(new Punto(0, size - 1))
						|| destino.equals(new Punto(size - 1, 0)) || destino
							.equals(new Punto(size - 1, size - 1)))) {
			return Jugador.GUARDIA;
		}

		/* Chequea que no pueda ir una ficha que no sea el rey al trono */
		if (ficha != Ficha.REY && destino.equals(new Punto(medio, medio))) {
			throw new MovimientoInvalidoException(origen, destino);
		}

		/* La ficha no se puede quedar en el mismo lugar */
		if (origen.getFila() == destino.getFila()
				&& origen.getColumna() == destino.getColumna()) {
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

				if (!origen.equals(new Punto(fila, columna))) {
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
					 * Verifica que el tablero se a largo y los guardias no
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

		if (origen.getFila() == medio && origen.getColumna() == medio) {
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

		//
		// TODO
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

	/*
	 * * Verifica si la ficha movida hacia _destino produjo algun bloqueo
	 */
	private Jugador verificarBloqueos(Punto destino) {

		// int j = 0;

		Punto pos_aliado, pos_enemigo;

		/* Itera sobre los puntos adyacentes */
		for (int columna = destino.getColumna() - 1; columna <= destino
				.getColumna() + 1; columna++) {
			for (int fila = destino.getFila() - 1; fila <= destino.getFila() + 1; fila++) {

				if ((fila != destino.getFila() && columna != destino
						.getColumna())
						|| (fila == destino.getFila() && columna == destino
								.getColumna())) {
					continue;
				}

				pos_enemigo = new Punto(fila, columna);

				if (esOponente(pos_enemigo)) {

					if (tablero[fila][columna] == Ficha.REY) {

						int bloqueos = 0;

						/* Verifica que el rey este rodeado por 4 aliados */
						for (int col_aux = columna - 1; col_aux <= columna + 1; col_aux++) {
							for (int fil_aux = fila - 1; fil_aux <= fila + 1; fil_aux++) {

								if (!((fil_aux != fila && col_aux != columna) || (fil_aux == fila && col_aux == columna))) {

									pos_aliado = new Punto(fil_aux, col_aux);

									if (!puntoEsValido(pos_aliado)
											|| tablero[pos_aliado.getFila()][pos_aliado
													.getColumna()] != Ficha.VACIO) {
										bloqueos++;
									}
								}

							}

						}
						/* El rey esta rodeado */
						if (bloqueos == 4) {
							tablero[fila][columna] = Ficha.REYMUERTO;
							return Jugador.ENEMIGO;
						}

					} else {
						/*
						 * La ficha oponente no es el rey, veo si esta capturada
						 */

						pos_aliado = new Punto(destino.getFila()
								+ (fila - destino.getFila()) * 2,
								destino.getColumna()
										+ (columna - destino.getColumna()) * 2);

						if (esAliado(pos_aliado)) {

							cantidadDeFichas[tablero[fila][columna]
									.getJugador().getIndice()]--;

							tablero[fila][columna] = Ficha.VACIO;

						}

					}
				}

			}

		}

		cambiarJugador();

		return null;

	}

	/*
	 * * Cambia de jugador en la estructura _*juego
	 */
	private void cambiarJugador() {

		if (turno == Jugador.GUARDIA) {

			turno = Jugador.ENEMIGO;

		} else if (turno == Jugador.ENEMIGO) {

			turno = Jugador.GUARDIA;
		}

	}

	public List<Movida> getPosiblesMovidas(Punto origen) {

		List<Movida> lista = new ArrayList<Movida>();

		int fila, columna;

		Ficha ficha;

		// abajo
		for (columna = origen.getColumna(), fila = origen.getFila(); fila < size; fila++) {

			if (fila == origen.getFila() && columna == origen.getColumna()) {
				continue;
			}

			ficha = tablero[fila][columna];

			if (ficha != Ficha.VACIO) {
				break;
			}

			lista.add(new Movida(origen, new Punto(fila, columna)));

		}

		// arriba
		for (columna = origen.getColumna(), fila = origen.getFila(); fila >= 0; fila--) {

			if (fila == origen.getFila() && columna == origen.getColumna()) {
				continue;
			}

			ficha = tablero[fila][columna];

			if (ficha != Ficha.VACIO) {
				break;
			}

			lista.add(new Movida(origen, new Punto(fila, columna)));

		}

		// derecha
		for (fila = origen.getFila(), columna = origen.getColumna(); columna > size; columna++) {

			if (fila == origen.getFila() && columna == origen.getColumna()) {
				continue;
			}

			ficha = tablero[fila][columna];

			if (ficha != Ficha.VACIO) {
				break;
			}

			lista.add(new Movida(origen, new Punto(fila, columna)));
		}

		// izquierda
		for (fila = origen.getFila(), columna = origen.getColumna(); columna >= 0; columna--) {

			if (fila == origen.getFila() && columna == origen.getColumna()) {
				continue;
			}

			ficha = tablero[fila][columna];

			if (ficha != Ficha.VACIO) {
				break;
			}

			lista.add(new Movida(origen, new Punto(fila, columna)));
		}

		// TODO
		return lista;

	}

	public Game copiar() {
		Game tmpgame = new Game(size, null, turno);

		tmpgame.tablero = new Ficha[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				tmpgame.tablero[i][j] = tablero[i][j];

			}
		}

		return tmpgame;
	}

}
