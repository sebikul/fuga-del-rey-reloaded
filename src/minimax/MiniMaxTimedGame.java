package minimax;

import models.Game;
import models.Move;

public class MiniMaxTimedGame extends MiniMaxGame {

	private int timeParamInMillis;

	public MiniMaxTimedGame(Game initialGame, boolean prune, boolean saveTree,
			int time) {
		super(initialGame, prune, saveTree);

		this.timeParamInMillis = time * 1000;

	}

	@Override
	public Move getBestMove() {

		long start = System.currentTimeMillis();
		Move move = null;
		Move lastMove = null;
		
		long maxTime=timeParamInMillis+ System.currentTimeMillis();
		
		int prof = 1;

		while (System.currentTimeMillis() < maxTime) {
			move = currentState.getMoveByDepth(prof, isPrune(),
					maxTime);

			if (move != null) {
				lastMove = move;

				// diff = System.currentTimeMillis() - comienzo;
				System.out.println("Tiempo tardado en profundidad " + prof
						+ " = " + (System.currentTimeMillis() - start)
						+ "ms");
				prof++;

			} else {
				System.out.println("Cortamos en profundidad " + prof);
			}

		}

		assert lastMove != null;

		return lastMove;

	}
}
