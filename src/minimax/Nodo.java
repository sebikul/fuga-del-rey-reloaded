package minimax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Ficha;
import models.Game;
import models.Jugador;
import models.Movida;
import models.Punto;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Nodo implements Comparable<Nodo> {

	private Game estado;

	private Movida movida;
	private List<Nodo> hijos = new ArrayList<Nodo>();

	public Nodo(Game estado, Movida movida) {
		this.movida = movida;
		this.estado = estado;
	}

	public Movida getMovidaPorProfundidad(int profundidad) {
		return getMovidaPorProfundidad(this, profundidad);
	}

	private Movida getMovidaPorProfundidad(Nodo nodo, int profundidad) {

		calcularNodosHijo(nodo);

		if (profundidad > 1) {

			for (Nodo hijo : nodo.hijos) {

				Movida nuevaMovida = getMovidaPorProfundidad(hijo,
						profundidad - 1);

				hijo.getMovida().setValor(nuevaMovida.getValor());

			}

		}

		if (estado.getTurno() == Jugador.ENEMIGO) {
			return Collections.max(hijos).getMovida();
		} else {
			return Collections.min(hijos).getMovida();
		}

	}

	private void calcularNodosHijo(Nodo nodo) {

		for (int fila = 0; fila < nodo.estado.getSize(); fila++) {
			for (int columna = 0; columna < nodo.estado.getSize(); columna++) {

				try {

					Ficha ficha = nodo.estado.getFichaAt(fila, columna);

					if ((nodo.estado.getTurno() == Jugador.ENEMIGO && ficha != Ficha.ENEMIGO)
							|| (nodo.estado.getTurno() == Jugador.GUARDIA && (ficha != Ficha.GUARDIA && ficha != Ficha.REY))) {
						continue;
					}
				} catch (BoardPointOutOfBoundsException e1) {

				}

				for (Movida movida : nodo.estado.getPosiblesMovidas(new Punto(
						fila, columna))) {

					Game game = nodo.estado.copiar();

					try {

						Jugador result = game.mover(movida);

						int signo = 1;

						if (game.getTurno() == Jugador.GUARDIA) {
							signo = -1;
						}

						movida.setValor(((result == null) ? game.valorMagico()
								: Integer.MAX_VALUE) * signo);
					} catch (MovimientoInvalidoException
							| BoardPointOutOfBoundsException
							| MovimientoBloqueadoException e) {
						System.out.println("Nodo.getMovidaPorProfundidad()");
					}

					nodo.hijos.add(new Nodo(game, movida));
				}

			}
		}

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

	@Override
	public int compareTo(Nodo o) {

		return movida.getValor() - o.getMovida().getValor();
	}

	public List<Nodo> getHijos() {
		return Collections.unmodifiableList(hijos);
	}

	public Movida getMovida() {
		return movida;
	}

	public void setMovida(Movida movida) {
		this.movida = movida;
	}
}
