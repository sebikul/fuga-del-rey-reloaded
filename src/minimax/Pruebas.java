package minimax;

import java.util.Iterator;

import models.Movida;

//Hay nodos a los cuales no se les pisó el valor heurístico en la movida, pero fueron creados porque se lo hace al principio (caso nodo C del ejemplo), habria que 
//eliminarlos además de hacer un break en el for no? Sino cuando obtenga el min o el max voy a comparar contra cualquier cosa. Tendría que eliminar los proximos con iterator.
		
/**
 *Si los elimino no voy a poder hacer el arbol destacando la poda. Entonces debería marcar los nodos con un boolean "podado". Y si los marcamos, vamos a tener que hacer
 *nuestra propia collections.max que no le preste atención a los podados. 
 * 
 */



public class Pruebas {
	
	Iterator it = nodo.hijos.iterator();
	
	Nodo hijo;
	
	boolean podar = false;
	
	while(it.hasNext()) {

		hijo = it.next();
		if(podar){
			it.remove();
			continue;
		}
		
		Movida nuevaMovida = hijo.getMovidaPorProfundidad(hijo,
				profundidad - 1, valorPoda);

		nuevaMovida.setElegida(true);

		if (Math.abs(nuevaMovida.getValor()) == Integer.MAX_VALUE) {
			return hijo.movida;
		}
		
		//valorPoda==null si está deshabilitada la poda desde argumentos.
		if(valorPoda!=null && podar(valorPoda, nuevaMovida.getValor(),nodo.getEstado().getTurno())){
			
			//es necesario porque despues recorre el Collections.max con este nodo tambien. Y además no va marcado como podado.
			hijo.movida.setValor(nuevaMovida.getValor());
			podar=true;
			continue;
			
		}else{
			valorPoda = nuevaMovida.getValor();
		}

		
		hijo.movida.setValor(nuevaMovida.getValor());
		
	}


}
