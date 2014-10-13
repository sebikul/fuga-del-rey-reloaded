

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "tablero.h"

#define tablero_max(var) ((var)-1)

/* Define el condicional para ciclos for de movimientos positivos y negativos*/
#define COND_MOV_BILATERAL(campo, axis) origen.campo<destino.campo?axis<=destino.campo:axis>=destino.campo

/* Define el incremento para ciclos for de movimientos positivos y negativos*/
#define COND_INCR_BILATERAL(campo, axis) origen.campo<destino.campo?axis++:axis--

void load(char * path, t_juego *juego)
{
	int fil, col,var;

	FILE * archivo;

	archivo = fopen(path,"r");

	fgets(juego->jugadores[0], 30, archivo);

	var=strlen(juego->jugadores[0]);
	juego->jugadores[0][var-1]=0;

	fgets(juego->jugadores[1], 30, archivo);
	var=strlen(juego->jugadores[1]);
	juego->jugadores[1][var-1]=0;


	fread(&var,sizeof(var), 1, archivo);

	juego->jugador= (var==1) ? TABLERO_JUGADOR_ENEMIGO : TABLERO_JUGADOR_GUARDIA;

	fread(&var,sizeof(var), 1, archivo);
	juego->size=var;
	juego->tablero=tablero_crear(juego->size);

	for(fil=0;fil<juego->size;fil++){
		for(col=0;col<juego->size;col++){	
			var=fgetc(archivo);

			if(var=='0')
				var=0;

			juego->tablero[fil][col]=var;
		}	

	}

	fclose(archivo);

}

int path_exist(char *path){

	FILE * file;
	int retval=0;

	file = fopen(path,"r");

	retval= (file!=NULL);
	fclose(file);

	return retval;

}

void guardar(char * path, t_juego *juego)
{
	int fil,col,var;

	FILE * archivo;

	archivo = fopen(path,"w");

	fputs(juego->jugadores[0], archivo);
	fputc('\n', archivo);

	fputs(juego->jugadores[1], archivo);
	fputc('\n', archivo);


	var=juego->jugador==TABLERO_JUGADOR_ENEMIGO?1:2;
	fwrite(&var, sizeof(var), 1, archivo);

	var=juego->size;
	fwrite(&var, sizeof(var), 1, archivo);
	
	for(fil=0; fil<juego->size; fil++){

		for(col=0; col<juego->size; col++){

			var= (juego->tablero[fil][col]==0)?'0':juego->tablero[fil][col];

			fputc(var, archivo);

		}
	}

		

			
	
	fclose(archivo);

}

char ** tablero_crear(int size){

	int i;
	char ** tmptablero;

	if(size%2==0 || !(7 <= size && size <= 19) )
		/* todo: exit */
		return NULL;

	tmptablero=calloc(size, sizeof(*tmptablero));

	if(tmptablero==NULL)
		return NULL;


	for(i=0;i<size;i++){

		tmptablero[i]=calloc(size,sizeof(**tmptablero));

		if(tmptablero[i]==NULL)
			/* TODO: limpiar,*/
			return NULL;

	}

	return tmptablero;

}


static int tropas_lenght(int size){

	return (size<TABLERO_LARGO)?(3-1)/2:(5-1)/2;

}

static int punto_equals(t_punto punto, int fila, int columna){

	 return (punto.fila==fila && punto.columna==columna);

}

static int punto_valido(t_punto punto, int size){

	return (var_entre(punto.fila,0,size) && var_entre(punto.columna,0,size));

}

void tablero_llenar(char ** tablero, int size){

	int medio=(tablero_max(size))/2;
	int i, j, max;

	tablero[medio][medio]=TABLERO_JUGADOR_REY;

	tablero[0][0] = tablero[0][tablero_max(size)] = 
					tablero[tablero_max(size)][0] =
					tablero[tablero_max(size)][tablero_max(size)] = TABLERO_CASILLA_CASTILLO;


	/* casilleros horizontales */
	tablero[1][medio] = tablero[tablero_max(size)-1][medio] = 
						tablero[medio][1] = 
						tablero[medio][tablero_max(size)-1] = 
							TABLERO_JUGADOR_ENEMIGO;
	max=tropas_lenght(size);

	for(i=medio;i<=(medio+max);i++){
		tablero[0][i] = tablero[0][tablero_max(size)-i] = 
						tablero[tablero_max(size)][i] = 
						tablero[tablero_max(size)][tablero_max(size)-i] = 
							TABLERO_JUGADOR_ENEMIGO;

		tablero[i][0] = tablero[tablero_max(size)-i][0] = 
						tablero[i][tablero_max(size)] = 
						tablero[tablero_max(size)-i][tablero_max(size)] = 
							TABLERO_JUGADOR_ENEMIGO;
	}

	for(i=medio-1; i<=medio+1;i++){

		for(j=medio-1; j<=medio+1;j++){

			if(i!=medio || j!=medio)
				tablero[i][j]=TABLERO_JUGADOR_GUARDIA;
		}
	}

	if(size>TABLERO_LARGO){

			tablero[medio][medio+2] = tablero[medio+2][medio] = 
					tablero[medio][medio-2] =
					tablero[medio-2][medio] = TABLERO_JUGADOR_GUARDIA;

	}

}

static int es_oponente(t_juego * juego, t_punto punto){

	casillero ficha;

	if(!punto_valido(punto, juego->size))
		return 0;

	ficha=juego->tablero[punto.fila][punto.columna];

	if(ficha==TABLERO_CASILLA_CASTILLO)
		return 1;

	if(juego->jugador==TABLERO_JUGADOR_GUARDIA && ficha==TABLERO_JUGADOR_ENEMIGO)
		return 1;

	if(juego->jugador==TABLERO_JUGADOR_ENEMIGO && (ficha==TABLERO_JUGADOR_REY || ficha==TABLERO_JUGADOR_GUARDIA))
		return 1;

	return 0;
}

static int es_aliado(t_juego * juego, t_punto punto){

	casillero ficha;

	if(!punto_valido(punto, juego->size))
		return 0;

	ficha=juego->tablero[punto.fila][punto.columna];

	if(ficha==TABLERO_CASILLA_CASTILLO)
		return 1;

	if(juego->jugador==TABLERO_JUGADOR_GUARDIA && (ficha==TABLERO_JUGADOR_REY || ficha==TABLERO_JUGADOR_GUARDIA || ficha==TABLERO_CASILLA_TRONO))
		return 1;

	if(juego->jugador==TABLERO_JUGADOR_ENEMIGO && ficha==TABLERO_JUGADOR_ENEMIGO)
		return 1;

	return 0;
}

static int verificar_bloqueos(t_juego * juego, t_punto destino){

	int fil,col,i=0,fil_aux,col_aux;

	t_punto pos_aliado,pos_enemigo;

	/* Itera sobre los puntos adyacentes*/
	for(col=destino.columna-1; col<=destino.columna+1; col++){
		for(fil=destino.fila-1; fil<=destino.fila+1; fil++){

			if((fil!=destino.fila && col!=destino.columna) || (fil==destino.fila && col==destino.columna))
				continue;

			/*printf("%d, %d\n", fil,col);*/

			pos_enemigo.fila=fil;
			pos_enemigo.columna=col;

			if(es_oponente(juego, pos_enemigo)){

				printf("Enemigo en "); PRINT_PUNTO(pos_enemigo); putchar('\n');

				if(juego->tablero[fil][col]==TABLERO_JUGADOR_REY){

					printf("Detectado rey enemigo en "); PRINT_PUNTO(pos_enemigo); putchar('\n');

					/*Verifica que el rey este rodeado por 4 aliados */
					for(col_aux=col-1; col_aux<=col+1; col_aux++){
						for(fil_aux=fil-1; fil_aux<=fil+1; fil_aux++){

							printf("Analizando [%d, %d]\n", fil_aux,col_aux);

							if((fil_aux!=fil && col_aux!=col) || (fil_aux==fil && col_aux==col))
								continue;

							pos_aliado.fila=fil_aux;
							pos_aliado.columna=col_aux;

							if(es_aliado(juego, pos_aliado)){
								i++;
								printf("Rey atacado por "); PRINT_PUNTO(pos_aliado); putchar('\n');
							}
								

						}
					/* El rey esta rodeado */
					if(i==4)
						return 1;
					}

					continue;
				}
				
				/* La ficha oponente no es el rey, veo si esta capturada */

				pos_aliado.fila=destino.fila+(fil-destino.fila)*2;
				pos_aliado.columna=destino.columna+(col-destino.columna)*2;

				if(es_aliado(juego, pos_aliado)){

					printf("Aliado en "); PRINT_PUNTO(pos_aliado);

					printf("Ficha en posicion [%d, %d] capturada.\n", fil,col);

					/*TODO: vector con fichas capturadas*/

					juego->tablero[fil][col]=TABLERO_CASILLA_VACIA;

				}

			}



		}

	}

	if( juego->jugador == TABLERO_JUGADOR_GUARDIA )

		juego->jugador = TABLERO_JUGADOR_ENEMIGO;

	else if( juego->jugador == TABLERO_JUGADOR_ENEMIGO )

		juego->jugador = TABLERO_JUGADOR_GUARDIA;


	return 0;

}

int move(t_juego * juego, t_punto origen, t_punto destino){

	int medio=(tablero_max(juego->size))/2;

	int fil,col;

	casillero ficha;

	ficha=juego->tablero[origen.fila][origen.columna];

	/* El punto esta fuera del tablero*/
	if(!punto_valido(origen, juego->size) || !punto_valido(destino, juego->size))
		return ERROR_TABLERO_PUNTO_EDOM;
	
	/* La fila ó columna de origen y destino no coinciden*/
	if(origen.fila != destino.fila && origen.columna != destino.columna)
		return ERROR_TABLERO_MOVIMIENTO_INVALIDO;

	if(!es_aliado(juego, origen))
		return ERROR_TABLERO_MOVIMIENTO_JUGADOR;

	/* Verifica si el destino es un castillo y la ficha no es el rey*/
	if(ficha!=TABLERO_JUGADOR_REY && ( punto_equals(destino,0,0) ||
										punto_equals(destino,0,tablero_max(juego->size)) ||
										punto_equals(destino,tablero_max(juego->size),0) ||
										punto_equals(destino,tablero_max(juego->size),tablero_max(juego->size))))
		return ERROR_TABLERO_MOVIMIENTO_CASTILLO;


	/* Tablero corto y el destino es el trono */
	if(juego->size<TABLERO_LARGO && ficha==TABLERO_JUGADOR_REY && punto_equals(destino,medio,medio))
		return ERROR_TABLERO_MOVIMIENTO_INVALIDO;

	/* Se verifica que el camino este vacio, y sea valido*/
	for(fil=origen.fila; COND_MOV_BILATERAL(fila,fil); COND_INCR_BILATERAL(fila,fil)){

		for(col=origen.columna; COND_MOV_BILATERAL(columna,col); COND_INCR_BILATERAL(columna,col)){

			 if(punto_equals(origen,fil,col))
			 	continue;

			 /* Verifica si el camino esta bloqueado por otra ficha o si es el tablero largo y no se es el enemigo*/
			if( juego->tablero[fil][col]!=TABLERO_CASILLA_VACIA || (juego->size>TABLERO_LARGO && juego->tablero[fil][col] == TABLERO_CASILLA_TRONO && ficha != TABLERO_JUGADOR_ENEMIGO))
				return ERROR_TABLERO_MOVIMIENTO_BLOQUEADO;

		}


	}


	printf("[%d, %d] -> [%d, %d]\n", origen.fila,origen.columna, destino.fila,destino.columna);
	if(origen.fila==medio && origen.columna==medio)
		juego->tablero[origen.fila][origen.columna]=TABLERO_CASILLA_TRONO;
	else
		juego->tablero[origen.fila][origen.columna]=TABLERO_CASILLA_VACIA;

	juego->tablero[destino.fila][destino.columna]=ficha;

	return verificar_bloqueos(juego, destino);

}

