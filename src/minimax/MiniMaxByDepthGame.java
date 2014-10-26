package minimax;

import models.Game;
import models.Movida;

public class MiniMaxByDepthGame extends MiniMaxGame {

	private int depth;

	public MiniMaxByDepthGame(Game juegoInicial, boolean prune,
			boolean saveTree, int depth) {
		super(juegoInicial, prune, saveTree);

		this.depth = depth;
	}

	@Override
	protected Movida getMejorMovida() {
		Movida movida = currentState.getMovidaPorProfundidad(depth);

		if (this.saveTree) {
			this.printGraphVizCode();
		}

		return movida;

	}

}
