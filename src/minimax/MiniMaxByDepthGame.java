package minimax;

import models.Game;

public class MiniMaxByDepthGame extends MiniMaxGame {

	private int depth;

	public MiniMaxByDepthGame(Game juegoInicial, boolean prune,
			boolean saveTree, int depth) {
		super(juegoInicial, prune, saveTree);

		this.depth = depth;
	}

}
