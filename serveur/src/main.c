#define _XOPEN_SOURCE 700

#include <stdio.h>
#include <stdlib.h>
#include <sys/un.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <errno.h>
#include <string.h>
#include <netinet/in.h>
#include <unistd.h>
#include <fcntl.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <pthread.h>

#include "../include/struct.h"
#include "../include/t_routine.h"
#include "../include/globals.h"
#include "../include/func.h"


/*mutex*/
pthread_mutex_t mut_liste_joueurs = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mut_tirage = PTHREAD_MUTEX_INITIALIZER;


list liste_joueurs;
const char separateur[2] = "/";
const  char fin_rqt[2] = "\n";
int nbTours;
char points_par_taille[6][2] = {
    {0, 0},
    {1, 0},
    {2, 0},
    {3, 1},
    {4, 1},
    {5, 2},
    {6, 3},
    {7, 5},
    {8, 11}
};
char dico[378975][20];
char des[16][6] = {
    {'E','T','U','K','N','O'},
    {'E','V','G','T','I','N'},
    {'D','E','C','A','M','P'},
    {'I','E','L','R','U','W'},
    {'E','H','I','F','S','E'},
    {'R','E','C','A','L','S'},
    {'E','N','T','D','O','S'},
    {'O','F','X','R','I','A'},
    {'N','A','V','E','D','Z'},
    {'E','I','O','A','T','A'},
    {'G','L','E','N','Y','U'},
    {'B','M','A','Q','J','O'},
    {'T','L','I','B','R','A'},
    {'S','P','U','L','T','E'},
    {'A','I','M','S','O','R'},
    {'E','N','H','R','I','S'},
};
int game_started = 0;
pthread_t *th_game;
FILE *fp;

char type[20];
char phase[5];
char scores[50];
char tirage[16];
char temps[5];

int chrono;
int duree_REC = 300;
int duree_RES = 10;
int duree_entre_session = 20;


int main(int argc, char * argv[]){
	struct sockaddr_in sin;
	int sock_connexion;
	int PORTSERV;
	unsigned int taille_addr = sizeof(sin);
	pthread_t *th_receiver;
	char from_client[500];
	char to_client[500];
	joueur *j;
	elem *elt;
  char mot[16];
  int dico;
  int i = 0;

	printf("\n*** Démarrage du serveur Boggle ***\n\n");



  srand(time(NULL));

  /*chargement du dico*/
  printf("\nChargement du dico... ");
  fp = fopen ("include/ODS5.txt", "r");
  printf("terminé\n");

  rewind(fp);
  dico = 0;



	liste_joueurs.first = NULL;
	liste_joueurs.last = NULL;
	PORTSERV = atoi(argv[1]);
  nbTours = atoi(argv[2]);
	srand(time(NULL));


	/*Mise en place de la socket*/
	if( (sock_connexion = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		perror("Erreur de creation de socket\n");
		return errno;
	}

	/* Initialisation de la socket */
	memset((char *) &sin, 0, sizeof(sin));
	sin.sin_addr.s_addr =  htonl(INADDR_ANY);/* inet_addr("127.0.0.1");*/
	sin.sin_port = htons(PORTSERV);
	sin.sin_family = AF_INET;

	/*binding*/
	if( bind(sock_connexion, (struct sockaddr *) &sin, sizeof(sin)) == -1){
		perror("Erreur de nommage de la socket\n");
		return errno;
	}

  /*On ecoute sur la socket*/
	listen(sock_connexion, 100);

	sprintf(phase, "DEB");


	while(1){
		memset(from_client, 0, sizeof(from_client));

		j = malloc(sizeof(struct joueur));
		elt = malloc(sizeof(struct elem));
		th_receiver = malloc(sizeof(pthread_t) );

		if( (j->soc_com = accept(sock_connexion, (struct sockaddr*) &(j->sin), &taille_addr)) == -1 ){
			perror("Erreur accept\n");return errno;
		}

    printf("nouvelle connexion\n");

		if (pthread_create(th_receiver, NULL, routine_receiver, (void*)j) != 0) {
			printf("pthread_create\n");exit(1);
		}

	}

	close(sock_connexion);
  fclose(fp);

	printf("Fin de communication.\nTerminaison du serveur.\n");
	return 0;

}

