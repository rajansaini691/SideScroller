package server;

public class OutputMessage extends Message {

	public OutputMessage(byte ID, byte message) {
		super(ID, message);
	}
	
	//Constants
	public static final byte START_GAME = 127;
	public static final byte CAN_PLACE = 126;
	public static final byte IMMOBILIZED = 125;
	public static final byte FREE = 124; 
	public static final byte DIE = 123;
	
}
