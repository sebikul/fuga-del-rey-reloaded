package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.InvalidFormatException;

public class GameFileParser {

	public static Game fromFile(String path) throws FileNotFoundException,
			InvalidFormatException {

		FileReader file = new FileReader(path);

		BufferedReader fileReader = new BufferedReader(file);

		Turno turno;

		try {
			String firstLine = fileReader.readLine();
			turno = Turno.fromInt(Integer.valueOf(firstLine));
		} catch (IOException e) {
			return null;
		}

		int size = -1;

		Ficha[][] tablero = new Ficha[size][];
		int i = 0;

		try {
			while (fileReader.ready()) {

				String line = fileReader.readLine();

				if (size == -1) {
					size = line.length();
				} else if (size != line.length()) {
					throw new InvalidFormatException(line);
				}

				char[] lineArray = line.toCharArray();

				tablero[i] = new Ficha[size];
				for (int j = 0; j < size; j++) {
					tablero[i][j] = Ficha.fromChar(lineArray[j]);
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

		return new Game(size, tablero, turno);

	}
}
