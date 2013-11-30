package ClientProtocal;
import java.net.*;

public class LoginProtocal extends Protocal {
	String Port;
	public LoginProtocal(String port,String username){
		StringTrans trans = new StringTrans();
		Port = new String(port);
		super.content = trans.StringToAsciiString("CS1.0"+" LOGIN "+usrname+"\r\n"
				+ "Port " + Port + "\r\n");
	}
}
