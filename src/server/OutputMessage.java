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
	public static final byte CAN_SABOTAGE = 122;
	public static final byte WARNING_REVERSE = 121;
	public static final byte WARNING_OBSCURE = 120;
	public static final byte WARNING_DELAY_JUMP = 119;
	public static final byte REVERSE = 118;
	public static final byte OBSCURE = 117;
	public static final byte DELAY_JUMP = 116;
	
}
