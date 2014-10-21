package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import models.Ficha;
import models.Game;
import models.Jugador;
import models.Punto;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class GraphicalBoard {

	private JFrame frame;
	private JTable table;
	private TableModel tableModel;
	private Game game;
	private JLabel lblJugador;
	private JPanel panel_1;
	private JPanel panel;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JTextField txtFOrigen;
	private JTextField txtFDestino;
	private JLabel lblColumnaOrigen;
	private JLabel lblColumnaDestino;
	private JTextField txtCOrigen;
	private JTextField txtCDestino;

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
		frame.setResizable(false);

		frame.setBounds(100, 100, 550, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new JTable();
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

		JLabel lblTurnoDe = new JLabel("Turno de: ");
		lblTurnoDe.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.add(lblTurnoDe);

		lblJugador = new JLabel("JUGADOR");
		panel.add(lblJugador);
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { lblTurnoDe }));

		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new GridLayout(2, 4, 0, 0));

		lblNewLabel = new JLabel("Fila origen");
		panel_1.add(lblNewLabel);

		txtFOrigen = new JTextField();
		panel_1.add(txtFOrigen);
		txtFOrigen.setColumns(10);

		lblColumnaOrigen = new JLabel("Columna origen");
		panel_1.add(lblColumnaOrigen);

		txtCOrigen = new JTextField();
		panel_1.add(txtCOrigen);
		txtCOrigen.setColumns(10);

		lblNewLabel_1 = new JLabel("Fila destino");
		panel_1.add(lblNewLabel_1);

		txtFDestino = new JTextField();
		txtFDestino.setColumns(10);
		panel_1.add(txtFDestino);

		lblColumnaDestino = new JLabel("Columna destino");
		panel_1.add(lblColumnaDestino);

		txtCDestino = new JTextField();
		panel_1.add(txtCDestino);
		txtCDestino.setColumns(10);

		table.addMouseListener(new MouseAdapter() {

			private boolean isOrigenSet = false;

			@Override
			public void mouseClicked(MouseEvent e) {

				int fila = table.rowAtPoint(e.getPoint());
				int columna = table.columnAtPoint(e.getPoint());

				if (isOrigenSet == false) {
					txtCOrigen.setText("" + columna);
					txtFOrigen.setText("" + fila);
					isOrigenSet = true;
				} else {
					txtCDestino.setText("" + columna);
					txtFDestino.setText("" + fila);
					isOrigenSet = false;

					try {
						int filaOrigen = Integer.parseInt(txtFOrigen.getText());
						int columnaOrigen = Integer.parseInt(txtCOrigen
								.getText());
						int filaDestino = Integer.parseInt(txtFDestino
								.getText());
						int columnaDestino = Integer.parseInt(txtCDestino
								.getText());

						Jugador result = game.mover(new Punto(filaOrigen,
								columnaOrigen), new Punto(filaDestino,
								columnaDestino));

						if (result != null) {
							JOptionPane.showMessageDialog(null, "El jugador "
									+ result + " ha ganado", "Hay un ganador!",
									JOptionPane.INFORMATION_MESSAGE);
						}

					} catch (NumberFormatException
							| BoardPointOutOfBoundsException e1) {
						JOptionPane.showMessageDialog(null,
								"Las coordenadas ingresadas son invalidas",
								"Error de formato", JOptionPane.ERROR_MESSAGE);
						return;

					} catch (MovimientoBloqueadoException e1) {

						JOptionPane.showMessageDialog(null,
								"El movimiento esta bloqueado.",
								"Error de movimiento",
								JOptionPane.ERROR_MESSAGE);
						return;

					} catch (MovimientoInvalidoException e1) {
						JOptionPane.showMessageDialog(null,
								"El movimiento es invalido.",
								"Error de movimiento",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					((AbstractTableModel) table.getModel())
							.fireTableDataChanged();

					actualizarTurno();
					limpiarCoordenadas();

				}

			}
		});

		table.setRowHeight(400 / table.getModel().getRowCount());

		frame.setVisible(true);
	}

	private void limpiarCoordenadas() {
		txtCDestino.setText("");
		txtCOrigen.setText("");
		txtFDestino.setText("");
		txtFOrigen.setText("");
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

			return this;

		}
	}
}