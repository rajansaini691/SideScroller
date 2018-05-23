package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import server.InputMessage;

/**
 * Handles sending messages to server
 * 
 * @author rajans1900
 *
 */
public class ClientOutput {

	private DataOutputStream dout;

	public ClientOutput(Socket socket) {
		try {
			dout = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a message to the server in the form of a byte
	 * 
	 * @param InputMessage
	 *            message The message being sent to the server, carrying the command
	 *            and ID
	 */
	public void transmit(InputMessage inputMessage) {
		try {
			dout.writeByte(inputMessage.getID());
			dout.writeByte(inputMessage.getMessage());
		} catch (IOException e) {
			System.out.println("Connection closed by server");
			System.exit(0);
		}
	}

	/**
	 * Sends a string message to the server
	 * 
	 * @param message
	 */
	public void transmit(String message) {
		try {
			dout.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Connection closed by server");
			System.exit(0);
		}
	}

}
