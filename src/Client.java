import java.io.*;
import java.net.*;
import java.util.Scanner;

import ClientProtocal.StringTrans;
import ClientProtocal.Protocal;
import ClientProtocal.HelloMINET;
import ClientProtocal.LoginProtocal;


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
	public String username;
	static StringTrans trans = new StringTrans();
	static String ServerIP = "172.18.141.251";
	private boolean connecting = true;
	private ServerSocket welcomeSocket;
	private Socket connectionSocket;

	/*建立套接字并发送helloMINET*/
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
				String checkHello = trans.StringToAsciiString("MIRO") + " " + trans.StringToAsciiString(ServerIP);
				if(fromServer == checkHello)
					return true;
				else return false;
			}
			
		}
	}

	public static boolean login(String username) throws Exception{
		if (connecting){
			String newPort = new String("6789");
			try{
				LoginProtocal loginProtocal = new LoginProtocal(newPort);
				outToServer.wroteBytes(loginPortocal.getContent());

				fromServer = inFromeServer.readLine();
				if (fromServer != null){
					String []ifsuccess = fromServer.split(" ");
					if (ifsuccess[1] == 1)
						return true;
					else 
						return false;
				}else
					return false;
			}catch (IOException e){
				e.printStackTrace();
			}
		}else{
			return false;
		}
	}
	
	private void process(final Socket connectionSocket) throws IOException{
		new Thread(new Runnable(){
			public void run(){
				try{
					BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	/*用户输入登录用户名*/
	public static void main(String argv[]) throws Exception{
		connecting = hello();
		if(legal){
			System.out.println("connection success! Please login:");
			System.out.print("user name: ");
			Scanner in = new Scanner(System.in);
			username = in.next();
			if (login(username)){
				System.out.println("Login success!");
				welcomeSocket = new ServerSocket(6789);
				while (connecting){
					Socket connectionSocket = welcomeSocket.accept();
					server.process(connectionSocket);
				}
			}
			else
				System.out.println("Login error!");
		}
		else{
			System.out.println("connection error!");
		}
		
		
		
		
	}
}
