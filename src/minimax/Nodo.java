package minimax;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	private Movida getMovidaPorProfundidad(Game estado, int profundidad) {

		for (int fila = 0; fila < estado.getSize(); fila++) {
			for (int columna = 0; columna < estado.getSize(); columna++) {

				try {
					if (estado.getFichaAt(fila, columna) != Ficha.ENEMIGO) {
						continue;
					}
				} catch (BoardPointOutOfBoundsException e1) {
					// TODO
				}

				for (Movida movida : estado.getPosiblesMovidas(new Punto(fila,
						columna))) {

					Game game = estado.copiar();

					try {
						game.mover(movida);
					} catch (MovimientoInvalidoException
							| BoardPointOutOfBoundsException
							| MovimientoBloqueadoException e) {
						// TODO
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

		if (estado.getTurno() == Jugador.ENEMIGO) {
			return Collections.max(hijos.values());
		} else {
			return Collections.min(hijos.values());
		}

	}

	public Game getEstado() {
		return estado;
	}

}
