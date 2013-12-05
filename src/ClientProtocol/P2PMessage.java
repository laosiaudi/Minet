package ClientProtocol;
import java.net.*;

public class P2PMessage extends Protocol {
	String username;
	public P2PMessage(String username){
		super.content = "P2P1.0" + super.sp + "P2PMESSAGE" + super.sp + username + super.crlf;
	}
}
