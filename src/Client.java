import java.io.*;
import java.net.*;
import java.util.*;

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
	static String ServerIP = "10.4.15.40";
	static String username;
	static boolean connecting = false;
	static ServerSocket welcomeSocket;
	static Socket connectionSocket;
	
	/*connect and send hello*/
	public static boolean hello() throws Exception{
		
		clientSocket = new Socket(ServerIP,6788);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		InetAddress addr = InetAddress.getLocalHost();
		localIP = addr.getHostAddress().toString();
		
		HelloMINET helloMINET = new HelloMINET(localIP);
		toServer = helloMINET.getContent();
		
		outToServer.writeBytes(toServer + "\n");
		while (true)
		{
			fromServer = inFromServer.readLine();
			if(fromServer != null){
				String checkHello = "MIRO" + " " + ServerIP;
                if(fromServer.equals(checkHello))
					return true;
				else return false;
			}
			
		}
	}
	
	/*login function*/
	public static boolean login(String username) throws Exception{
		if (connecting){
			int p2pPort = 6789;
			try{
				Login loginProtocal = new Login(username,p2pPort);
				
				toServer = loginProtocal.getContent();
				outToServer.writeBytes(toServer + "\n");
				fromServer = inFromServer.readLine();
				if (fromServer != null){
                    String []ifsuccess = fromServer.split(" ");
					if (ifsuccess[1].equals("1"))
						return true;
					else
						return false;
				}else
					return false;
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void process(final Socket connectionSocket) throws IOException{
		new Thread(new Runnable(){
			public void run(){
				try{
					inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
					outToServer = new DataOutputStream(connectionSocket.getOutputStream());
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void main(String argv[]) throws Exception{
		connecting = hello();
		if(connecting){
			System.out.println("connection success!");
			System.out.print("Please login your username: ");
			Scanner in = new Scanner(System.in);
			username = in.next();
			if (login(username)){
				System.out.println("Login success!");
				welcomeSocket = new ServerSocket(6789);
				while (connecting){
					connectionSocket = welcomeSocket.accept();
					process(connectionSocket);
				}
			}else{
				System.out.println("Login error!");
			}
		}
		else{
			System.out.println("connection error!");
		}
		
		
		
		
	}
}
