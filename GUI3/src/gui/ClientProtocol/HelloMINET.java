package gui.ClientProtocol;

import java.net.*;

public class HelloMINET extends Protocol {
	String hostname;
	public HelloMINET(String hostName){
		super.content = "MINET" + super.sp + hostName + super.crlf ;
	}
}
