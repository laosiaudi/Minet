package ClientProtocal;
import java.net.*;

public class HelloMINET extends Protocal {
	String localIP;
	public HelloMINET(String localIP){
		super.content = "MINET" + " " + localIP + "\n";
	}
}
