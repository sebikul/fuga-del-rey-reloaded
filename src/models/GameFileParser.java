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

		Player turn;

		try {
			String firstLine = fileReader.readLine();
			turn = Player.fromInt(Integer.valueOf(firstLine));
		} catch (IOException e) {
			try {
				fileReader.close();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			return null;
		}

		int size = -1;

		int i = 0;

		int guards = 0;
		int enemies = 0;

		Piece[][] board = null;

		try {
			while (fileReader.ready()) {

				String line = fileReader.readLine();

				if (size == -1) {
					size = line.length();
					board = new Piece[size][];
				} else if (size != line.length()) {
					throw new InvalidFormatException(line);
				}

				char[] lineArray = line.toCharArray();

				board[i] = new Piece[size];
				for (int j = 0; j < size; j++) {
					board[i][j] = Piece.fromChar(lineArray[j]);

					Piece ficha = board[i][j];

					if (ficha.getPlayer() == Player.GUARD) {
						guards++;
					} else if (ficha.getPlayer() == Player.ENEMY) {
						enemies++;
					}

				}

				i++;

			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		board[0][0] = board[0][size - 1] = board[size - 1][0] = board[size - 1][size - 1] = Piece.CASTLE;

		Game game = new Game(size, board, turn);

		game.setPieceCount(enemies, guards);

		return game;

	}
}
