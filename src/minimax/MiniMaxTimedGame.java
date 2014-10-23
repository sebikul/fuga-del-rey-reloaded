package minimax;

import models.Game;
import models.Movida;

public class MiniMaxTimedGame extends MiniMaxGame {

	private int time;

	public MiniMaxTimedGame(Game juegoInicial, boolean prune, boolean saveTree,
			int time) {
		super(juegoInicial, prune, saveTree);

		this.time = time * 1000;

	}

	@Override
	protected Movida getMejorMovida() {

		long comienzo = System.currentTimeMillis();
		long actual = 0;
		Movida movida = null;
		int prof = 1;

		while (actual < time) {
			movida = currentState.getMovidaPorProfundidad(prof);
			actual = System.currentTimeMillis() - comienzo;
			prof++;
		}

		assert movida != null;

		return movida;

	}

}
