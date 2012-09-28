/** @Pulkit
 * Used to send a notice to the other part across the network.
 * Notice types include: Join Game (sent by player), Start Game (sent by server), 
 * Terminated (notifying player's death) and End Game (sent by server).
 * Usage:
 * 1. Use constructor to initialize
 * 2. Serialize and send
 * 3. Use getNoticeType() to obtain the notice
 */
package components.packets;

public class NotificationPacket extends Packet{
	private static final long serialVersionUID = -6943114757835171974L;

	private int noticeType;

	/* Notice types */
	public static final int JOINGAME = 0;
	public static final int STARTGAME = 1;
	public static final int TERMINATED = 2;
	public static final int ENDGAME = 3;
	public static final int REFRESHREQUEST = 4;

	/* Is monster? */
	private Boolean monster;
	/* Join packet preferred position */
	private int preferredPosition;

	public NotificationPacket(int noticeType){
		super(Packet.NOTIFICATIONPACKET);
		this.noticeType = noticeType;
		this.monster = false;
	}

	public NotificationPacket(int noticeType, Boolean monster){
		/* For monster declaration */
		super(Packet.NOTIFICATIONPACKET);
		this.noticeType = noticeType;
		this.monster = monster;
	}

	public NotificationPacket(int noticeType, int preferredPosition){
		/* For player joining */
		super(Packet.NOTIFICATIONPACKET);
		this.noticeType = noticeType;
		this.preferredPosition = preferredPosition;
		this.monster = false;
	}

	public int getNoticeType(){
		return noticeType;
	}

	public Boolean isMonster(){
		return monster;
	}
	
	public int getPreferredPosition(){
		return preferredPosition;
	}
}
