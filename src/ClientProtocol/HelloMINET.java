package ClientProtocol;
import java.net.*;

public class HelloMINET extends Protocol {
	String localIP;
	public HelloMINET(String localIP){
		super.content = "MINET" + super.sp + localIP + super.crlf ;
	}
}
