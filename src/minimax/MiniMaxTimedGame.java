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
		long diff = 0;
		Movida movida = null;
		int prof = 1;

		while (diff < time) {
			movida = currentState.getMovidaPorProfundidad(prof);
			diff = System.currentTimeMillis() - comienzo;
			System.out.println("Tiempo tardado en profundidad " + prof + " = "
					+ diff + "ms");
			prof++;
		}

		assert movida != null;

		if (this.saveTree) {
			this.printGraphVizCode();
		}

		return movida;

	}

}
