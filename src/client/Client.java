package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JFrame;

public class Client {
	
	public static void main(String[] args) {
		//Initializes networking variables
		Socket socket = null;
		ClientOutput client = null;
		ClientInput clientIn = null;					
		
		//Creates and initializes the frame	
		JFrame frame = new JFrame();
		Comp comp = new Comp();		
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(comp);
		frame.setVisible(true);
		
		//Wait for comp to finish entering in the info
		while(comp.getGameState() < 1) { 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
		
		//Waits to connect to server
		try {
			String host = comp.getHost();
			int port = comp.getPort();
			socket = new Socket(host, port);
			System.out.println("Logging in at address " + socket.getRemoteSocketAddress());
			
		} catch (SocketException e) {
			System.out.println("Failed to bind to given port. Using address " + comp.getHost() + " and port " + comp.getPort());
			e.printStackTrace();
			//System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to bind to given port. Using address " + comp.getHost() + " and port " + comp.getPort());
			//System.exit(0);
		}
		
		//Once connected to server, create input and output streams and pass it to comp so it can respond
		client = new ClientOutput(socket);
		comp.setOutputSocket(client);
		clientIn = new ClientInput(comp, socket);
		clientIn.start();
		
		//Gives the screen a title with the player's ID
		frame.setTitle("Client " + comp.getID());
		
	}
}
