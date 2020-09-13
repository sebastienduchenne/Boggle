# makefile pour jeu de lettre

CC =gcc
LDFLAGS =-lpthread -lrt
CFLAGS =-g -W -Wall -ansi -pedantic -Iinclude 

DIR=.
BIN=$(DIR)/bin/
OBJ=$(DIR)/obj/
INCLUDE=$(DIR)/include/
LIB=$(DIR)/lib/
SRC=$(DIR)/src/

HC=
O=$(OBJ)t_routine.o $(OBJ)func.o

.PHONY: all clean curl project
all: $(BIN)main

project: $(SRC)main
	-bin/main

$(BIN)%: $(OBJ)%.o $O
	@if [ -d $(BIN) ]; then : ; else mkdir $(BIN); fi
	$(CC) -o $@ $^ $(LDFLAGS)

$(OBJ)%.o: $(SRC)%.c $(HC)
	@if [ -d $(OBJ) ]; then : ; else mkdir $(OBJ); fi
	$(CC) $(CFLAGS) -o $@ -c $<

$(INCLUDE)%.h:
	@if [ -d $(INCLUDE) ]; then : ; else mkdir $(INCLUDE); fi

clean:
	rm -rf $(OBJ)*.o

