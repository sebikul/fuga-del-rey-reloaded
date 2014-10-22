package minimax;

import models.Game;

public class MiniMaxGame {

	private Nodo currentState;

	// private Map<Nodo> estados = new HashMap<Nodo>();

	public MiniMaxGame(Game juegoInicial) {

		currentState = new Nodo(juegoInicial);

	}

	public Nodo getCurrentState() {
		return currentState;
	}

	public Game getCurrentGame() {
		return currentState.getEstado();
	}

}
