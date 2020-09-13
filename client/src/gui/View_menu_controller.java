package gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import metier.Jeu;
import thread.Th_com;
import javafx.scene.control.TextField;

public class View_menu_controller {
    @FXML private Button bt_rejoindre;
    @FXML private TextField tf_pseudo;
    
    private App mainApp;
    
    @FXML
    private void initialize() { }
    
    
    public void showJouer() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("../gui/jouer.fxml"));
            AnchorPane battle = (AnchorPane) loader.load();

            BorderPane rootLayout = mainApp.getRootLayout();
            rootLayout.setCenter(battle);
            rootLayout.setStyle("-fx-background: #FFFFFF;");
            
            View_jouer_controller controller = loader.getController();
            App.view = controller;
        	App.th_com = new Th_com();
        	App.th_com.start();

            controller.setMainApp(mainApp);
            controller.setJeu(App.jeu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void new_part() {
    	if(tf_pseudo.getText().equals("")) {
    		System.out.println("pseudo vide");
    		return;
    	}
    	
    	try {Thread.sleep(200);} 
    	catch (InterruptedException e1) {e1.printStackTrace();}
    	
    	String pseudo = tf_pseudo.getText();
    	String rq = "/CONNEXION/"+pseudo;
    	OutputStream os;
    	PrintStream printStream;
		Socket soc = App.soc;
		InputStream is;
		
		App.jeu = new Jeu();

    	//envoyer le pseudo au serveur avec /CONNEXION/pseudo
    	try {
			os = soc.getOutputStream();
			printStream = new PrintStream(os);
			System.out.println("envoie de "+rq);
			printStream.print(rq);//envoi au serveur
			/*
			is = soc.getInputStream();
			int i;
			String reponse = "";
			while((i = is.read()) != 0){
				reponse += "" + (char)i;
			}
			
			System.out.println("r:"+reponse);
			String[] rep = reponse.split("/");
			String type = rep[0];
			
			if(type.equals("BIENVENUE")){
				String tirage = rep[1];
				App.jeu.setTirage(tirage);
			} else if(type.equals("SESSION")) {
				System.out.println("session");
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	showJouer();
    }
    
    
    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }
}
