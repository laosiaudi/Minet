import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

import ClientProtocol.*;

public class Client{
        String toScreen;
        String toServer;
        String fromServer;
        String fromUser;
        Socket clientSocket;
        Socket P2Psocket;
        DataOutputStream outToServer;
        DataOutputStream outToP2PServer;
        BufferedReader inFromServer;
        BufferedReader inFromUser;
        BufferedReader inFromP2PServer;
        String localIP;
        String ServerIP = "172.18.159.41";
        List onlineList = new LinkedList();
        static public String username = new String();
        static public boolean connecting = false;
        static Map<String, String> userlist = new HashMap<String, String>();//key:user_name value:ip port
        Timer timer = new Timer();
        static Map<String, Socket> chating_user_list = new HashMap<String, Socket>();
        static Map<String, String> beat_time = new HashMap<String, String>();
        Socket connectionSocket;
        
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
                                Login loginProtocol = new Login(username,p2pPort);
                        
                                toServer = loginProtocol.getContent();
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
        	System.out.println(fromServer);
            String []options = fromServer.split(" ");
            String status = options[2];
            String updateUserName = options[3];
            String []updateUserInfo = options[4].split(",");
            while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0){
                    System.out.println(fromServer);
            }
            if (status.equals('1')){
                    userlist.put(updateUserName, updateUserInfo[0]+" "+updateUserInfo[1]);
            }
            else if (status.equals('0')){
                    userlist.remove(updateUserName);
            }
            Set<Map.Entry<String, String>> allSet=userlist.entrySet();
            Iterator<Map.Entry<String, String>> iter=allSet.iterator();
            System.out.println("This is the user list:\n");
                    while(iter.hasNext()){
                        Map.Entry<String, String> me=iter.next();
                         System.out.println(me.getKey()+ " "+me.getValue());
                    }
        }
        
        /*process the CSMESSAGE protocol from the second line to the end, and change the online friend list*/
        public void csMessage() throws IOException{
        	String []options = fromServer.split(" ");
        	String fromUserName = options[3];
        	while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0){
        	}
        	while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0){
            	System.out.println(fromUserName + "send to all: \"" + fromServer +"\"\n");
        	}
        }
        
        /*9s to send the heartBeat protocol*/
        public void heartBeat(){
        	Timer timer;
        	timer = new Timer(true);
        	HeartBeat heartBeatProtocol = new HeartBeat(username);
        	final String sendBeat = heartBeatProtocol.getContent();
        	timer.schedule(
        	new TimerTask() { 
        		public void run(){ 
        			try {
						outToServer.writeBytes(sendBeat + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
        		} }, 0, 9*1000);
        }   
        
        /*listen the server port*/
        private void serverListen() throws IOException{
        	new Thread(new Runnable(){
        		public void run(){
        			try{
        				while(connecting){
        					while((fromServer = inFromServer.readLine())!=null && fromServer.length()>0)
            				{
        						System.out.println(fromServer);
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
        
        private void P2Plistener() throws IOException {
        	new Thread(new Runnable(){
        		public void run(){
        			try{
        				ServerSocket welcomeSocket;
                        welcomeSocket = new ServerSocket(6789);
        				while(connecting){
        					connectionSocket = welcomeSocket.accept();
        					process(connectionSocket);
        				}
        			}catch(IOException e){
        				e.printStackTrace();
        			}
        		}
        	}).start();
        }
        
        /*build P2P-chating TCP*/
        private boolean hello_P2P(String uname) throws IOException{
        	String P2Paddr = new String();
        	String P2Pip = new String();
        	String P2Pport = new String();
        	Set<Map.Entry<String, String>> allSet=userlist.entrySet();
        	Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        	while(iter.hasNext()){
        		Map.Entry<String, String> me=iter.next();
        		String temp = me.getKey();
        		if (uname.equals(temp)){
        			 P2Paddr = me.getValue();
        			 break;
        		}
 	        }
        	
        	String []P2PaddrSp = P2Paddr.split(" ");
        	P2Pip = P2PaddrSp[0];
        	P2Psocket = new Socket(P2Pip,6789); 
        	outToP2PServer = new DataOutputStream(P2Psocket.getOutputStream());
        	inFromP2PServer = new BufferedReader(new InputStreamReader(P2Psocket.getInputStream()));
        	
        	HelloMINET helloP2P = new HelloMINET(P2Pip);
        	String helloStr = helloP2P.getContent();
        	
        	outToP2PServer.writeBytes(helloStr + '\n');
        	System.out.println(helloStr);
        	String fromP2PServer = new String();
        	while (true){
        		fromP2PServer = inFromP2PServer.readLine();
        		if (fromP2PServer != null){
        			String checkHello = "MIRO" + " " + P2Pip;
        			System.out.println(checkHello);
        			if(fromP2PServer.equals(checkHello)){
        				chating_user_list.put(uname, P2Psocket);
                        return true;
        			}else 
        				return false;
        		}
        	}
        }
        
        public void sendMessage(String mail) throws IOException{
        	Date Current_Date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String DateStr = formatter.format(Current_Date);
            P2PMessage header = new P2PMessage(username);
            String mess = header.getContent();
            int content_length = mail.length();
            mess += "Date" + " " + DateStr + "Content-Length" + " " + content_length + "\r\n";
            mess += "Content-Type" + " " + "text/html" + " " + "charset" + " " + "ISO-8859-1" + "\r\n";
            mess += "\r\n" + mail; 
            try{
            	outToP2PServer = new DataOutputStream(P2Psocket.getOutputStream());
            	outToP2PServer.writeBytes(mess + '\n');
            }catch (IOException e){
            	e.printStackTrace();
            }
        }
        public void process(final Socket connectionSocket) throws IOException{
            new Thread(new Runnable(){
                    public void run(){
                    	boolean flag_ = true;
                    	try{
                    //		timer.purge();
                    		BufferedReader inFromP2P = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    		DataOutputStream outToP2P = new DataOutputStream(connectionSocket.getOutputStream());
                    		while (flag_ == true){
                    			String Sentence;
                    			StringBuilder temp = new StringBuilder();
                    			int ch;
                                boolean flag = false;
                                int pre = '\0';
                                while(0 <= (ch = inFromP2P.read())) {
                                    
                                    if (ch == '\n' && pre == '\n')
                                        break;
                                    temp.append((char)ch);
                                    pre = ch;
                                }
                                Sentence = temp.toString();
                                System.out.println(Sentence);
                                
                                int state = action(Sentence);
                                String Status = "";
                                switch (state){
                                case 1:
                                	Status = handshake(Sentence,connectionSocket);
                                    outToP2P.writeBytes(Status + '\n');
                                    break;
//                                case 2:
//                                	Status = leave_chating(clientSentence);
//                                    update_chating(Status);
//                                    flag = false;
//                                    break;
                                case 3:
                                	P2Pmessage(Sentence,connectionSocket);
                                	break;
//                                case 4:
//                                	keepBeat(Sentence);
//                                	break;
                                }
                    		}
                    	}catch(IOException e){
                    		e.printStackTrace();
                    	}
                    }
            }).start();
        }
        
        /**
         *deal with deciding which action to take according to the message
         *1---handshake
         *2---leave_chating
         *3---P2P_chating
         *4---Keep_Beat
         */
        private static int action(String message){
            String []options = message.split(" ");
            if (options[0].equals("MINET"))
                return 1;
            else{
                if (options[1].equals("LEAVE"))
                    return 2;
                else if (options[1].equals("P2PMESSAGE"))
                    return 3;
                else if (options[1].equals("BEAT"))
                    return 4;

            }
            return -1; 
        }
        
        /**
    	*used to handshake, just to send handshake message
    	*/
        private String handshake(String sentence, Socket connectionSocket){
        	String Status = "";

            Status = sentence.replace("MINET", "MIRO");
            
            String clientIP = connectionSocket.getInetAddress().getHostAddress();

        	String P2Pname = new String();
        	Set<Map.Entry<String, String>> allSet=userlist.entrySet();
        	Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        	while(iter.hasNext()){
        		Map.Entry<String, String> me=iter.next();
        		String temp = me.getValue();
        		String []P2PaddrSp = temp.split(" ");
        		if (clientIP.equals(P2PaddrSp[0])){
        			 P2Pname = me.getKey();
        			 break;
        		}
 	        }
        	chating_user_list.put(P2Pname, connectionSocket);
        	
            return Status;
        }
        
        private void P2Pmessage(String sentence, Socket connectionSocket) throws IOException{
        	String clientIP = connectionSocket.getInetAddress().getHostAddress();
        	Set<Map.Entry<String, Socket>> allSet=chating_user_list.entrySet();
        	Iterator<Map.Entry<String, Socket>> iter=allSet.iterator();
        	String P2Pname = new String();
        	while(iter.hasNext()){
        		Map.Entry<String, Socket> me=iter.next();
        		Socket temp = me.getValue();
        		String tempIP = temp.getInetAddress().getHostAddress();
        		if (clientIP.equals(tempIP)){
        			 P2Pname = me.getKey();
        			 break;
        		}
 	        }
        	
        	String []options = sentence.split("\r\n");
        	String []content_info = options[1].split(" ");
        	String length_temp = content_info[3];
        	int length = Integer.parseInt(length_temp);
            
        	System.out.println(P2Pname + ": " + options[3]);
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
//                                while(true){
//                              	  client.fromServer = client.inFromServer.readLine();
//                              	  System.out.println(client.fromServer);
//                                 }
                                client.serverListen();
                                client.heartBeat();
                                client.P2Plistener();
                                System.out.println("Please input the username you want to chat with:");
                                String P2Pname = in.next();
                                if (client.hello_P2P(P2Pname)){
                                	System.out.println("P2P connects successfully!");
                                	String mail = in.next();
                                	client.sendMessage(mail);
                                }
                                else
                                	System.out.println("P2P connection error!");
                        }else{
                                System.out.println("Login error!");
                        }
                }
                else{
                        System.out.println("connection error!");
                }
                
                
                
                
        }
}
