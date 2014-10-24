package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.InvalidFormatException;

public class GameFileParser {

	public static Game fromFile(String path) throws FileNotFoundException,
			InvalidFormatException {

		FileReader file = new FileReader(path);

		BufferedReader fileReader = new BufferedReader(file);

		Jugador turno;

		try {
			String firstLine = fileReader.readLine();
			turno = Jugador.fromInt(Integer.valueOf(firstLine));
		} catch (IOException e) {
			try {
				fileReader.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

		int size = -1;

		int i = 0;

		int guardias = 0;
		int enemigos = 0;

		Ficha[][] tablero = null;

		try {
			while (fileReader.ready()) {

				String line = fileReader.readLine();

				if (size == -1) {
					size = line.length();
					tablero = new Ficha[size][];
				} else if (size != line.length()) {
					throw new InvalidFormatException(line);
				}

				char[] lineArray = line.toCharArray();

				tablero[i] = new Ficha[size];
				for (int j = 0; j < size; j++) {
					tablero[i][j] = Ficha.fromChar(lineArray[j]);

					Ficha ficha = tablero[i][j];

					if (ficha.getJugador() == Jugador.GUARDIA) {
						guardias++;
					} else if (ficha.getJugador() == Jugador.ENEMIGO) {
						enemigos++;
					}

				}

				i++;

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		tablero[0][0] = tablero[0][size - 1] = tablero[size - 1][0] = tablero[size - 1][size - 1] = Ficha.CASTILLO;

		Game game = new Game(size, tablero, turno);

		game.setCantidadDeFichas(enemigos, guardias);

		return game;

	}
}
