package gui;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import metier.Jeu;
import thread.Th_com;

public class App extends Application {
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 600;
	public static String host = "";
	public static int port = 0;
	public static Socket soc;
	public static Jeu jeu;
	public static Th_com th_com;
	public static View_jouer_controller view;
	
    private Stage primaryStage;
    private BorderPane rootLayout;
    
    public App(){
    	//System.out.println(host + "-" + port);
    	
		Socket soc = null;
		
		try {
			soc = new Socket("127.0.0.1", port);
			App.soc = soc;
			System.out.println("connecté au serveur");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Boogle");
        
        initRootLayout();
        
        showMenu();
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("../gui/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("../gui/menu.fxml"));
			AnchorPane menu = (AnchorPane) loader.load();
			menu.setPrefSize(FRAME_WIDTH, FRAME_HEIGHT);
			
            rootLayout.setCenter(menu);
            rootLayout.setStyle("-fx-background: #FFFFFF;");
            
            View_menu_controller controller = loader.getController();
            controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    
    public static void launch_app(String[] args) {
    	launch(args);
    }


	public BorderPane getRootLayout() {
		return rootLayout;
	}

	
}