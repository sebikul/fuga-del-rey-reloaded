import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;
import models.Game;
import models.Punto;

public class Main {

	public static void main(String[] args) {
		Game board = new Game(7);

		System.out.println(board);

		try {
			board.mover(new Punto(2, 0), new Punto(1, 0));
			System.out.println(board);
			board.mover(new Punto(2, 2), new Punto(2, 1));
			System.out.println(board);
			board.mover(new Punto(1, 0), new Punto(1, 1));
			System.out.println(board);
		} catch (MovimientoBloqueadoException | BoardPointOutOfBoundsException
				| MovimientoInvalidoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
