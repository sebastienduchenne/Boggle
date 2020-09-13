package thread;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Th_temps extends Thread{
	private Label temps;
	private int nbs;
	
	public Th_temps(Label t) {
		temps = t;
	}
	
	public void run() {
		nbs = 300;
		
		while(nbs > 0) {
			
			Platform.runLater(new Runnable(){
				public void run() {
					int mn = nbs/60;
					int s = (nbs - mn*60);
					temps.setText(mn+"mn "+s+"s");
					//System.out.println("temps:"+mn+"mn "+s+"s");
				}
			});
			
			try {Thread.sleep(1000);}
			catch (InterruptedException e) {e.printStackTrace();}
			nbs--;
		}
	}
}
