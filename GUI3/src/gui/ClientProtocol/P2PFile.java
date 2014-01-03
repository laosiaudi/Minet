package gui.ClientProtocol;
import java.net.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
public class P2PFile extends Protocol {
	String username;
	public P2PFile(String username, int Port){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date Current_Date = new Date(System.currentTimeMillis());
        String DateStr = formatter.format(Current_Date);
		super.content = "P2P1.0" + super.sp + "P2PFile" + super.sp + username + super.crlf
		+ "Date" + super.sp + DateStr + super.crlf
		+ "Content-Type" + super.sp + "file" + super.crlf
		+ "Port" + super.sp + "" + Port + super.crlf;
	}
}
