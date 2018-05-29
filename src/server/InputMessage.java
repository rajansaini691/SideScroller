package server;

public class InputMessage extends Message{
	
	public InputMessage(byte ID, byte message) {
		super(ID, message);
	}
	
	public InputMessage(byte[] data) {
		super(data[0], data[1]);
	}
	
	/**
	 * Message constants
	 */
	//Used as an ID
	public static final byte PLAYER_ALL = -1; 

	//Commands:
	public static final byte ADD_PLAYER = 0;
	public static final byte JUMP = 1;
	public static final byte DUCK = 2;
	public static final byte SABOTAGE = 3;
	public static final byte DIE = 4;
	public static final byte ADD_BLOCK = 5;
	public static final byte CAN_PLACE = 6;
	public static final byte READY_FOR_RESTART = 7;
	
}
