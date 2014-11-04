package minimax;

import models.Game;
import models.Move;

public class MiniMaxByDepthGame extends MiniMaxGame {

	private int depth;

	public MiniMaxByDepthGame(Game initialGame, boolean prune,
			boolean saveTree, int depth) {
		super(initialGame, prune, saveTree);

		this.depth = depth;
	}

	@Override
	public Move getBestMove() {

		Move movida = currentState.getMoveByDepth(depth, isPrune(),
				0);

		return movida;

	}

}
