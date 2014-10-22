package minimax;

import models.Game;
import models.Movida;

public class MiniMaxGame {

	private Nodo currentState;

	// private Map<Nodo> estados = new HashMap<Nodo>();

	public MiniMaxGame(Game juegoInicial) {

		currentState = new Nodo(juegoInicial);

	}

	public Game getCurrentGame() {
		return currentState.getEstado();
	}

	public Movida getMejorMovida() {
		return currentState.getMovidaPorProfundidad(1);
	}

}
