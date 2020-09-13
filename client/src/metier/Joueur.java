package metier;

import java.util.ArrayList;

public class Joueur {
	private int id;
	private String pseudo;
	private int score;
	private ArrayList<String> mots;
	
	public Joueur(String p, int s) {
		pseudo = p;
		score = s;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
	
	
}
