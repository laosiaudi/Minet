package ClientProtocal;
import java.util.*;

public class Protocal {
	StringTrans trans = new StringTrans();
	String content;
	String CS = trans.StringToAsciiString("CS1.0");
	String ActionKey;
	public String getContent(){
		return content;
	}
}
