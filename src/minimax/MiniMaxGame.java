package minimax;

import models.Game;
import models.Jugador;
import models.Movida;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class MiniMaxGame {

	private Nodo currentState;

	public MiniMaxGame(Game juegoInicial) {

		currentState = new Nodo(juegoInicial);

	}

	public Game getCurrentGame() {
		return currentState.getEstado();
	}

	public Movida getMejorMovida() {
		return currentState.getMovidaPorProfundidad(1);
	}

	public boolean ejecutarMovidaDeEnemigo() {
		Movida movida = getMejorMovida();

		Jugador result = null;
		try {
			result = currentState.getEstado().mover(movida);
		} catch (MovimientoInvalidoException | BoardPointOutOfBoundsException
				| MovimientoBloqueadoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		currentState = new Nodo(currentState.getEstado());

		if (result != null) {
			return true;
		}

		return false;

	}

}
