import java.io.*;
import java.net.*;
import java.util.*;

import ClientProtocal.*;

public class Client{
	String toScreen;
	String toServer;
	String fromServer;
	String fromUser;
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	BufferedReader inFromUser;
	String localIP;
	String ServerIP = "172.18.158.39";
	static public String username;
	static public boolean connecting = false;
	
	/*connect and send hello*/
	public boolean hello() throws Exception{
		
		clientSocket = new Socket(ServerIP,6770);
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
	public boolean login(String username) throws Exception{
		if (connecting){
			int p2pPort = 6789;
			try{
				Login loginProtocal = new Login(username,p2pPort);
			
				toServer = loginProtocal.getContent();
				String []first = toServer.split("\r\n");
				System.out.println(first[0]);
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				
				inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				outToServer.writeBytes(toServer + "\n");
				while (true)
				{
					fromServer = inFromServer.readLine();
					if (fromServer != null){
						String []ifsuccess = fromServer.split(" ");
						if (ifsuccess[1].equals("1"))
							return true;
						else
							return false;
					}
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return false;
	}

	public void process(final Socket connectionSocket) throws IOException{
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
		Client client = new Client();
		connecting = client.hello();
		if(connecting){
			System.out.println("connection success!");
			System.out.print("Please login your username: ");
			Scanner in = new Scanner(System.in);
			username = in.next();
			if (client.login(username)){
				System.out.println("Login success!");
				ServerSocket welcomeSocket;
				Socket connectionSocket;
				welcomeSocket = new ServerSocket(6789);
				while (connecting){
					connectionSocket = welcomeSocket.accept();
					client.process(connectionSocket);
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
