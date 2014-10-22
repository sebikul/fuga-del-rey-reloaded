import java.io.FileNotFoundException;

import models.Game;
import models.GameFileParser;
import exceptions.InvalidFormatException;
import gui.GraphicalBoard;

public class Main {

	public static void main(String[] args) {
		try {

			Game juegoArchivo = GameFileParser.fromFile(System
					.getProperty("user.dir") + "/./" + args[1]);

			GraphicalBoard gui = new GraphicalBoard(juegoArchivo);

			// System.out.println(juegoArchivo);

			// if(args[2].equals("-maxtime")){
			//
			// System.out.println("Hago la funcion por tiempo de la clase MiniMax con el parametro args[3]");
			// }else if(args[2].equals("-depth")){
			// System.out.println("Llamo a la funcion por profundidad de la clase MiniMax con el parametro args[3]");
			// }else{
			// System.out.println("Parametros incorrectos");
			// System.exit(1);
			// }
			//
			//

		} catch (FileNotFoundException | InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
