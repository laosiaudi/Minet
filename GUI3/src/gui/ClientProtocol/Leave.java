package gui.ClientProtocol;


public class Leave extends Protocol{
	String userName;
	public Leave(String un){
		userName = un;
		super.content = super.CS + super.sp + "LEAVE" + super.sp + userName + super.crlf
				+ super.crlf;
	}
}
