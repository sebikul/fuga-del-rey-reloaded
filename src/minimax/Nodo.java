package minimax;

import models.Game;
import models.Jugador;
import models.Movida;
import models.Punto;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Nodo {

	private Game estado;

	private Movida movida;
	private int valor;

	public int getValor() {
		return valor;
	}

	private final GraphVizWriter gvw;

	public Nodo(Game estado, Movida movida, GraphVizWriter gvw) {
		this.movida = movida;
		this.estado = estado;
		this.gvw = gvw;
	}

	public Movida getMovidaPorProfundidad(int profundidad, boolean prune,
			long maxTime) {
		Integer alfa, beta;

		int signo = -1;
		if (!prune) {

			alfa = null;
			beta = null;
		} else {
			alfa = signo * Integer.MAX_VALUE;
			beta = Integer.MAX_VALUE;
		}

		if (gvw != null) {
			gvw.addNode(null, this, this.estado.getTurno() == Jugador.GUARDIA);
		}

		return getMejorEnProfundidad(this, profundidad, alfa, beta, maxTime).movida;

	}

	private Nodo getMejorEnProfundidad(Nodo nodo, int profundidad,
			Integer alfa, Integer beta, long maxTime) {

		boolean podar = false;

		Nodo mejorHijo = null;

		for (Punto punto : nodo.estado.fichasDelBandoActual()) {

			for (Movida movida : nodo.estado.getPosiblesMovidas(punto)) {

				// Si en la evaluaci�n de la anterior movida se determin�
				// una
				// poda, se setan las siguientes movidas como podadas para luego
				// incluirlas al �rbol.
				if (podar) {
					movida.setPodada(true);
					continue;
				}

				Game game = nodo.estado.copiar();

				try {

					int signo = 1;

					if (game.getTurno() == Jugador.GUARDIA) {
						signo = -1;
					}

					Jugador result = game.mover(movida);

					Nodo hijo = new Nodo(game, movida, gvw);

					if (result == null) {
						hijo.valor = game.valorMagico();
					} else if (result == nodo.estado.getTurno()) {
						hijo.valor = Integer.MAX_VALUE * signo;
						hijo.movida.setElegida(true);

						if (gvw != null) {
							gvw.addNode(nodo, hijo,
									nodo.estado.getTurno() == Jugador.GUARDIA);
						}
						return hijo;
					}

					if (profundidad > 1) {

						hijo.valor = hijo.getMejorEnProfundidad(hijo,
								profundidad - 1, alfa, beta, maxTime).valor;

						if (alfa != null && beta != null) {
							if (estado.getTurno() == Jugador.ENEMIGO) {
								alfa = hijo.valor;
								if (alfa >= beta)
									podar = true;

							} else {
								beta = hijo.valor;
								if (beta <= alfa)
									podar = true;
							}
						}

					}

					if (mejorHijo == null) {
						mejorHijo = hijo;
					} else if ((game.getTurno() == Jugador.GUARDIA && mejorHijo.valor < hijo.valor)
							|| (game.getTurno() == Jugador.ENEMIGO && mejorHijo.valor > hijo.valor)) {
						mejorHijo = hijo;
					} else {

						if (gvw != null) {
							// La movida es descartada, la agrego al arbol
							gvw.addNode(nodo, hijo,
									nodo.estado.getTurno() == Jugador.GUARDIA);

						}
					}

					if (alfa != null && beta != null && profundidad == 1) {
						if (estado.getTurno() == Jugador.ENEMIGO) {
							alfa = hijo.valor;
							if (alfa >= beta)
								podar = true;

						} else {
							beta = hijo.valor;
							if (beta <= alfa)
								podar = true;
						}
					}

				} catch (MovimientoInvalidoException
						| BoardPointOutOfBoundsException
						| MovimientoBloqueadoException e) {
					System.out.println("Nodo.getMovidaPorProfundidad()");
				}

			}

			if (maxTime != 0 && System.currentTimeMillis() > maxTime) {
				// System.out.println("Cortando por limite de tiempo");
				// System.out.println("Nos pasamos "
				// + (System.currentTimeMillis() - maxTime));
				return new Nodo(null, null, null);
			}

		}

		mejorHijo.movida.setElegida(true);

		if (gvw != null) {
			// Es la meor movida, todavia no la agregamos
			gvw.addNode(nodo, mejorHijo,
					nodo.estado.getTurno() == Jugador.GUARDIA);
		}

		return mejorHijo;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((movida == null) ? 0 : movida.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nodo other = (Nodo) obj;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (movida == null) {
			if (other.movida != null)
				return false;
		} else if (!movida.equals(other.movida))
			return false;
		return true;
	}

	public Game getEstado() {
		return estado;
	}

	public Movida getMovida() {
		return movida;
	}

	public void setMovida(Movida movida) {
		this.movida = movida;
	}
}
