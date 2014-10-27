package minimax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Ficha;
import models.Game;
import models.Jugador;
import models.Movida;
import models.Punto;
import exceptions.BoardPointOutOfBoundsException;
import exceptions.MovimientoBloqueadoException;
import exceptions.MovimientoInvalidoException;

public class Nodo implements Comparable<Nodo> {

	private Game estado;

	private Movida movida;
	private List<Nodo> hijos = new ArrayList<Nodo>();

	public Nodo(Game estado, Movida movida) {
		this.movida = movida;
		this.estado = estado;
	}

	public Movida getMovidaPorProfundidad(int profundidad, boolean prune) {
		Integer valorPoda;
		int signo = -1;
		if(!prune){
			//Poda deshabilitada desde argumentos de ejecución.
			valorPoda=null;
		}
		else if(estado.getTurno()==Jugador.ENEMIGO){
			//Si soy MAX nunca va a cortar la primera rama porque Alfa<+Infinito, por más grande que sea.
			valorPoda=Integer.MAX_VALUE;
		}else{
			//Si soy MIN nunca va a cortar la primera rama porque Beta>-Infinito, por más chico que sea.
			valorPoda=signo*Integer.MAX_VALUE;
		}
			
		return getMovidaPorProfundidad(this, profundidad, valorPoda);
	}
	
	//Uso valor de poda 
	private Movida getMovidaPorProfundidad(Nodo nodo, int profundidad, Integer valorPoda) {
		
		boolean podar=false;

		calcularNodosHijo(nodo);

		if (profundidad > 1) {
			
			for (Nodo hijo : nodo.hijos) {

				Movida nuevaMovida = hijo.getMovidaPorProfundidad(hijo,
						profundidad - 1, valorPoda);

				nuevaMovida.setElegida(true);

				if (Math.abs(nuevaMovida.getValor()) == Integer.MAX_VALUE) {
					return hijo.movida;
				}
				
				//valorPoda==null si está deshabilitada la poda desde argumentos.
				if(valorPoda!=null && podar(valorPoda, nuevaMovida.getValor(),nodo.getEstado().getTurno())){
					
					//es necesario porque despues recorre el Collections.max con los nodos que quedaron.
					hijo.movida.setValor(nuevaMovida.getValor());
					break;
					
				}else{
					valorPoda = nuevaMovida.getValor();
				}

				
				hijo.movida.setValor(nuevaMovida.getValor());
				
			}

		}

		Movida movida;
		//Hay nodos a los cuales no se les pisó el valor heurístico en la movida, pero fueron creados porque se lo hace al principio (caso nodo C del ejemplo), habria que 
		//eliminarlos además de hacer un break en el for no? Sino cuando obtenga el min o el max voy a comparar contra cualquier cosa. Tendría que eliminar los proximos con iterator.
		//VER ACA CASO DE PODA DEL NODO F DEL EJEMPLO!! cuando estoy a profundidad 1, no estoy teniendo en cuenta la poda sino. 

		if (estado.getTurno() == Jugador.ENEMIGO) {
			//compareTo es por valorHeurístico
			movida = Collections.max(hijos).getMovida();
		} else {
			movida = Collections.min(hijos).getMovida();
		}

		movida.setElegida(true);

		return movida;

	}
	
	private boolean podar(Integer valorPoda, Integer actual, Jugador turno){
		
		if(turno==Jugador.ENEMIGO){
			int alfa = actual;
			if(alfa>=valorPoda){
				return true;
			}
			
			
		}else{
			
			int beta = actual;
			if(beta<=valorPoda){
				return true;
			}
		}
		
		return false;
	}

	private void calcularNodosHijo(Nodo nodo) {
		
		for (int fila = 0; fila < nodo.estado.getSize(); fila++) {
			for (int columna = 0; columna < nodo.estado.getSize(); columna++) {

				try {

					Ficha ficha = nodo.estado.getFichaAt(fila, columna);

					if ((nodo.estado.getTurno() == Jugador.ENEMIGO && ficha != Ficha.ENEMIGO)
							|| (nodo.estado.getTurno() == Jugador.GUARDIA && (ficha != Ficha.GUARDIA && ficha != Ficha.REY))) {
						continue;
					}
				} catch (BoardPointOutOfBoundsException e1) {
					e1.printStackTrace();
				}
				
				for (Movida movida : nodo.estado.getPosiblesMovidas(new Punto(
						fila, columna))) {
					Game game = nodo.estado.copiar();

					try {

						int signo = 1;

						if (game.getTurno() == Jugador.GUARDIA) {
							signo = -1;
						}

						Jugador result = game.mover(movida);

						// System.out.println(game.valorMagico());
						movida.setValor(((result == null) ? game.valorMagico()
								: Integer.MAX_VALUE) * signo);
					} catch (MovimientoInvalidoException
							| BoardPointOutOfBoundsException
							| MovimientoBloqueadoException e) {
						System.out.println("Nodo.getMovidaPorProfundidad()");
					}

					nodo.hijos.add(new Nodo(game, movida));
				}
		
			}
		
		}
		
		

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((movida == null) ? 0 : movida.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nodo other = (Nodo) obj;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (movida == null) {
			if (other.movida != null)
				return false;
		} else if (!movida.equals(other.movida))
			return false;
		return true;
	}

	public Game getEstado() {
		return estado;
	}

	@Override
	public int compareTo(Nodo o) {

		return movida.getValor() - o.getMovida().getValor();
	}

	public List<Nodo> getHijos() {
		return Collections.unmodifiableList(hijos);
	}

	public Movida getMovida() {
		return movida;
	}

	public void setMovida(Movida movida) {
		this.movida = movida;
	}
}
