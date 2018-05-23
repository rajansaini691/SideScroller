package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import game.Comp;

public class Server {
	
	private static int[] IDs = new int[2];
	private static boolean acceptingNewClients = true;
	
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	
	public static void main(String[] args) {		
		//Initializes networking
		int port = 2222;
		ServerSocket ss = null;
		
		NetworkBridge bridge = new NetworkBridge();
		PlayerCollection players = new PlayerCollection(bridge);
		SocketCollection sockets = new SocketCollection(bridge);
		bridge.init(sockets, players);
		
		//Initializes GUI
		Comp comp = new Comp(players);
		JFrame frame = new JFrame();
		frame.setTitle("RAJAN'S FINAL GAME!!!");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(comp);
		frame.setVisible(true);
		
		//Sets actual screen width and height for everyone else to use
		SCREEN_WIDTH = frame.getContentPane().getWidth();
		SCREEN_HEIGHT = frame.getContentPane().getHeight();
		
		//Starts server
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			System.exit(0);
		}
		
		//Waits until four clients are connected at once, then breaks out of the loop
		while(acceptingNewClients) {
			try {
				Socket s = ss.accept();
				System.out.println("Client accepted with host " + s.getInetAddress().getHostAddress() + " and port " + s.getPort());
				byte key = getNextID();
				
				sockets.addSocket(key, s);
				System.out.println("A player joined with ID " + key);
				
				//If the player added was the last player needed, then stop listening for new clients
				if(key == IDs.length || key == -1) break;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Waits until final player sets their name before broadcasting
		while(players.getPlayerFromKey((byte) IDs.length) == null) {}
		
		//Broadcasts that the game has started to every client
		System.out.println("### Starting game ###");
		sockets.broadcast(OutputMessage.START_GAME);
		comp.startGame();
		
	}
	
	/**
	 * Finds the next available ID, marks it for use, and returns it. Returns -1 if none are available.
	 * @return new ID
	 */
	private static byte getNextID() {
		for(int i = 0; i < IDs.length; i++) {
			if(IDs[i] == 0) {
				IDs[i] = i + 1;
				return (byte) (i + 1);
			}
		}
		return -1;
	}
	
	/**
	 * Searches for ID and frees it for future use
	 * @param ID
	 */
	public static void removeID(byte ID) {
		for(int i = 0; i < IDs.length; i++) {
			if(IDs[i] == ID) {
				IDs[i] = 0;
				break;
			}
		}
	}
	
}
