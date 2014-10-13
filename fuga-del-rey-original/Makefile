
SOURCES_main=main.c getnum.c
SOURCES_tablero=tablero.c tablero.h

CFLAGS=-Wall -pedantic -g

EXECUTABLE=fuga

all: fuga

tablero:
	gcc $(CFLAGS) -c $(SOURCES_tablero)

fuga: tablero
	gcc $(CFLAGS) -o $(EXECUTABLE) $(SOURCES_main) tablero.o
	
exec: all
	./fuga
	
debug: all
	gdb ./fuga

clean:
	rm fuga tablero.o tablero.h.gch