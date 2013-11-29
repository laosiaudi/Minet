package ClientProtocal;
import java.net.*;

public class HelloMINET extends Protocal {
	String localIP;
	public HelloMINET(String localIP){
		StringTrans trans = new StringTrans();
		super.content = trans.StringToAsciiString("MINET") + " " + trans.StringToAsciiString(localIP) + "\n";
	}
}
