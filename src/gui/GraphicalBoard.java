package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import minimax.MiniMaxByDepthGame;
import minimax.MiniMaxGame;
import minimax.MiniMaxTimedGame;
import models.Piece;
import models.Game;
import models.Player;
import models.Point;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.BlockedMoveException;
import exceptions.InvalidMoveException;

public class GraphicalBoard {

	private JFrame frame;
	private JTable table;
	private TableModel tableModel = new GameTableModel();
	private MiniMaxGame game;
	private JLabel lblJugador;
	private JPanel panel;
	private JLabel lblError;
	private JPanel panel_2;
	private JPanel panel_3;
	private MouseListener mouseListener;
	private Point origen, destino;
	private JLabel lblGameOver;

	/**
	 * Create the application.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private GraphicalBoard() {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	static public GraphicalBoard fromGameWithMaxTime(Game tmpgame, int time,
			boolean prune, boolean saveTree) {

		GraphicalBoard gui = new GraphicalBoard();

		gui.game = new MiniMaxTimedGame(tmpgame, prune, saveTree, time);

		gui.initialize();

		return gui;

	}

	static public GraphicalBoard fromGameWithDepth(Game tmpgame, int depth,
			boolean prune, boolean saveTree) {

		GraphicalBoard gui = new GraphicalBoard();

		gui.game = new MiniMaxByDepthGame(tmpgame, prune, saveTree, depth);

		gui.initialize();

		return gui;

	}

	private void actualizarTurno() {
		lblJugador.setText(game.getCurrentGame().getTurn().name());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		table = new JTable();
		mouseListener = new MouseListenerImpl();

		frame.setResizable(false);
		frame.setBounds(100, 100, 550, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.setBorder(null);
		table.setModel(tableModel);
		table.setDefaultRenderer(Piece.class, new ColorRenderer());

		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.getContentPane().add(table);

		panel = new JPanel();

		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		panel_2 = new JPanel();
		panel.add(panel_2);

		JLabel lblTurnoDe = new JLabel("Turno de: ");
		panel_2.add(lblTurnoDe);
		lblTurnoDe.setHorizontalAlignment(SwingConstants.TRAILING);

		lblJugador = new JLabel("JUGADOR");
		panel_2.add(lblJugador);

		panel_3 = new JPanel();
		panel.add(panel_3);

		lblError = new JLabel("");
		panel_3.add(lblError);

		lblGameOver = new JLabel("");
		lblGameOver.setForeground(Color.RED);
		panel_3.add(lblGameOver);

		table.addMouseListener(mouseListener);
		table.setRowHeight(450 / table.getModel().getRowCount());

		frame.setVisible(true);

		// FIXME
		if (game.getCurrentGame().getTurn() == Player.ENEMY) {
			ejecutarMovidaDeMaquina();
		}

		actualizarPantalla();

	}

	private class MouseListenerImpl implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {

			// if (game.getCurrentGame().getTurno() == Jugador.ENEMIGO) {
			// return;
			// }

			int fila = table.rowAtPoint(e.getPoint());
			int columna = table.columnAtPoint(e.getPoint());

			if (origen == null) {

				origen = new Point(fila, columna);

			} else {

				destino = new Point(fila, columna);

				if (origen.equals(destino)) {
					table.clearSelection();
				} else {
					ejecutarMovidaDeJugador(origen, destino);
				}

				destino = origen = null;

			}

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

	}

	private void limpiarCoordenadas() {

		lblError.setText("");
	}

	private void ejecutarMovidaDeJugador(Point origen, Point destino) {

		try {

			Player result;

			result = game.getCurrentGame().move(origen, destino);
			if (result != null) {
				gameOver(result);
			}

		} catch (BoardPointOutOfBoundsException e1) {
			lblError.setText("Las coordenadas ingresadas son invalidas");
			return;
		} catch (BlockedMoveException e1) {
			lblError.setText("El movimiento esta bloqueado.");
			return;
		} catch (InvalidMoveException e1) {
			lblError.setText("El movimiento es invalido.");
			return;
		} finally {
			origen = destino = null;
		}

		actualizarPantalla();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ejecutarMovidaDeMaquina();

				actualizarPantalla();

			}

		});

	}

	private void ejecutarMovidaDeMaquina() {

		Player result = game.ejecutarMovida();

		if (result != null) {

			gameOver(result);
		}

	}

	private void gameOver(Player jugador) {

		System.out.println("El jugador " + jugador + " ha ganado");
		if (jugador == Player.GUARD) {
			lblGameOver.setForeground(Color.BLUE);
		}
		lblGameOver.setText("EL JUGADOR " + jugador + " HA GANADO");

		System.out.println(game.getCurrentGame());

		table.setEnabled(false);
		// System.exit(0);

	}

	private void actualizarPantalla() {

		((AbstractTableModel) table.getModel()).fireTableDataChanged();

		actualizarTurno();
		limpiarCoordenadas();

	}

	@SuppressWarnings("serial")
	private class GameTableModel extends AbstractTableModel {

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Piece.class;
		}

		@Override
		public int getRowCount() {

			return game.getCurrentGame().getSize();
		}

		@Override
		public int getColumnCount() {
			return game.getCurrentGame().getSize();
		}

		@Override
		public Object getValueAt(int fila, int columna) {
			try {
				return game.getCurrentGame().getTokenAt(fila, columna);
			} catch (BoardPointOutOfBoundsException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	@SuppressWarnings("serial")
	private class ColorRenderer extends JLabel implements TableCellRenderer {

		public ColorRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object fichaObject, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Color newColor = Color.WHITE;

			if (fichaObject == null) {
				return this;
			}

			char fichaChar = (char) fichaObject;

			Piece ficha = Piece.fromChar(fichaChar);

			setText("" + fichaChar);

			setHorizontalAlignment(SwingConstants.CENTER);

			switch (ficha) {

			case KING:
				newColor = Color.YELLOW;
				break;

			case CASTLE:
				newColor = Color.GRAY;
				break;

			case ENEMY:
				newColor = Color.RED;
				break;

			case GUARD:
				newColor = Color.BLUE;
				break;

			case THRONE:
				newColor = Color.CYAN;
				break;

			case EMPTY:
				break;
			default:
				break;
			}

			setBackground(newColor);

			if (row == table.getSelectedRow()
					&& column == table.getSelectedColumn()) {
				setBackground(Color.GREEN);
			}

			return this;

		}
	}
}