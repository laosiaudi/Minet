package ClientProtocal;

public class Login extends Protocal{
	String userName;
	int Port;
	
	public String ToLogin(String un, int p){
		userName = un;
		Port = p;
		return content;
	}
}
