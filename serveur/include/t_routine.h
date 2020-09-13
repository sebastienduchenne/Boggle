#ifndef T_ROUTINE_H
#define T_ROUTINE_H

void *routine_game(void* arg);
void new_session();
void new_tour();
void phase_recherche();
void phase_soumission();
void phase_resultat();
void *routine_receiver(void *arg);

#endif
