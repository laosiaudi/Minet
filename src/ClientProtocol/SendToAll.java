package ClientProtocol;

public class SendToAll extends Protocol{
	String username;
	String message;
	public SendToAll(String un, String m){
		username = un;
		message = m;
		super.content = super.CS + "MESSAGE" + super.sp + username + super.crlf
				+ super.crlf + message + super.crlf;
	}
}
