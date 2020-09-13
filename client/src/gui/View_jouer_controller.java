package gui;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import metier.Jeu;
import javafx.scene.control.TextArea;

public class View_jouer_controller {
	private final String ALL = "Tout le monde";
	private ArrayList<Label> set_lb_mots;
	private ArrayList<Line> lines;
	private ArrayList<Label> cases;
	private Jeu jeu;
	private String[] coord = {
			"A1","A2","A3","A4",
			"B1","B2","B3","B4",
			"C1","C2","C3","C4",
			"D1","D2","D3","D4",
			};
	
	//plateau
	@FXML private AnchorPane ap_plateau;
    @FXML private Label case1;
    @FXML private Label case2;
    @FXML private Label case3;
    @FXML private Label case4;
    @FXML private Label case5;
    @FXML private Label case6;
    @FXML private Label case7;
    @FXML private Label case8;
    @FXML private Label case9;
    @FXML private Label case10;
    @FXML private Label case11;
    @FXML private Label case12;
    @FXML private Label case13;
    @FXML private Label case14;
    @FXML private Label case15;
    @FXML private Label case16;
    @FXML private Button bt_clear;
    
    //mots
	@FXML private ListView<String> lv_mots;
	@FXML private Label lb_temps;
	@FXML private Label lb_score;
	@FXML private Label lb_pseudo;

    //mot
	@FXML private Label lb_mot;
    @FXML private Button bt_add;
    @FXML private Button bt_quit;

    //chat
    @FXML private TextArea ta_chat;
    @FXML private AnchorPane ap_msg;
    @FXML private Label lb_to;
    @FXML private ComboBox<String> cb_to;
    @FXML private Label lb_msg;
    

	private App mainApp;
    
    public View_jouer_controller() {
    }
    
    @FXML
    private void initialize() {
    	set_lb_mots = new ArrayList<Label>();
    	lines = new ArrayList<Line>();
    	lb_mot.setText("");
    	cb_to.getItems().add(ALL);
    	cb_to.setValue(ALL);
    	
    	cases = new ArrayList<Label>();
    	cases.add(case1);
    	cases.add(case2);
    	cases.add(case3);
    	cases.add(case4);
    	cases.add(case5);
    	cases.add(case6);
    	cases.add(case7);
    	cases.add(case8);
    	cases.add(case9);
    	cases.add(case10);
    	cases.add(case11);
    	cases.add(case12);
    	cases.add(case13);
    	cases.add(case14);
    	cases.add(case15);
    	cases.add(case16);
    	
    	for(int i = 0; i < cases.size(); i++) {
    		cases.get(i).setTextFill(Color.BLACK);
    		cases.get(i).setText("T");
    	}
    	
    }
    
    
    public void select(Label lb) {
    	if(lb.getTextFill()==Color.BLACK) {//add lettre
    		lb.setTextFill(Color.RED);
    		set_lb_mots.add(lb);
    		
    		if(set_lb_mots.size() > 1) {//add line
    			relier();
    		}
    	} else {//remove lettre
    		lb.setTextFill(Color.BLACK);
    		set_lb_mots.remove(lb);
    		relier();
    	}
    	lb_mot.setText("");
    	String mot = "";
    	for(int i = 0; i < set_lb_mots.size(); i++) {
    		mot+=set_lb_mots.get(i).getText();
    	}
    	lb_mot.setText(mot);
    }
    
    public void relier() {
    	//System.out.println("relier");
    	remove_lines();
    	lines.clear();
    	for(int i = 0; i < set_lb_mots.size()-1; i++) {
			Label ls = set_lb_mots.get(i);
			Label le = set_lb_mots.get(i+1);
			
			int sx = (int)Math.round(ls.getLayoutX()+ls.getWidth()/2);
    		int sy = (int)Math.round(ls.getLayoutY()+ls.getHeight()/2);
			
			int ex = (int)Math.round(le.getLayoutX()+le.getWidth()/2);
    		int ey = (int)Math.round(le.getLayoutY()+le.getHeight()/2);
    		
			Line ln = new Line(sx,sy,ex,ey);
        	ln.setStroke(Color.RED);
			lines.add(ln);
        	ap_plateau.getChildren().add(ln);
		}
    }
    
    public void remove_lines() {
    	for(int i = 0; i < lines.size(); i++) {
    		ap_plateau.getChildren().remove(lines.get(i));
    	}
    }
    
    // gestion des boutons
    

    @FXML
    public void ajouter(){
    	//System.out.println("mot:"+lb_mot.getText());
    	System.out.println("size:"+set_lb_mots.size());
    	
    	if(lb_mot.getText().equals("") || lb_mot.getText()==null) {
    		System.out.println("pas de mot");
    		return;
    	}
    	
    	//vérifier la validité du mot
    	
    	//->vérifier que les label sont tous différents
    	/*for(int i = 0; i < set_lb_mots.size(); i++) {
    		for(int j = 0; j < cases.size(); j++) {
        		if(set_lb_mots.get(i) == set_lb_mots.get(j)) {
        			System.out.println("mot invalide : 2 fois la même lettre");
        		}
        	}
    	}*/
    	
    	//->vérifier la trajectoire
    	
    	//faire la trajectoire
    	String trajectoire = "";

    	for(int i = 0; i < set_lb_mots.size(); i++) {
    		for(int j = 0; j < cases.size(); j++) {
        		if(set_lb_mots.get(i) == cases.get(j)) {
        			trajectoire+=coord[j];
        		}
        	}
    	}
    	
    	System.out.println("traj:"+trajectoire);
    	
    	
    	//envoie du mot au serveur pour vérification
    	String rqt = "/TROUVE/"+lb_mot.getText().toLowerCase()+"/"+trajectoire;
    	OutputStream os;
    	PrintStream printStream;
		Socket soc = App.soc;
		
    	//envoyer /TROUVE/tirage/trajectoire
    	try {
			os = soc.getOutputStream();
			printStream = new PrintStream(os);
			System.out.println("envoie de "+rqt);
			printStream.print(rqt);//envoi au serveur
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    @FXML
    public void quitter(){
    	System.out.println("quitter");
    	System.exit(1);
    }
    
    @FXML
    public void send_message(){
    	System.out.println("send message");
    	
    	String rqt = "";
    	String dest = cb_to.getValue();
    	String msg = ta_chat.getText();
    	
    	System.out.println("rqt:"+rqt);
    	
    	if(dest.equals(ALL)) {
    		rqt = "ENVOIE/"+msg;
    	} else {
    		rqt = "PENVOIE/"+dest+"/"+msg;
    	}
    	
    	//envoie
    	OutputStream os;
    	PrintStream printStream;
		Socket soc = App.soc;
    	
		try {
			os = soc.getOutputStream();
			printStream = new PrintStream(os);
			System.out.println("envoie de "+rqt);
			printStream.print(rqt);//envoi au serveur
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ta_chat.clear();
    	
    }
    
    @FXML
    public void clear() {
    	remove_lines();
    	lines.clear();
    	for(int i = 0; i < cases.size(); i++) {
    		cases.get(i).setTextFill(Color.BLACK);
    	}
    	lb_mot.setText("");
    	set_lb_mots.clear();
    }
    
    
    
    /////////////////////////////////////////////////////
    
    @FXML public void selected_case1() {select(case1);}
    @FXML public void selected_case2() {select(case2);}
    @FXML public void selected_case3() {select(case3);}
    @FXML public void selected_case4() {select(case4);}
    @FXML public void selected_case5() {select(case5);}
    @FXML public void selected_case6() {select(case6);}
    @FXML public void selected_case7() {select(case7);}
    @FXML public void selected_case8() {select(case8);}
    @FXML public void selected_case9() {select(case9);}
    @FXML public void selected_case10() {select(case10);}
    @FXML public void selected_case11() {select(case11);}
    @FXML public void selected_case12() {select(case12);}
    @FXML public void selected_case13() {select(case13);}
    @FXML public void selected_case14() {select(case14);}
    @FXML public void selected_case15() {select(case15);}
    @FXML public void selected_case16() {select(case16);}
    
	public App getMainApp() {
		return mainApp;
	}

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }
    
    public AnchorPane getap_msg() {
    	return ap_msg;
    }
    
    public ListView<String> getlv_mots(){
    	return lv_mots;
    }
    
    public Button getBt_add() {
    	return bt_add;
    }
    
    public void setJeu(Jeu j) {
    	jeu = j;
    }
    
    public void setTirage() {
    	System.out.println("setTirage");
    	for(int i = 0; i < cases.size(); i++) {
    		cases.get(i).setTextFill(Color.BLACK);
    		cases.get(i).setText(""+jeu.getTirage().charAt(i));
    	}
    }
    
    public Label getLb_temps() {
    	return lb_temps;
    }
    
    public Label getLb_mot() {
    	return lb_mot;
    } 
    
    public Label getLb_msg() {
		return lb_msg;
	}
    
}
