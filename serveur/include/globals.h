#ifndef GLOBALS_H
#define GLOBALS_H

#include "struct.h"

/*mutex*/
extern pthread_mutex_t mut_liste_joueurs;
extern pthread_mutex_t mut_tirage;

/*variables*/
extern list liste_joueurs;
extern const char separateur[2];
extern int nbTours;
extern char points_par_taille[6][2];
extern char dico[378975][20];
extern char des[16][6];
extern FILE *fp;
extern int game_started;
extern pthread_t *th_game;
extern const char fin_rqt[2];

/*chrono*/
extern int chrono;
extern int duree_REC;
extern int duree_RES;
extern int duree_entre_session;

/*envoie au client*/
extern char type[20];
extern char phase[5];
extern char scores[50];
extern char tirage[16];
extern char temps[5];


#endif
