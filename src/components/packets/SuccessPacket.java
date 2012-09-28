/** @Pulkit 
 * Records the success or failure of an action
 * Usage: 
 * 1. Use constructor to initialize with success result
 * 2. Serialize and send
 * 3. Use getSuccess() to get result
 */
package components.packets;

public class SuccessPacket extends Packet{
	private static final long serialVersionUID = -9124494793724245073L;

	/* Record if a move/action was successful or not */
	private Boolean success;

	public SuccessPacket(Boolean success){
		super(Packet.SUCCESSPACKET);
		this.success = success;
	}
	public Boolean getSuccess(){
		return success;
	}
}
