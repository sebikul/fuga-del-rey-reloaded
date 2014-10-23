package minimax;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.Ficha;
import models.Game;
import models.Jugador;
import models.Movida;
import models.Punto;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Nodo {

	private Game estado;
	private Map<Nodo, Movida> hijos = new HashMap<Nodo, Movida>();

	public Nodo(Game estado) {
		this.estado = estado;
	}

	public Movida getMovidaPorProfundidad(int profundidad) {
		return getMovidaPorProfundidad(estado, profundidad);
	}

	private Movida getMovidaPorProfundidad(Game estado, int profundidad) {

		for (int fila = 0; fila < estado.getSize(); fila++) {
			for (int columna = 0; columna < estado.getSize(); columna++) {

				try {

					Ficha ficha = estado.getFichaAt(fila, columna);

					if ((estado.getTurno() == Jugador.ENEMIGO && ficha != Ficha.ENEMIGO)
							|| (estado.getTurno() == Jugador.GUARDIA && (ficha != Ficha.GUARDIA || ficha != Ficha.REY))) {
						continue;
					}
				} catch (BoardPointOutOfBoundsException e1) {

				}

				for (Movida movida : estado.getPosiblesMovidas(new Punto(fila,
						columna))) {

					Game game = estado.copiar();

					try {

						game.mover(movida);

						movida.setValor(game.valorMagico());
					} catch (MovimientoInvalidoException
							| BoardPointOutOfBoundsException
							| MovimientoBloqueadoException e) {

					}

					hijos.put(new Nodo(game), movida);
				}

			}

		}

		if ((profundidad == 1 && estado.getTurno() == Jugador.ENEMIGO)
				|| (profundidad == 2 && estado.getTurno() == Jugador.GUARDIA)) {

			return Collections.max(hijos.values());

		}

		for (Nodo nodo : hijos.keySet()) {

			Movida movida = hijos.get(nodo);

			Movida nuevaMovida = getMovidaPorProfundidad(estado,
					profundidad - 1);

			movida.setValor(nuevaMovida.getValor());

		}

		Movida movida;

		if (estado.getTurno() == Jugador.ENEMIGO) {
			movida = Collections.max(hijos.values());
		} else {
			movida = Collections.min(hijos.values());
		}

		// System.out.println(movida);
		return movida;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
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
		return true;
	}

	public Game getEstado() {
		return estado;
	}

	public Map<Nodo, Movida> getHijos() {
		return Collections.unmodifiableMap(hijos);
	}
}
