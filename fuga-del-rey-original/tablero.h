

#define PRINT_PUNTO(punto) {printf("[%d, %d]", punto.fila,punto.columna);}


#define var_entre(i,min,max) ((min) <= (i) && (i) < (max))

#define ERROR_TABLERO_DIMENSIONES_INVALIDAS -2

#define ERROR_TABLERO_MOVIMIENTO_INVALIDO -3
#define ERROR_TABLERO_MOVIMIENTO_BLOQUEADO -4
#define ERROR_TABLERO_MOVIMIENTO_TRONO -5
#define ERROR_TABLERO_MOVIMIENTO_CASTILLO -6
#define ERROR_TABLERO_MOVIMIENTO_JUGADOR -7
#define ERROR_TABLERO_PUNTO_EDOM -8

#define ERROR_JUGADA -9

#define ERROR_NOMBRE_INVALIDO -12

#define LIMPIA_BUFFER while (getchar() != '\n')

#define TABLERO_CASILLA_TRONO 'T'
#define TABLERO_CASILLA_CASTILLO 'X'
#define TABLERO_CASILLA_VACIA 0

#define TABLERO_JUGADOR_REY 'R'
#define TABLERO_JUGADOR_GUARDIA 'G'
#define TABLERO_JUGADOR_ENEMIGO 'N'

#define TABLERO_LARGO 12

typedef struct{

	int fila;
	int columna;

} t_punto;

typedef struct{

	char ** tablero;
	int size;
	int simulacion;
	char jugadores[2][30];
	char jugador;

} t_juego;

typedef char casillero;

char ** tablero_crear(int size);

void tablero_llenar(char ** tablero, int size);

int move(t_juego * juego, t_punto origen, t_punto destino);

#define ERROR_LOAD -10

void load(char * path, t_juego *juego);

void guardar(char *name, t_juego *juego);

int path_exist(char *path);