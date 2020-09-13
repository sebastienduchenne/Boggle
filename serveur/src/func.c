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
#include <time.h>

#include "../include/func.h"
#include "../include/globals.h"
#include "../include/struct.h"


void supprimer_joueur(int soc_com){
	elem *user = getElem(soc_com);
	elem *elt = liste_joueurs.first;
	elem *first = liste_joueurs.first;
	elem *last = liste_joueurs.last;
	elem *p;
	elem *n;

	printf("supprimer_joueur %d\n", soc_com);

	while(elt != NULL){
		if(user == first && user == last){/*un joueur*/
			printf("*\n");
			first = NULL;
			last = NULL;
			pthread_kill(th_game);
			game_started = 0;
			return;
		} else if(user == first){
			printf("**\n");
			first = user->next;
			first->prev = NULL;
			/*pthread_kill(th_game);*/
			return;
		} else if(user == last){
			printf("***\n");
			last = user->prev;
			last->next = NULL;
			/*pthread_kill(th_game);*/
			return;
		} else if(soc_com == elt->joueur.soc_com){
			printf("****\n");
			p = elt->prev;
			n = elt->next;
			p->next = n;
			n->prev = p;
			/*pthread_kill(th_game);*/
			return;
		}
		elt = elt->next;
	}

	free(user);
	free(elt);
	free(first);
	free(last);
}


elem* getElem(int soc_com){
	elem *elt = liste_joueurs.first;
	elem *user;
	printf("getElem\n");

	while(elt != NULL){
		if(soc_com == elt->joueur.soc_com){
			user = elt;
		}
		elt = elt->next;
	}

	return user;
}


int verifier_mot(char* proposition){
  int verif = 0;
  int i = 0;
  int j = 0;
  int dico = 0;
  char mot[17] = "";
  char prop[20];
  int placement = 0;

  

	memset(prop, 0, sizeof(prop));
  sprintf(prop, proposition);
  strcat(prop, "\n");

  printf("verifier_mot:%s",prop);
  
/*
0:valide
1:mot mal placé
2:mot inconnu

placement = vérifier placement
si placement OK
    dico = vérifier si mot dans le dico - MUTEX
    si dico OK
        vérif = 0
    sinon
        vérif = 2
sinon
    vérif = 1
*/

/*vérifier placement*/
  placement = 0;



  rewind(fp);
  while(dico == 0 && j < 378976){
    fgets(mot, 17, fp);
    if(strcmp(mot, prop) == 0){
      dico = 1;
    }
    j++;
  }

  if(dico == 1){
    verif = 0;
  } else {
    verif = 2;
  }

  printf("verif:%d\n",verif);
	return verif;
}
