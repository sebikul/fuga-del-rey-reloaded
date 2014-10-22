package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import minimax.MiniMaxGame;
import models.Ficha;
import models.Game;
import models.Jugador;
import models.Movida;
import models.Punto;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class GraphicalBoard {

	private JFrame frame;
	private JTable table;
	private TableModel tableModel;
	private MiniMaxGame game;
	private JLabel lblJugador;
	private JPanel panel;
	private JLabel lblError;
	private JPanel panel_2;
	private JPanel panel_3;

	private Punto origen, destino;

	/**
	 * Create the application.
	 */
	public GraphicalBoard(Game tmpgame) {

		game = new MiniMaxGame(tmpgame);
		tableModel = new GameTableModel();

		initialize();
		actualizarTurno();

	}

	private void actualizarTurno() {
		lblJugador.setText(game.getCurrentGame().getTurno().name());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);

		frame.setBounds(100, 100, 550, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new JTable();
		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.setBorder(null);
		table.setModel(tableModel);
		table.setDefaultRenderer(Ficha.class, new ColorRenderer());

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

		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { lblTurnoDe }));

		panel_3 = new JPanel();
		panel.add(panel_3);

		lblError = new JLabel("");
		panel_3.add(lblError);

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				int fila = table.rowAtPoint(e.getPoint());
				int columna = table.columnAtPoint(e.getPoint());

				if (origen == null) {

					origen = new Punto(fila, columna);

				} else {

					destino = new Punto(fila, columna);

					if (origen.equals(destino)) {
						table.clearSelection();
					} else {
						ejecutarMovidaDeJugador(origen, destino);
					}

					destino = origen = null;

				}

			}
		});

		table.setRowHeight(450 / table.getModel().getRowCount());

		frame.setVisible(true);

		ejecutarMovidaDeMaquina();

		actualizarPantalla();
	}

	private void limpiarCoordenadas() {

		lblError.setText("");
	}

	private void ejecutarMovidaDeJugador(Punto origen, Punto destino) {

		try {

			Jugador result;

			// TODO
			result = game.getCurrentGame().mover(origen, destino);
			if (result != null) {
				lblError.setText("El jugador " + result + " ha ganado");
				return;
			}

		} catch (BoardPointOutOfBoundsException e1) {
			lblError.setText("Las coordenadas ingresadas son invalidas");
			return;
		} catch (MovimientoBloqueadoException e1) {
			lblError.setText("El movimiento esta bloqueado.");
			return;
		} catch (MovimientoInvalidoException e1) {
			lblError.setText("El movimiento es invalido.");
			return;
		} finally {
			origen = destino = null;
		}

		actualizarPantalla();

		ejecutarMovidaDeMaquina();

		actualizarPantalla();

	}

	private void ejecutarMovidaDeMaquina() {

		if (game.ejecutarMovidaDeEnemigo()) {
			lblError.setText("El enemigo ha ganado");
			return;
		}

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
			return Ficha.class;
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

			assert fichaObject != null;

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

			if (row == table.getSelectedRow()
					&& column == table.getSelectedColumn()) {
				setBackground(Color.GREEN);
			}

			return this;

		}
	}
}