package server;

public class Message {
	protected byte message;
	protected byte ID;
	
	public Message(byte ID, byte message) {
		this.message = message;
		this.ID = ID;
	}
	
	/**
	 * @return actual message
	 */
	public byte getMessage() {
		return message;
	}
	
	/**
	 * @return message's ID
	 */
	public byte getID() {
		return ID;
	}
	
	/**
	 * Converts the message to an array of bytes
	 * @return
	 */
	public byte[] getData() {
		return new byte[]{ID, message};
	}
	
}
