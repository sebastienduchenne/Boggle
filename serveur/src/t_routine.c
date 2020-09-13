#define _XOPEN_SOURCE 700

#include <stdio.h>
#include <stdlib.h>
#include <sys/un.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <string.h>
#include <netinet/in.h>
#include <unistd.h>
#include <fcntl.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <pthread.h>
#include <sys/wait.h>
#include <time.h>

#include "../include/t_routine.h"
#include "../include/globals.h"
#include "../include/func.h"




void *routine_game(void* arg){
	printf("routine_game started\n");

  while(1){
	  new_session();
  }

	pthread_exit(NULL);
}


void new_session(){
  int i;
	elem *elt;
	char to_client[500] = "SESSION/\n";

	printf("nouvelle session\n");

	/*send SESSION/ to all*/
  pthread_mutex_lock(&mut_liste_joueurs);
	elt = liste_joueurs.first;
	while(elt != NULL){
		printf("SESSION joueur %s\n", elt->joueur.pseudo);
		if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			perror("Erreur d'ecriture sur la socket\n");
		}
		elt = elt->next;
	}
  pthread_mutex_unlock(&mut_liste_joueurs);

  for(i = 0; i < nbTours; i++){
	  new_tour();
  }


  /*résultat session*/
  memset(to_client, 0, sizeof(to_client));
  sprintf(to_client, "VAINQUEUR/bilan/\n");

  pthread_mutex_lock(&mut_liste_joueurs);

  elt = liste_joueurs.first;
	while(elt != NULL){
		printf("VAINQUEUR joueur %s\n", elt->joueur.pseudo);
		if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			perror("Erreur d'ecriture sur la socket\n");
		}
		elt = elt->next;
	}
  pthread_mutex_unlock(&mut_liste_joueurs);

  chrono = duree_entre_session;
  while (chrono > 0) {
	  /*printf("%d\n", chrono);*/
		sleep(1);
    chrono--;
  }

}


void new_tour(){
  elem* elt;
	char to_client[500];
	int r;
	int i = 0;
	char l;
  char tir[17];
  char star[2] = "*";
  char scores[500];

  printf("***");
  printf("nouveau tour\n");
  printf("***");
  /*tirage random de 16 chiffres*/
  pthread_mutex_lock(&mut_tirage);
  memset(tirage, 0, sizeof(tirage));
  memset(tir, 0, sizeof(tir));
  memset(scores, 0, sizeof(scores));
  sprintf(tirage, "JEICBAPLEMXUEHFI");

 /* ne fonctionne pas

  r = rand() % 6;
  l = des[0][0];
  sprintf(tirage, l);
  printf("**%d-%c\n",r,l);
  
  for(i = 0; i < 15; i++){
    printf("*\n");
    r = rand() % 6;/* random int between 0 and 5
    printf("**\n");
    l = des[0][0];
    printf("**%d-%c\n",r,l);
    strcat(tirage, l);
  }*/
  pthread_mutex_unlock(&mut_tirage);

	printf("tirage : %s\n", tirage);
	sprintf(type, "TOUR");
  sprintf(scores, &nbTours);


  /*score*/
  pthread_mutex_lock(&mut_liste_joueurs);
	elt = liste_joueurs.first;
	while(elt != NULL){
    strcat(scores,star);
    strcat(scores,elt->joueur.pseudo);
    strcat(scores,star);
    snprintf(scores, sizeof scores, "%d", elt->joueur.score);
		elt = elt->next;
	}
  pthread_mutex_unlock(&mut_liste_joueurs);

	memset(to_client, 0, sizeof(to_client));
	strcat(to_client, type);
	strcat(to_client, "/");
	strcat(to_client, tirage);
	strcat(to_client, "/");
	strcat(to_client, scores);
	strcat(to_client, "/\n");


	/*send TOUR/ to all*/
  pthread_mutex_lock(&mut_liste_joueurs);

	elt = liste_joueurs.first;
	while(elt != NULL){
		printf("%s - TOUR - joueur %s\n", to_client, elt->joueur.pseudo);
		if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			perror("Erreur d'ecriture sur la socket\n");
		}
		elt = elt->next;
	}
  pthread_mutex_unlock(&mut_liste_joueurs);

	phase_recherche();
}


void phase_recherche(){
  sprintf(phase, "REC");
	chrono = duree_REC;

	printf("\n** phase recherche **\n\n");

  /*lancement chrono */
	chrono = duree_REC;
  while (chrono > 0) {
	  printf("%d\n", chrono);
		sleep(1);
    chrono--;
  }

  phase_resultat();
}


void phase_resultat(){
	char to_client[500];
  elem* elt;

  printf("\n** phase résultat **\n\n");
  sprintf(phase, "RES");

  /*> calculer les scores*/
   /*elt = liste_joueurs.first;
   while(elt != NULL){
     strcat(scores,"*");
     strcat(scores,elt->joueur.pseudo);
     strcat(scores,"*");
     strcat(scores,elt->joueur.score);
	   elt = elt->next;
  }*/


  /*envoyer les résultats à tout le monde*/
  memset(to_client, 0, sizeof(to_client));
  sprintf(to_client, "VAINQUEUR/bilan/\n");

  pthread_mutex_lock(&mut_liste_joueurs);
  elt = liste_joueurs.first;
	while(elt != NULL){
		printf("SESSION joueur %s\n", elt->joueur.pseudo);
		if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			perror("Erreur d'ecriture sur la socket\n");
		}
		elt = elt->next;
	}
  pthread_mutex_unlock(&mut_liste_joueurs);

  chrono = duree_RES;
  while (chrono > 0) {
	  printf("%d\n", chrono);
		sleep(1);
    chrono--;
  }

}


void *routine_receiver(void* arg) {
  elem* elt;
	joueur *j;
	char from_client[500];
	char to_client[500];
	char *type_clt;
	char CONNEXION[] = "CONNEXION";
	char TROUVE[] = "TROUVE";
	char SORT[] = "SORT";
  char ENVOIE[] = "ENVOIE";
  char PENVOIE[] = "PENVOIE";
	char raison[100];
	int verif;
	char *proposition;
  char *msg;
  char *pseudo;
  int i;
  int sc;

  printf("routine_receiver started\n");

	j = (joueur*)arg;


	while(1){
		memset(from_client, 0, sizeof(from_client));
		memset(to_client, 0, sizeof(to_client));

		if(read(j->soc_com, from_client, sizeof(from_client)) < 0){
			perror("Erreur de lecture de la socket\n");
		}

		printf("client : %s\n\n", from_client);

    type_clt = strtok(from_client, separateur);


		printf("client type : %s\n", type_clt);


    if(strcmp(type_clt, CONNEXION) == 0){/*CONNEXION nouveau joueur*/
      /*récup pseudo*/

		  pseudo = strtok(NULL, separateur);

		  memset(to_client, 0, sizeof(to_client));

		  strcpy(j->pseudo, pseudo);
      j->score = 0;
		  elt = malloc(sizeof(struct elem));
		  elt->joueur = *j;

		  /*ajout du joueur à la liste*/
      pthread_mutex_lock(&mut_liste_joueurs);
		  if(liste_joueurs.first == NULL){
			  printf("ajout du 1er joueur\n");
			  elt->prev = NULL;
			  elt->next = NULL;
			  liste_joueurs.first = elt;
			  liste_joueurs.last = elt;
		  } else {
			  elt->prev = liste_joueurs.last;
			  elt->next = NULL;
			  liste_joueurs.last->next = elt;
			  liste_joueurs.last = elt;
		  }
      pthread_mutex_unlock(&mut_liste_joueurs);

		  printf("nouveau joueur connecté : %s\n\n", elt->joueur.pseudo);

      /*envoie du tirage*/
      if(game_started == 0){/*si game_started = 0 : lancer routine_game*/

        if (pthread_create(&th_game, NULL, routine_game, NULL) != 0) {
		      printf("pthread_create\n");exit(1);
	      }
        game_started = 1;

      } else {
        /*send BIENVENUE to user*/
		    sprintf(type, "BIENVENUE");
        sprintf(scores, "3*t*6*y*5");

        /*sprintf(scores, nbTour);
        elt = liste_joueurs.first;
		    while(elt != NULL){
          strcat(scores,"*");
          strcat(scores,elt->joueur.pseudo);
          strcat(scores,"*");
          strcat(scores,elt->joueur.score);
			    elt = elt->next;
		    }*/


		    strcat(to_client, type);
		    strcat(to_client, separateur);
		    strcat(to_client, tirage);
        strcat(to_client, separateur);
		    strcat(to_client, scores);
        strcat(to_client, separateur);
        strcat(to_client, fin_rqt);
        
		    printf("send %s\n", to_client);

		    if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			    perror("Erreur d'ecriture sur la socket\n");
		    }

		    /*send CONNECTE/ to all*/
		    memset(to_client, 0, sizeof(to_client));
		    sprintf(type, "CONNECTE");

		    strcat(to_client, type);
		    strcat(to_client, separateur);
		    strcat(to_client, pseudo);
		    strcat(to_client, separateur);
		    strcat(to_client, fin_rqt);

        pthread_mutex_lock(&mut_liste_joueurs);
		    elt = liste_joueurs.first;
		    while(elt != NULL){
			    if(elt->joueur.soc_com != j->soc_com){
				    printf("CONNECTE joueur %s\n", elt->joueur.pseudo);
				    if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
					    perror("Erreur d'ecriture sur la socket\n");
				    }
			    }
			    elt = elt->next;
		    }
        pthread_mutex_unlock(&mut_liste_joueurs);
      }

    } else if(strcmp(type_clt, TROUVE) == 0){/*TROUVE*/
			proposition = strtok(NULL, separateur);
      printf("un client a envoyé le mot '%s', taille:%d\n", proposition, strlen(proposition));

      /*vérification du mot*/
      verif = verifier_mot(proposition);

      if(verif == 0){/*0 si OK*/
        printf("Le mot '%s' a été validé.\n", proposition);
        sprintf(to_client, "MVALIDE/");

        if(strlen(proposition) < 8){
          sc = points_par_taille[strlen(proposition)][1];
        } else {
          sc = points_par_taille[8][1];
        }
        j->score = j->score + sc;

        strcat(to_client, proposition);
        strcat(to_client, separateur);
      } else {
        printf("Le mot '%s' n'a pas été validé.\n", proposition);
        memset(raison, '\0', sizeof(raison));
        sprintf(to_client, "MINVALIDE/");
        if(verif == 1){
          strcpy(raison, "POSMot mal placé");
        } else {
          strcpy(raison, "DICMot inconnu");
        }
        
        strcat(to_client, raison);
      }

      strcat(to_client, fin_rqt);
      printf("%s-joueur-%s\n", to_client, j->pseudo);
		  if(write(j->soc_com, to_client, sizeof(to_client)) < 0){
			  perror("Erreur d'ecriture sur la socket\n");
		  }

    } else if(strcmp(type_clt, SORT) == 0){/*SORT pour déconnexion*/
      pseudo = strtok(NULL, separateur);
      supprimer_joueur(j->soc_com);

      /*envoyer à tt le monde la déco de user*/
      sprintf(to_client, "DECONNEXION/");
      strcat(to_client, pseudo);
      strcat(to_client, separateur);
      strcat(to_client, fin_rqt);

      pthread_mutex_lock(&mut_liste_joueurs);
		  elt = liste_joueurs.first;
		  while(elt != NULL){
			  if(elt->joueur.soc_com != j->soc_com){
			    printf("DECONNEXION joueur %s\n", elt->joueur.pseudo);
			    if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			      perror("Erreur d'ecriture sur la socket\n");
			    }
			  }
			  elt = elt->next;
		  }
      pthread_mutex_unlock(&mut_liste_joueurs);

    } else if(strcmp(type_clt, ENVOIE) == 0){/*ENVOIE*/
      msg = strtok(NULL, separateur);
      printf("ENVOIE de '%s'\n", msg);
      /*envoyer à tt le monde le msg*/
      sprintf(to_client, "RECEPTION/");
      strcat(to_client, msg);
      strcat(to_client, separateur);
      strcat(to_client, fin_rqt);

      pthread_mutex_lock(&mut_liste_joueurs);
      elt = liste_joueurs.first;
		  while(elt != NULL){
			  printf("ENVOIE joueur %s\n", elt->joueur.pseudo);
			  if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			    perror("Erreur d'ecriture sur la socket\n");
			  }
			  elt = elt->next;
		  }
      pthread_mutex_unlock(&mut_liste_joueurs);

    } else if(strcmp(type_clt, PENVOIE) == 0){/*PENVOIE*/
      msg = strtok(NULL, separateur);
      pseudo = strtok(NULL, separateur);
      /*envoyer à user le msg*/
      sprintf(to_client, "PRECEPTION/");
      strcat(to_client, msg);
      strcat(to_client, separateur);
      strcat(to_client, pseudo);
      strcat(to_client, separateur);
      strcat(to_client, fin_rqt);

      pthread_mutex_lock(&mut_liste_joueurs);
      elt = liste_joueurs.first;
      while(elt != NULL){
        if(strcmp(elt->joueur.pseudo, pseudo)){
			    printf("PENVOIE joueur %s\n", elt->joueur.pseudo);
			    if(write(elt->joueur.soc_com, to_client, sizeof(to_client)) < 0){
			      perror("Erreur d'ecriture sur la socket\n");
			    }
			  }
      }
      pthread_mutex_unlock(&mut_liste_joueurs);
    }


  }

	pthread_exit(NULL);
}

