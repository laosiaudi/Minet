package ClientProtocol;

public class HeartBeat extends Protocol{
	String userName;
	public HeartBeat(String un){
		userName = un;
		super.content = super.CS + super.sp + "BEAT" + super.sp + userName + super.crlf
				+ super.crlf + super.crlf;
	}
}
