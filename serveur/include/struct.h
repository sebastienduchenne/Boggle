#ifndef STRUCT_H
#define STRUCT_H


typedef struct joueur joueur;
struct joueur{
	struct sockaddr_in sin;
	int soc_com;
	char pseudo[50];
	int pointsSession;
    int score;
	char motPlace[16];
};

/*élément d'une liste chainée*/
typedef struct elem elem;
struct elem{
	elem *prev;
	joueur joueur;
	elem *next;
};

/*liste chainée*/
typedef struct list list;
struct list{
	elem *first;
	elem *last;
};


#endif
