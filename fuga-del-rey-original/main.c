#include "getnum.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "tablero.h"

#define clear() system("clear")

#define JUEGO_SAVE 1

#define JUEGO_QUIT 2

int imprimir_menu(void){

	int i=0;

	clear();

	printf("1)Juego Nuevo\n");
	printf("2)Recuperar un juego guardado\n");
	printf("3)Leer juego de archivo de texto\n");
	printf("4)Terminar\n");

	do{

		if (i)
			printf("Opción incorrecta. ");

		i=getint("Ingrese una opción: ");

	}while (!var_entre(i,1,5));

	clear();

	return i;
}

void pedir_archivo(char **nombre)
{
	int c,k=0;
	printf("Ingrese el nombre del archivo: ");
	while ((c=getchar())!='\n')
	{
		(*nombre)[k++]=c;
	}
	(*nombre)[k]=0;
}

char * get_full_path(int test_exist){

	int salir=0;
	char *nombre;
	char *path;


	path=malloc(300*sizeof(char));

	sprintf(path,"%s/",getenv("PWD"));

	nombre=malloc(30);

	do{
		pedir_archivo(&nombre);

		printf("%s\n",nombre);
		path=strcat(path,nombre);
		printf("%s\n",path);

		if (test_exist && !path_exist(path))
			printf("No se encontro el archivo\n");
		else
			salir=1;

	}while(!salir);

	free(nombre);

	path=realloc(path,strlen(path));

	return path;

}


int leer_jugada(char jugada[]){
	int c,k=0;

	while((c=getchar())!='\n' && k<15){
		jugada[k++]=c;
	}

	jugada[k]=0;

	if (k==15 || k==0){
		return ERROR_JUGADA;
	}

	return 0;
}


void procesar_guardado(t_juego *juego){

	char *path;

	path=get_full_path(0);

	guardar(path, juego);

	free(path);


}

int procesar_jugada(t_juego *juego, t_punto *origen, t_punto *destino){

	char ult;
	int errno,i=0, salir=0;

	char jugada[15];

	printf("Turno de: %s (%s)\n",juego->jugadores[(juego->jugador==TABLERO_JUGADOR_ENEMIGO)?0:1], (juego->jugador==TABLERO_JUGADOR_ENEMIGO)?"Enemigo":"Guardia");

	do{
		errno=leer_jugada(jugada);

		if(!juego->simulacion && errno==ERROR_JUGADA){
				printf("Comando Incorrecto. Ingrese nuevamente:\n");
				LIMPIA_BUFFER;
				break;

		}else{

			if (!juego->simulacion && strcmp(jugada,"undo")==0){

				printf("VOLVER ATRAS\n");
				salir=1;

			}else if (!juego->simulacion && strcmp(jugada,"save")==0){

					printf("GUARDAS\n");
					procesar_guardado(juego); /* TODO: */
					return JUEGO_SAVE;
			}else if (!juego->simulacion && strcmp(jugada,"quit")==0){

					return JUEGO_QUIT;
					
			}else{

					i=sscanf(jugada,"(%d,%d)(%d,%d%c",&(origen->fila),&(origen->columna),&(destino->fila),&(destino->columna),&ult);

					 if (i==5 && ult==')')
						salir=1;
					else if (!juego->simulacion){
						printf("Comando Incorrecto. Ingrese nuevamente:\n");
					}else{
						salir=1;
					}
						
			}

		}
	
	}while (salir==0);

	/* Es una simulacion y el comando es invalido*/
	if(i==0 && juego->simulacion){
		printf("Comando invalido, simulacion terminada\n");
		exit(0);
	}

	return 0;
}

void imprimir_tablero(char ** tablero, int size){

	int fila,columna; 

	printf("     ");
	
	for(columna=0; columna<size;columna++) {
		printf("%.2d  ", columna);
	}

	putchar('\n'); 
	for(fila=0;fila<size;fila++) { 
		printf("%-4d|", fila); 
									
		for(columna=0;columna<size;columna++) { 
			printf("%c   ", tablero[fila][columna]?tablero[fila][columna]:'-'); 
		} 
		putchar('\n'); 
	}

	putchar('\n'); 


}

void leer_nombre(char *nombre){

	int ret;
	int c,len=0;

	do{

		if ((c=getchar())=='\n')
				ret=ERROR_NOMBRE_INVALIDO;

		do{		

			nombre[len++]=c;

		}while ((c=getchar())!='\n' && len<29);

		if (len>=29)
			ret= ERROR_NOMBRE_INVALIDO;

		nombre[len]=0;

		if (ret==ERROR_NOMBRE_INVALIDO){

			LIMPIA_BUFFER;
			printf("Ingreso un nombre vacio o mayor a 30 caracteres!\nIngrese un nombre valido...");

		}else{
			break;
		}
	
	}while(1);
}

int main_loop(t_juego *juego){

	int errno, retval;
	char jugada[15];

	t_punto origen, destino;

	juego->jugador=TABLERO_JUGADOR_ENEMIGO; /* Jugador inicial */

	while(1){

		do{
			if(juego->simulacion){
				/* TODO*/
				errno=leer_jugada(jugada);
			}else{
				retval=procesar_jugada(juego, &origen, &destino);

				if(retval!=0)
					return retval;

				errno=move(juego, origen, destino);
				
				switch(errno){
					case ERROR_TABLERO_MOVIMIENTO_INVALIDO:		printf("No se puede mover en diagonal!\n");
																break;

					case ERROR_TABLERO_MOVIMIENTO_BLOQUEADO:	printf("Esta bloqueado el camino!\n");
																break;

					case ERROR_TABLERO_MOVIMIENTO_TRONO:		printf("No puede pasar por el trono!\n");
																break;

					case ERROR_TABLERO_MOVIMIENTO_CASTILLO:		printf("No se puede ir al castillo!\n");
																break;

					case ERROR_TABLERO_PUNTO_EDOM:				printf("Fuera del tablero!\n");
																break;

					case ERROR_TABLERO_MOVIMIENTO_JUGADOR:		printf("No es su pieza!\n");
																break;
				}

			}
				

		}while(errno<0);

		imprimir_tablero(juego->tablero,juego->size);

	}

}

void juego_nuevo(t_juego *juego){

	do{
		juego->size=getint("Ingrese la dimension del tablero: ");

		if ( (juego->tablero=tablero_crear(juego->size)) == NULL )
			printf("Dimensiones Invalidas! ");

	}while(juego->tablero==NULL);

	clear();

	tablero_llenar(juego->tablero, juego->size);

	printf("Nombre del atacante: ");
	leer_nombre(juego->jugadores[0]);
	
	printf("Nombre del defensor: ");
	leer_nombre(juego->jugadores[1]);
	
	imprimir_tablero(juego->tablero,juego->size);

	juego->simulacion=0;

	main_loop(juego);

	/* TODO: free tablero, jugadores*/
}


void regenerar_tablero(t_juego *juego)
{
	char *path;

	path=get_full_path(1);

	load(path, juego);

	free(path);

	imprimir_tablero(juego->tablero, juego->size);

	juego->simulacion=0;

	main_loop(juego);

}

int main(void)
{
	int eleccion;

	t_juego *juego;

	juego=malloc(sizeof(t_juego));

	eleccion=imprimir_menu();

	switch(eleccion){
		case 1: juego_nuevo(juego);
				break;

		case 2: 
				regenerar_tablero(juego);
				break;
		/*case 3: simular_juego;
				break;*/
		case 4: printf("Ha salido del juego.\n");
				break;
	}

	printf("Ha salido del juego\n");

	free(juego);
	
	return 0;

}