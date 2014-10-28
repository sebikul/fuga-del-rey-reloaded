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
	public Movida getMejorMovida() {

		Movida movida = currentState.getMovidaPorProfundidad(depth, isPrune(),
				0);

		return movida;

	}

}
