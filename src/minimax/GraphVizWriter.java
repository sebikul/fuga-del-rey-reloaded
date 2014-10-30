package minimax;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GraphVizWriter {

	private PrintWriter writer;

	public GraphVizWriter() throws IllegalAccessException {

		writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					System.getProperty("user.dir") + "/./" + "tree.dot")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (writer == null) {
			throw new IllegalAccessException();
		}

		writer.println("digraph minimax {");

	}

	public void finalizeDotFile() throws IllegalAccessException {

		if (writer == null) {
			throw new IllegalAccessException();
		}

		writer.println("}");

		writer.flush();
		writer.close();
	}

	public void addNode(Nodo parentNode, Nodo nodo, boolean boxShape) {

		String line = "";

		if (parentNode == null) {
			line += nodo.hashCode() + " [label=\"START\"";
		} else {

			line += "" + nodo.hashCode() + "[label=\"" + nodo.getMovida()
					+ (!nodo.getMovida().isPodada() ? nodo.getValor() : "")
					+ "\"";

			if (nodo.getMovida().isElegida()) {
				line += ", color=lightblue";
			} else if (nodo.getMovida().isPodada()) {
				line += ", color=red";
			}

			if (boxShape) {
				line += ", shape=box";
			}

		}

		line += "]";

		writer.println(line);

		if (parentNode != null) {
			writer.println(parentNode.hashCode() + " -> " + nodo.hashCode());
		}
		writer.println();

	}
}
