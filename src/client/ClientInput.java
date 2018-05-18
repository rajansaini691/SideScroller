package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Reads messages from server 
 * @author rajans1900
 *
 */
public class ClientInput extends Thread {
	
	private DataInputStream din;
	private boolean running = true;
	private Comp comp;
	
	public ClientInput(Comp comp, Socket socket) {
		this.comp = comp;
		
		try {
			din = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Reads init from server, setting ID
		try {
			comp.setID(din.readByte());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constantly listens to messages from server and passes them to comp object	 * 
	 */
	@Override
	public void run() {
		while(running) {
			try {
				comp.process(din.readByte());
			} catch (IOException e) {
				System.out.println("Server closed");
				System.exit(0);
			}
		}
	}
	
	/**
	 * When the server stops running, the thread gets closed. 
	 */
	public synchronized void close() {
		try {
			din.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		running = false;
	}
	
}
