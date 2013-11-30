import java.io.*;
import java.net.*;

import ClientProtocal.*;



public class Client{
	static String toScreen;
	static String toServer;
	static String fromServer;
	static String fromUser;
	static Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	static BufferedReader inFromUser;
	static String localIP;
	static String ServerIP = "172.18.141.251";
	
	/*connect and send hello*/
	public static boolean hello() throws Exception{
		
		clientSocket = new Socket(ServerIP,6788);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		InetAddress addr = InetAddress.getLocalHost();
		localIP = addr.getHostAddress().toString();
		
		HelloMINET helloMINET = new HelloMINET(localIP);
		toServer = helloMINET.getContent();
		
		outToServer.writeBytes(toServer);
		while (true)
		{
			fromServer = inFromServer.readLine();
			if(fromServer != null){
				String checkHello = "MIRO" + " " + ServerIP;
				if(fromServer == checkHello)
					return true;
				else return false;
			}
			
		}
	}
	

	public static void main(String argv[]) throws Exception{
		if(hello()){
			System.out.println("connection success!");
		}
		else{
			System.out.println("connection error!");
		}
		
		
		
		
	}
}
