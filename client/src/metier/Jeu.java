package metier;

import java.util.ArrayList;

public class Jeu {
	private String phase;
	private ArrayList<Joueur> autres;
	private Joueur soi;
	private int nbTour;
	private String tirage;
	private ArrayList<String> mots_proposes;
	
	
	public void new_tour(String tirage) {
		if(tirage.length() != 16) {
			return;
		}
		this.tirage = tirage;
		this.autres = new ArrayList<Joueur>();
	}
	
	public void connexion(String pseudo) {
		autres.add(new Joueur(pseudo,0));
	}
	
	public void deconnexion(String pseudo) {
		int i = 0;
		while(autres.get(i).getPseudo().equals(pseudo)) {
			autres.remove(i);
			i++;
		}
	}
	
	public String getTirage() {
		return tirage;
	}

	public void setTirage(String tirage) {
		this.tirage = tirage;
	}
	
	public void setScores(String scores) {
		String[] sc = scores.split("*");
		
		for(int i = 0; i < autres.size(); i++) {
			autres.get(i).setScore(Integer.parseInt(sc[i]));
		}
	}
	
	public void setMots_proposes(String mots) {
		String[] mp = mots.split("*");
		for(int i = 0; i < autres.size(); i++) {
			mots_proposes.add(mp[i]);
		}
	}
	
	
	
}
