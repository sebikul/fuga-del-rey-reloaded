package minimax;

import models.Game;
import models.Movida;

public class MiniMaxTimedGame extends MiniMaxGame {

	private int timeParamInMillis;

	public MiniMaxTimedGame(Game juegoInicial, boolean prune, boolean saveTree,
			int time) {
		super(juegoInicial, prune, saveTree);

		this.timeParamInMillis = time * 1000;

	}

	@Override
	public Movida getMejorMovida() {

		long comienzo = System.currentTimeMillis();
		Movida movida = null;
		Movida ultimaMovida = null;
		
		long maxTime=timeParamInMillis+ System.currentTimeMillis();
		
		int prof = 1;

		while (System.currentTimeMillis() < maxTime) {
			movida = currentState.getMovidaPorProfundidad(prof, isPrune(),
					maxTime);

			if (movida != null) {
				ultimaMovida = movida;

				// diff = System.currentTimeMillis() - comienzo;
				System.out.println("Tiempo tardado en profundidad " + prof
						+ " = " + (System.currentTimeMillis() - comienzo)
						+ "ms");
				prof++;

			} else {
				System.out.println("Cortamos en profundidad " + prof);
			}

		}

		assert ultimaMovida != null;

		return ultimaMovida;

	}
}
