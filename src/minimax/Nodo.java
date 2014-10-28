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

	public Movida getMovidaPorProfundidad(int profundidad, boolean prune) {
		Integer valorPoda;
		int signo = -1;
		if (!prune) {
			// Poda deshabilitada desde argumentos de ejecuci�n.
			valorPoda = null;
		} else if (estado.getTurno() == Jugador.ENEMIGO) {
			// Si soy MAX nunca va a cortar la primera rama porque
			// Alfa<+Infinito, por m�s grande que sea.
			valorPoda = Integer.MAX_VALUE;
		} else {
			// Si soy MIN nunca va a cortar la primera rama porque
			// Beta>-Infinito, por m�s chico que sea.
			valorPoda = signo * Integer.MAX_VALUE;
		}

		if (gvw != null) {
			gvw.addNode(null, this, this.estado.getTurno() == Jugador.GUARDIA);
		}

		return getMejorEnProfundidad(this, profundidad, valorPoda).movida;
	}

	// Uso valor de poda
	private Nodo getMejorEnProfundidad(Nodo nodo, int profundidad,
			Integer valorPoda) {

		// boolean podar = false;

		Nodo mejorHijo = null;

		for (Punto punto : nodo.estado.fichasDelBandoActual()) {

			for (Movida movida : nodo.estado.getPosiblesMovidas(punto)) {

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
								profundidad - 1, valorPoda).valor;
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

				} catch (MovimientoInvalidoException
						| BoardPointOutOfBoundsException
						| MovimientoBloqueadoException e) {
					System.out.println("Nodo.getMovidaPorProfundidad()");
				}

				// // valorPoda==null si est� deshabilitada la poda desde
				// // argumentos.
				// if (valorPoda != null
				// && podar(valorPoda, nuevaMovida.getValor(), nodo
				// .getEstado().getTurno())) {
				//
				// // es necesario porque despues recorre el
				// // Collections.max con los nodos que quedaron.
				// hijo.movida.setValor(nuevaMovida.getValor());
				// break;
				//
				// } else {
				// valorPoda = nuevaMovida.getValor();
				// }
				//
				// hijo.movida.setValor(nuevaMovida.getValor());

			}

		}

		// Hay nodos a los cuales no se les pis� el valor heur�stico en la
		// movida, pero fueron creados porque se lo hace al principio (caso nodo
		// C del ejemplo), habria que
		// eliminarlos adem�s de hacer un break en el for no? Sino cuando
		// obtenga el min o el max voy a comparar contra cualquier cosa. Tendr�a
		// que eliminar los proximos con iterator.
		// VER ACA CASO DE PODA DEL NODO F DEL EJEMPLO!! cuando estoy a
		// profundidad 1, no estoy teniendo en cuenta la poda sino.

		mejorHijo.movida.setElegida(true);

		if (gvw != null) {
			// Es la meor movida, todavia no la agregamos
			gvw.addNode(nodo, mejorHijo,
					nodo.estado.getTurno() == Jugador.GUARDIA);
		}
		return mejorHijo;

	}

	private boolean podar(Integer valorPoda, Integer actual, Jugador turno) {

		if (turno == Jugador.ENEMIGO) {
			int alfa = actual;
			if (alfa >= valorPoda) {
				return true;
			}

		} else {

			int beta = actual;
			if (beta <= valorPoda) {
				return true;
			}
		}

		return false;
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
