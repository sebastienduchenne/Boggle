package gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class View_resultat_controller {
    
    private App mainApp;
    
    
    @FXML
    private void initialize() { }
    
    
    public void showResTour() {
    	try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("../gui/resultats.fxml"));
            AnchorPane battle = (AnchorPane) loader.load();

            BorderPane rootLayout = mainApp.getRootLayout();
            rootLayout.setCenter(battle);
            rootLayout.setStyle("-fx-background: #FFFFFF;");
            
            View_jouer_controller controller = loader.getController();
            controller.setMainApp(mainApp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }
}
