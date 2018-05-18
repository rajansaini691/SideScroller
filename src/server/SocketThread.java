package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * SocketThread listens for input from its client. It's created whenever
 * a client connects to facilitate communication between it and the server. 
 * 
 * @author rajans1900
 *
 */
public class SocketThread extends Thread {

	private Socket socket;
	private byte ID;
	private DataInputStream din;
	private DataOutputStream dout;
	private boolean closed = false;
	private SocketCollection collection;
	
	public SocketThread(Socket socket, byte ID, SocketCollection collection) {
		this.socket = socket;
		this.ID = ID;
		this.collection = collection;
		
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Initializes data: Sends ID to client and then listens for client's name
		try {
			dout.writeByte(ID);
			
			//Listens for name
			collection.setName(ID, din.readUTF());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Sends an outgoing message to the client
	 */
	public void transmit(byte message) {
		try {
			dout.writeByte(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Called when the thread starts running. As soon as this thread
	 * receives a message, it passes it to the main game so that it can
	 * respond adequately. 
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while(!closed) {
			try {
				byte id = din.readByte();
				byte command = din.readByte();
				
				InputMessage message = new InputMessage(id, command);
				collection.accept(message);
				
			} catch (IOException e) {
				closed = true;
			}
		}
		try {
			System.out.println("Player with ID " + ID + " disconnected");
			collection.removePlayer(ID);
			socket.close();
			Server.removeID(ID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
