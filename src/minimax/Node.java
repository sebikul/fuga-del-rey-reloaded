package minimax;

import java.util.ArrayList;
import java.util.List;

import models.Game;

public class Node {

	private Game estado;
	private int valor; 
	private List<Game> hijos = new ArrayList<Game>();
	
	public Node(Game estado, int valor){
		this.estado = estado;
		this.valor = valor;
	}
	
	//public List<Game> 
	
	
}
