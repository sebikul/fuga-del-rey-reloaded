package minimax;

import java.util.LinkedList;
import java.util.Queue;

import models.Game;
import models.Jugador;
import models.Movida;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public abstract class MiniMaxGame {

	protected Nodo currentState;
	private boolean prune = false;
	private boolean saveTree = false;

	public MiniMaxGame(Game juegoInicial, boolean prune, boolean saveTree) {

		currentState = new Nodo(juegoInicial, null);

		this.prune = prune;
		this.saveTree = saveTree;
	}

	public Game getCurrentGame() {
		return currentState.getEstado();
	}

	protected abstract Movida getMejorMovida();

	public boolean ejecutarMovidaDeEnemigo() {
		Movida movida = getMejorMovida();

		Jugador result = null;
		try {
			result = currentState.getEstado().mover(movida);
		} catch (MovimientoInvalidoException | BoardPointOutOfBoundsException
				| MovimientoBloqueadoException e) {
			e.printStackTrace();
		}

		currentState = new Nodo(currentState.getEstado(), null);

		if (result != null) {
			return true;
		}

		return false;

	}

	public void printGraphVizCode() {

		System.out.println("digraph {");

		Queue<Nodo> queue = new LinkedList<Nodo>();
		queue.add(currentState);

		while (!queue.isEmpty()) {

			Nodo nodo = queue.poll();

			for (Nodo hijo : nodo.getHijos()) {

				System.out.println(hijo);
				queue.add(hijo);

			}

		}

		System.out.println("digraph }");

	}

}
