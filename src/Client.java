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
        String ServerIP = "172.18.159.41";
        List onlineList = new LinkedList();
        static public String username;
        static public boolean connecting = false;
        static Map<String, String> userlist = new HashMap<String, String>();//key:user_name value:ip port
        
        /*connect and send hello*/
        public boolean hello() throws Exception{
                
                clientSocket = new Socket(ServerIP,6788);
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                InetAddress addr = InetAddress.getLocalHost();
                localIP = addr.getHostAddress().toString();
                
                HelloMINET helloMINET = new HelloMINET(ServerIP);
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
                                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                                
                                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                                
                                outToServer.writeBytes(toServer + "\n");
                                
                                StringBuilder temp = new StringBuilder();
                                int pre = '\0';
                                int ch;
                                while(0 <= (ch = inFromServer.read())) {
                    
                    if (ch == '\n' && pre == '\r')
                        break;
                    temp.append((char)ch);
                    pre = ch;
                }
                                fromServer = temp.toString();
                                
                                System.out.println(fromServer);
                                
                                if (fromServer != null){
                                        String []ifsuccess = fromServer.split(" ");
                                        if (ifsuccess[2].equals("1\r"))
                                                return true;
                                        else
                                                return false;
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
                                	 BufferedReader inFromP2P = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                                	 DataOutputStream outToP2P = new DataOutputStream(connectionSocket.getOutputStream());
                                }catch(IOException e){
                                        e.printStackTrace();
                                }
                        }
                }).start();
        }
        /*process the LIST protocol from the second line to the end, and list online friend to the user*/
        public void listOnline() throws IOException{
        	while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0){
        	}
        	userlist.clear();
        	while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0){
            	String []user = fromServer.split(" ");
            	userlist.put(user[0],user[1]+" "+user[2]);
        	}
        	Set<Map.Entry<String, String>> allSet=userlist.entrySet();
        	Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        	System.out.println("This is the user list:\n");
        	        while(iter.hasNext()){
        	            Map.Entry<String, String> me=iter.next();
        	             System.out.println(me.getKey()+ " "+me.getValue());
        	        }
        }
        
        /*process the UPDATE protocol from the second line to the end, and change the online friend list*/
        public void updateOnline() throws IOException{
        	String []options = fromServer.split(" ");
        	String status = options[2];
        	String userInfo = options[3];
        	while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0){
        		
        	}
        	if (status.equals('1')){
        		String []userInfos = userInfo.split(",");
        		userlist.put(userInfos[0], userInfos[1]+" "+userInfos[2]);
        	}
        	else if (status.equals('0')){
        		userlist.remove(userInfo);
        	}
        }
        
        /*process the CSMESSAGE protocol from the second line to the end, and change the online friend list*/
        public void csMessage(){
        	
        }
        
        /*listen the server port*/
        private void serverListen() throws IOException{
        	new Thread(new Runnable(){
        		public void run(){
        			try{
        				while(true){
        					while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0)
            				{
            					String []options = fromServer.split(" ");
                		        if (options[1].equals("LIST"))
                		        	listOnline();
                		        else if (options[1].equals("UPDATE"))
                		        	updateOnline();
                		        else if (options[1].equals("CSMESSAGE"))
                		        	csMessage();
            				}
        				}
            				
            				
        				
        					
			
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
                                client.serverListen();
                                ServerSocket welcomeSocket;
                                welcomeSocket = new ServerSocket(6667);
                                while (connecting){
                                	Socket connectionSocket = welcomeSocket.accept();
                                        client.process(connectionSocket);
//                                        System.out.println(client.inFromServer.readLine());
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
