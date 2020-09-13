package main;

import gui.App;

public class Main {
	
	public static void main(String[] args) {
		for(int i = 0; i < args.length; i++) {
			//System.out.println(args[i]);
			if(args[i].equals("-hostname")) {
				App.host = args[i+1];
			}
			if(args[i].equals("-port")) {
				App.port = Integer.parseInt(args[i+1]);
			}
		}
		
		App.launch_app(args);
	}
	
	
}
