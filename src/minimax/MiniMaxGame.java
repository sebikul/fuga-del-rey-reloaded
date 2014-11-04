package minimax;

import models.Game;
import models.Player;
import models.Move;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.BlockedMoveException;
import exceptions.InvalidMoveException;

public abstract class MiniMaxGame {

	protected Node currentState;
	private boolean prune;
	protected boolean saveTree = false;
	private GraphVizWriter gvw = null;

	public MiniMaxGame(Game juegoInicial, boolean prune, boolean saveTree) {

		if (saveTree) {
			try {
				gvw = new GraphVizWriter();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		currentState = new Node(juegoInicial, null, gvw);

		this.prune = prune;
		this.saveTree = saveTree;
	}

	public boolean isPrune() {
		return prune;
	}

	public Game getCurrentGame() {
		return currentState.getState();
	}

	public abstract Move getBestMove();

	public void forceCloseTreeFile() {

		if (gvw != null) {
			try {
				gvw.finalizeDotFile();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	public Player ejecutarMovida() {
		Move movida = getBestMove();

		// System.out.println("El valor magico de angie: " + movida.getValor());

		Player result = null;
		try {
			result = currentState.getState().move(movida);
			// System.out.println("LA MOVIDA A REALIZAR ES    --ORIGEN:"
			// + movida.getOrigen() + "--DESTINO:" + movida.getDestino());
		} catch (InvalidMoveException | BoardPointOutOfBoundsException
				| BlockedMoveException e) {
			e.printStackTrace();
		}

		currentState = new Node(currentState.getState(), null, gvw);

		forceCloseTreeFile();

		return result;

	}

}
