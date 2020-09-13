package thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import gui.App;
import gui.View_jouer_controller;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import metier.Jeu;

public class Th_com extends Thread{
	private Socket soc;
	private Th_temps th_temps;
	private Jeu jeu;
	private View_jouer_controller view;
	private BufferedReader inchan;
	private InputStream is;
	private String reponse;
	
	
	public Th_com() {
		soc = App.soc;
		jeu = App.jeu;
		view = App.view;
	}
	
	public void run() {
		System.out.println("Démarrage de th_com");
		try {
			inchan = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			is = soc.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(!this.isInterrupted()){
			try {
				reponse = "";
				//System.out.println("en attente d'un réponse ...");
				
				int ii = 0;
				while((ii = is.read()) != '\n') {
					if(ii != 0) {
						reponse += (char)ii;
					}
				}
				
				System.out.println("reponse: "+reponse);

				//if(reponse != null && !reponse.equals("")){System.out.println("rep:"+reponse);}
				
				String[] rep = reponse.split("/");
				String type = rep[0];
				
				if(type.equals("CONNECTE")){
					//ajouter le pseudo à la liste des joueurs et ajouter un message au chat
					jeu.connexion(rep[1]);
					String s = "[CO] " + rep[1] + " s'est connecté.";

					Platform.runLater(new Runnable(){
						public void run() {view.getLb_msg().setText(s);}
					});
					
				} else if(type.equals("BIENVENUE")){
					//remplir tirage et score puis afficher plateau
					jeu.new_tour(rep[1]);
					
					th_temps = new Th_temps(view.getLb_temps());
					th_temps.start();
					
					Platform.runLater(new Runnable(){
						public void run() {view.setTirage();}
					});
				} else if(type.equals("DECONNEXION")){
					//retirer le pseudo de la liste des joueurs + aff msg chat
					jeu.deconnexion(rep[1]);
					String s ="[DECO] " + rep[1] + " s'est déconnecté.";
					
					Platform.runLater(new Runnable(){
						public void run() {view.getLb_msg().setText(s);}
					});
					
				} else if(type.equals("SESSION")){
					//afficher page jouer
					
				} else if(type.equals("VAINQUEUR")){
					//afficher une page du résultat de la session
					String bilan = rep[1];
					//String[] rep = reponse.split("/");
				} else if(type.equals("TOUR")){
					System.out.println("tour");
					//System.out.println("view:"+view);
					//nouveau tirage + lancer le chrono
					jeu.new_tour(rep[1]);
					th_temps = new Th_temps(view.getLb_temps());
					th_temps.start();
					//System.out.println("tir:"+rep[1]);
					System.out.println("tirage:"+jeu.getTirage());
					
					Platform.runLater(new Runnable(){
						public void run() {view.setTirage();}
					});
					
					//afficher jouer
					
				} else if(type.equals("MVALIDE")){
					//ajouter le mot à la liste

					Platform.runLater(new Runnable(){
						public void run() {
							//ajout du mot à la liste
							view.getlv_mots().getItems().add(rep[1]);
							//effacer lignes et mot
							view.clear();
						}
					});
					
				} else if(type.equals("MINVALIDE")){
					//afficher msg de refus + raison
					String raison = rep[1];
					String s = "'"+view.getLb_mot().getText()+"' n'a pas été validé. Raison : " + raison;
					
					Platform.runLater(new Runnable(){
						public void run() {
							view.getLb_msg().setText(s);
							view.clear();
						}
					});
					
				} else if(type.equals("RFIN")){
					
					Platform.runLater(new Runnable(){
						public void run() {
							//bloquer le bouton d'envoie de mot
							view.getBt_add().setDisable(false);
							//message dans le chat
							view.getLb_msg().setText("FIN de la phase de recherche");
						}
					});
					
				} else if(type.equals("BILANMOTS")){
					//compléter liste des mots
					String mots_prop = rep[1];
					String scores = rep[2];
					jeu.setScores(scores);
					jeu.setMots_proposes(mots_prop);
					
					//afficher la page de résultats
					
				} else if(type.equals("RECEPTION")){
					//aff message public
					// [PUBLIC] > message
					String s = "[PUBLIC] > " + rep[1];
					
					Platform.runLater(new Runnable(){
						public void run() {view.getLb_msg().setText(s);}
					});
					
				} else if(type.equals("PRECEPTION")){
					//aff message privé
					// [PRIVE] toto > message
					String s = "[PRIVE] from " + rep[2] + " > " + rep[1];
					
					Platform.runLater(new Runnable(){
						public void run() {view.getLb_msg().setText(s);}
					});
					
				} else {
					System.out.println("Commande non reconnue");
				}
					
			} catch(Exception e) {
				System.out.println("**"+e.toString());
			}
		}
	}
	
	
}
