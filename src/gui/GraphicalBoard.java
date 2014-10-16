package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import models.Ficha;
import models.Game;
import exceptions.BoardPointOutOfBoundsException;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.eclipse.wb.swing.FocusTraversalOnArray;

public class GraphicalBoard {

	private JFrame frame;
	private JTable table;
	private TableModel tableModel;
	private Game game;
	private JLabel lblJugador;

	/**
	 * Create the application.
	 */
	public GraphicalBoard(int size) {

		game = new Game(size);
		tableModel = new GameTableModel();

		initialize();
		actualizarTurno();

	}

	private void actualizarTurno() {
		lblJugador.setText(game.getTurno().name());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.setBorder(null);
		table.setModel(tableModel);
		table.setDefaultRenderer(Ficha.class, new ColorRenderer());

		frame.getContentPane().add(table, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JLabel lblTurnoDe = new JLabel("Turno de: ");
		lblTurnoDe.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.add(lblTurnoDe);

		lblJugador = new JLabel("JUGADOR");
		panel.add(lblJugador);
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { lblTurnoDe }));
		frame.setVisible(true);
	}

	@SuppressWarnings("serial")
	private class GameTableModel extends AbstractTableModel {

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Ficha.class;
		}

		@Override
		public int getRowCount() {

			return game.getSize();
		}

		@Override
		public int getColumnCount() {
			return game.getSize();
		}

		@Override
		public Object getValueAt(int fila, int columna) {
			try {
				return game.getTokenAt(fila, columna);
			} catch (BoardPointOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	@SuppressWarnings("serial")
	public class ColorRenderer extends JLabel implements TableCellRenderer {

		public ColorRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object fichaObject, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Color newColor = Color.WHITE;

			char fichaChar = (char) fichaObject;

			Ficha ficha = Ficha.fromChar(fichaChar);

			setText("" + fichaChar);

			setHorizontalAlignment(SwingConstants.CENTER);

			switch (ficha) {

			case REY:
				newColor = Color.YELLOW;
				break;

			case CASTILLO:
				newColor = Color.GRAY;
				break;

			case ENEMIGO:
				newColor = Color.RED;
				break;

			case GUARDIA:
				newColor = Color.BLUE;
				break;

			case TRONO:
				newColor = Color.CYAN;
				break;

			case VACIO:
				break;
			default:
				break;
			}

			setBackground(newColor);

			return this;

		}
	}
}