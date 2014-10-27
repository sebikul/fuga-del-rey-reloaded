package minimax;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	private long i;

	public MiniMaxGame(Game juegoInicial, boolean prune, boolean saveTree) {

		currentState = new Nodo(juegoInicial, null);

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

		System.out.println("El valor magico de angie: " + movida.getValor());

		Jugador result = null;
		try {
			result = currentState.getEstado().mover(movida);
			System.out.println("LA MOVIDA A REALIZAR ES    --ORIGEN:"
					+ movida.getOrigen() + "--DESTINO:" + movida.getDestino());
		} catch (MovimientoInvalidoException | BoardPointOutOfBoundsException
				| MovimientoBloqueadoException e) {
			e.printStackTrace();
		}

		currentState = new Nodo(currentState.getEstado(), null);

		if (result != null) {
			return true;
		}

		return false;

	}

	public void printGraphVizCode() {

		try {

			StringBuffer sbf = new StringBuffer();

			sbf.append("digraph minimax {\n");

			printGraphVizCode(currentState, i, sbf);

			sbf.append("\n}");

			BufferedWriter bwr;
			File treeFile = null;

			treeFile = new File(System.getProperty("user.dir") + "/./"
					+ "tree.dot");

			bwr = new BufferedWriter(new FileWriter(treeFile));
			bwr.write(sbf.toString());

			// flush the stream
			bwr.flush();

			// close the stream
			bwr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void printGraphVizCode(Nodo nodo, long idPadre, StringBuffer bwr)
			throws IOException {

		String dotLine;

		if (i == 0) {

			dotLine = "" + i + " [label=\"START\"]\n";

		} else {

			dotLine = "" + i + " [label=\"" + nodo.getMovida() + "\"";

			if (nodo.getMovida() != null && nodo.getMovida().isElegida()) {
				dotLine += ",color=lightblue";
			}

		}

		long idActual = i;
		i++;

		if (idActual != 0) {

			if (nodo.getEstado().getTurno() == Jugador.GUARDIA) {
				dotLine += " shape=box";
			}

			dotLine += "]\n" + idPadre + " -> " + idActual + "\n";
		}

		bwr.append(dotLine);

		for (Nodo hijo : nodo.getHijos()) {
			printGraphVizCode(hijo, idActual, bwr);
		}

	}

}
