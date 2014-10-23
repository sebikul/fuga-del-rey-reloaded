package minimax;

import models.Game;

public class MiniMaxTimedGame extends MiniMaxGame {

	private int time;

	public MiniMaxTimedGame(Game juegoInicial, boolean prune, boolean saveTree, int time) {
		super(juegoInicial, prune, saveTree);

		this.time = time;

	}

}
