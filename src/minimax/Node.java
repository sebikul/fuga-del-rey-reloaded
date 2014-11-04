package minimax;

import models.Game;
import models.Player;
import models.Move;
import models.Point;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.BlockedMoveException;
import exceptions.InvalidMoveException;

public class Node {

	private Game state;

	private Move move;
	private int value;

	public int getValor() {
		return value;
	}

	private final GraphVizWriter gvw;

	public Node(Game state, Move move, GraphVizWriter gvw) {
		this.move = move;
		this.state = state;
		this.gvw = gvw;
	}

	public Move getMoveByDepth(int depth, boolean prune,
			long maxTime) {
		Integer alfa, beta;

		int sign = -1;
		if (!prune) {

			alfa = null;
			beta = null;
		} else {
			alfa = sign * Integer.MAX_VALUE;
			beta = Integer.MAX_VALUE;
		}

		if (gvw != null) {
			gvw.addNode(null, this, this.state.getTurn() == Player.GUARD);
		}

		return getBestMoveByDepth(this, depth, alfa, beta, maxTime).move;

	}

	private Node getBestMoveByDepth(Node nodo, int depth,
			Integer alfa, Integer beta, long maxTime) {

		boolean prune = false;

		Node bestChild = null;

		for (Point point : nodo.state.currentTurnPieces()) {

			for (Move move : nodo.state.getPossibleMoves(point)) {

				// Si en la evaluaci�n de la anterior movida se determin�
				// una
				// poda, se setan las siguientes movidas como podadas para luego
				// incluirlas al �rbol.

				if (prune) {
					move.setPruned(true);

					//Hijo dummy para poder agregarlo al arbol.
					Node child = new Node(null, move, gvw);

					if (gvw != null) {
						gvw.addNode(nodo, child,
								nodo.state.getTurn() == Player.GUARD);
					}

					continue;
				}

				Game game = nodo.state.copy();

				try {

					int sign = 1;

					if (game.getTurn() == Player.GUARD) {
						sign = -1;
					}

					Player result = game.move(move);

					Node child = new Node(game, move, gvw);

					if (result == null) {
						child.value = game.magicValue();
					} else if (result == nodo.state.getTurn()) {
						child.value = Integer.MAX_VALUE * sign;
						child.move.setChoosen(true);

						if (gvw != null) {
							gvw.addNode(nodo, child,
									nodo.state.getTurn() == Player.GUARD);
						}
						return child;
					}

					if (depth > 1) {

						child.value = child.getBestMoveByDepth(child,
								depth - 1, alfa, beta, maxTime).value;

						if (alfa != null && beta != null) {
							if (state.getTurn() == Player.ENEMY) {
								alfa = child.value;
								if (alfa >= beta)
									prune = true;

							} else {
								beta = child.value;
								if (beta <= alfa)
									prune = true;
							}
						}

					}

					if (bestChild == null) {
						bestChild = child;
					} else if ((game.getTurn() == Player.GUARD && bestChild.value < child.value)
							|| (game.getTurn() == Player.ENEMY && bestChild.value > child.value)) {
						bestChild = child;
					} else {

						if (gvw != null) {
							// La movida es descartada, la agrego al arbol
							gvw.addNode(nodo, child,
									nodo.state.getTurn() == Player.GUARD);

						}
					}

					if (alfa != null && beta != null && depth == 1) {
						if (state.getTurn() == Player.ENEMY) {
							alfa = child.value;
							if (alfa >= beta)
								prune = true;

						} else {
							beta = child.value;
							if (beta <= alfa)
								prune = true;
						}
					}

				} catch (InvalidMoveException
						| BoardPointOutOfBoundsException
						| BlockedMoveException e) {
					System.out.println("Nodo.getMovidaPorProfundidad()");
				}

			}

			if (maxTime != 0 && System.currentTimeMillis() > maxTime) {
				// System.out.println("Cortando por limite de tiempo");
				// System.out.println("Nos pasamos "
				// + (System.currentTimeMillis() - maxTime));
				return new Node(null, null, null);
			}

		}

		bestChild.move.setChoosen(true);

		if (gvw != null) {
			// Es la meor movida, todavia no la agregamos
			gvw.addNode(nodo, bestChild,
					nodo.state.getTurn() == Player.GUARD);
		}

		return bestChild;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((move == null) ? 0 : move.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (move == null) {
			if (other.move != null)
				return false;
		} else if (!move.equals(other.move))
			return false;
		return true;
	}

	public Game getState() {
		return state;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}
}
