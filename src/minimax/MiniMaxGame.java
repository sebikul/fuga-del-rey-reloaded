package minimax;

import models.Game;
import models.Jugador;
import models.Movida;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public abstract class MiniMaxGame {

	protected Nodo currentState;
	private boolean prune;
	protected boolean saveTree = false;
	private GraphVizWriter gvw = null;

	public MiniMaxGame(Game juegoInicial, boolean prune, boolean saveTree) {

		if (saveTree) {
			try {
				gvw = new GraphVizWriter();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		currentState = new Nodo(juegoInicial, null, gvw);

		this.prune = prune;
		this.saveTree = saveTree;
	}

	public boolean isPrune() {
		return prune;
	}

	public Game getCurrentGame() {
		return currentState.getEstado();
	}

	protected abstract Movida getMejorMovida();

	public boolean ejecutarMovidaDeEnemigo() {
		Movida movida = getMejorMovida();

		// System.out.println("El valor magico de angie: " + movida.getValor());

		Jugador result = null;
		try {
			result = currentState.getEstado().mover(movida);
			// System.out.println("LA MOVIDA A REALIZAR ES    --ORIGEN:"
			// + movida.getOrigen() + "--DESTINO:" + movida.getDestino());
		} catch (MovimientoInvalidoException | BoardPointOutOfBoundsException
				| MovimientoBloqueadoException e) {
			e.printStackTrace();
		}

		currentState = new Nodo(currentState.getEstado(), null, gvw);

		try {
			gvw.finalizeDotFile();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result != null) {
			return true;
		}

		return false;

	}

}
