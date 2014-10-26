import java.io.FileNotFoundException;

import minimax.MiniMaxByDepthGame;
import minimax.MiniMaxGame;
import minimax.MiniMaxTimedGame;
import models.Game;
import models.GameFileParser;
import exceptions.InvalidFormatException;
import gui.GraphicalBoard;

public class Main {

	public static void main(String[] args) {

		if (args.length < 5) {
			throwArgumentErrorAndExit();
		}

		if (!args[0].equals("-file")) {
			throwArgumentErrorAndExit();
		}

		String path = System.getProperty("user.dir") + "/./" + args[1];

		boolean limitByDepth = false;

		if (args[2].equals("-maxtime")) {

			limitByDepth = false;

		} else if (args[2].equals("-depth")) {

			limitByDepth = true;

		} else {
			throwArgumentErrorAndExit();
		}

		int param = 0;

		try {
			param = Integer.parseInt(args[3]);
		} catch (NumberFormatException e) {
			throwArgumentErrorAndExit();
		}

		boolean isVisual = false;

		if (args[4].equals("-visual")) {
			isVisual = true;
		} else if (args[4].equals("-console")) {
			isVisual = false;
		} else {
			throwArgumentErrorAndExit();
		}

		boolean prune = false;
		boolean saveTree = false;

		if (args.length == 6) {

			if (args[5].equals("-prune")) {
				prune = true;
			} else if (args[5].equals("-tree")) {
				saveTree = true;
			} else {
				throwArgumentErrorAndExit();
			}

		} else if (args.length == 7) {

			if (args[5].equals("-prune")) {
				prune = true;

				if (args[6].equals("-tree")) {
					saveTree = true;
				} else {
					throwArgumentErrorAndExit();
				}

			} else {
				throwArgumentErrorAndExit();
			}

		}

		Game juegoArchivo = null;

		try {
			juegoArchivo = GameFileParser.fromFile(path);
		} catch (FileNotFoundException | InvalidFormatException e) {
			System.out.println("Archivo invalido.");
			System.exit(1);
		}

		if (isVisual) {

			GraphicalBoard gui;

			if (limitByDepth) {

				gui = GraphicalBoard.fromGameWithDepth(juegoArchivo, param,
						prune, saveTree);

			} else {

				gui = GraphicalBoard.fromGameWithMaxTime(juegoArchivo, param,
						prune, saveTree);

			}

		} else {

			MiniMaxGame game;

			if (limitByDepth) {

				game = new MiniMaxByDepthGame(juegoArchivo, prune, saveTree,
						param);
				
				//TODO

			} else {

				game = new MiniMaxTimedGame(juegoArchivo, prune, saveTree,
						param);

			}
			
			/*Aca falta colocar lo que iria si se ejecuta -console**/
			//TODO por turno
			//Movida movida = game.getMejorMovida();
//			if(movida ==null){
//				System.out.println("No se ha podido calcular jugada");
//			}else{
//				System.out.println(movida);
//			}
//				
				
			

		}

	}

	private static void throwArgumentErrorAndExit() {
		System.out.println("Argumentos invalidos!");

		printUsage();

		System.exit(1);
	}

	private static void printUsage() {

	}

}
