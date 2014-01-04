package gui;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

import gui.ClientProtocol.*;

public class Client{
        String toScreen;
        String toServer;
        String fromServer;
        String fromUser;
        Socket clientSocket;
        Socket P2Psocket;
        ServerSocket welcomeSocket;
        DataOutputStream outToServer;
        DataOutputStream outToP2PServer;
        DataInputStream inFromServer;
        DataInputStream inFromUser;
        DataInputStream inFromP2PServer;
        String localIP;
//      String ServerIP = "23.98.34.182";
//      int ServerPort = 8000;
        String ServerIP = "127.0.0.1";
        int ServerPort = 6788;
        List onlineList = new LinkedList();
        static public String username = new String();
        static public boolean connecting = false;
        static public int localP2PPort;
        static Map<String, String> userlist = new HashMap<String, String>();//key:user_name value:ip port
        Timer timer = new Timer();
        static Map<String, Socket> chating_user_list = new HashMap<String, Socket>();
        static Map<String, String> beat_time = new HashMap<String, String>();
        Socket connectionSocket;
        private FileInputStream fis;
        private DataOutputStream dos;
        private DataInputStream dis;
        private FileOutputStream fos;
        private Socket fileSendSocket;
        Timer P2Ptimer = new Timer();

        /*connect and send hello*/
        public boolean hello() throws Exception{
                System.out.println(ServerIP);
                clientSocket = new Socket(ServerIP,ServerPort);
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new DataInputStream(clientSocket.getInputStream());
                
                InetAddress addr = InetAddress.getLocalHost();
                localIP = addr.getHostAddress().toString();
                
                HelloMINET helloMINET = new HelloMINET(ServerIP + ":" + ServerPort);
                toServer = helloMINET.getContent();
                welcomeSocket = new ServerSocket(0);
                localP2PPort = welcomeSocket.getLocalPort();
                outToServer.writeUTF(toServer + "\n");
                outToServer.flush();
                while (true)
                {
                        fromServer = inFromServer.readUTF();
                        if(fromServer != null && fromServer.length()>=0){
                                String checkHello = "MIRO" + " " + ServerIP + ":" + ServerPort;
                                fromServer = fromServer.split("\r\n")[0];
                                if(fromServer.equals(checkHello))
                                        return true;
                                else return false;
                        }
                        
                }
        }
        
        /*login function*/
        public boolean login(String username) throws Exception{
                if (connecting){
                        try{
                                Login loginProtocol = new Login(username,localP2PPort);
                        
                                toServer = loginProtocol.getContent();
                                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                                
                                inFromServer = new DataInputStream(clientSocket.getInputStream());
                                
                                outToServer.writeUTF(toServer + "\n");
                                outToServer.flush();
                                StringBuilder temp = new StringBuilder();
                             
                                while((fromServer = inFromServer.readUTF())==null && fromServer.length()<=0) {
				                }
                                
                                System.out.println(fromServer);
                                
                                if (fromServer != null){
                                    String []options = fromServer.split("\r\n");
                                        String []ifsuccess = options[0].split(" ");
                                        if (ifsuccess[2].equals("1"))
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
        	userlist.clear();
            if(fromServer.indexOf("\r\n\r\n\r\n\r\n")!=-1){
            	System.out.println("nouser--------------");
            	return ;
            }
        	String []data = fromServer.split("\r\n\r\n");
        	System.out.println("++++data0"+data[0]);
            String []userLine = data[1].split("\r\n");
            for(String users : userLine){
            	System.out.println(users+"--------");
            	if(!users.equals("\n")){
                String []user = users.split(" ");
                userlist.put(user[0],user[1]+" "+user[2]);
            	}
            }
            
        	Set<Map.Entry<String, String>> allSet=userlist.entrySet();
        	Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        	System.out.println("-------This is the user list:---------");
	        while(iter.hasNext()){
	            Map.Entry<String, String> me=iter.next();
	             System.out.println(me.getKey()+ " "+me.getValue());
	        }
            System.out.println("--------user list end----------");

        }
        
        /*process the UPDATE protocol from the second line to the end, and change the online friend list*/
        public void updateOnline() throws IOException{
        	System.out.println(fromServer);
            String []options = fromServer.split("\r\n");
            options = options[0].split(" ");
            String status = options[2];
            String updateUserName = options[3];
            String []updateUserInfo = options[4].split(",");
            if (status.equals("1")){
                    userlist.put(updateUserName, updateUserInfo[0]+" "+updateUserInfo[1]);
            }
            else if (status.equals("0")){
                    userlist.remove(updateUserName);
            }
            Set<Map.Entry<String, String>> allSet=userlist.entrySet();
            Iterator<Map.Entry<String, String>> iter=allSet.iterator();
            System.out.println("-------This is the user list:---------");
            while(iter.hasNext()){
                Map.Entry<String, String> me=iter.next();
                 System.out.println(me.getKey()+ " "+me.getValue());
            }
            System.out.println("--------user list end----------");
        }
        
        /*process the CSMESSAGE protocol from the second line to the end, and change the online friend list*/
        public void csMessage() throws IOException{
        	String []options = fromServer.split("\r\n");
            
        	String fromUserName = options[0].split(" ")[2];
            int i = 0;
        	for(String item : options){
                if (item.length()==0)
                    break;
                i+=1;
            }
            System.out.println(fromUserName + "send to all: \"" + options[i+1] +"\"\n");
        }
        
        public void P2PsendFile(String uname,final String FilePath) throws Exception{
            Set<Map.Entry<String, Socket>> allSet=chating_user_list.entrySet();
            Iterator<Map.Entry<String, Socket>> iter=allSet.iterator();
            Socket goalSocket = null;
            while(iter.hasNext()){
                Map.Entry<String, Socket> me=iter.next();
                String temp = me.getKey();
                if (uname.equals(temp)){
                    goalSocket = me.getValue();
                     break;
                }
            }
            try{
                dos = new DataOutputStream(goalSocket.getOutputStream());
                final ServerSocket FileSocket = new ServerSocket(0);
                int fileSendPort = FileSocket.getLocalPort();
                P2PFile P2PFileProtocol = new P2PFile(username, fileSendPort);
                dos.writeUTF(P2PFileProtocol.getContent()+'\n');
                dos.flush();
                
                new Thread(new Runnable(){
                    public void run(){
                    	try{
                    		fileSendSocket = FileSocket.accept();
                            sendingFile(fileSendSocket, FilePath);
                    	}catch(Exception e){
                            e.printStackTrace();
                        }
                    	
                    }
                }).start();
                    
            }catch (IOException e){
                e.printStackTrace();
            }
            
        }
        
        private void sendingFile(Socket socket, String FilePath){
            try{
                try{
                    File file = new File(FilePath);
                    fis = new FileInputStream(file);
                    dos = new DataOutputStream(socket.getOutputStream());

                    dos.writeUTF(file.getName());
                    dos.flush();
                    dos.writeLong(file.length());
                    dos.flush();

                    byte[] sendBytes = new byte[1024];
                    int length = 0;
                    while((length = fis.read(sendBytes,0,sendBytes.length))>0){
                        dos.write(sendBytes,0,length);
                        dos.flush();
                    }
                    
                }catch (Exception e){
                    e.printStackTrace();
                   
                }finally{
                    if(fis!=null)
                        fis.close();
                    if(dos!=null)
                        dos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                
            }
            
        }
        
        
        public void P2PrecFile(Socket socket, String sentence, String savePath) throws Exception{
            String []options = sentence.split("\r\n");
            String []sendInfos = options[3].split(" ");
            String sendFilePorts = sendInfos[1];
            String sendFileIP = socket.getInetAddress().getHostAddress();
            int sendFilePort = Integer.parseInt(sendFilePorts);
            Socket fileRecSocket = new Socket(sendFileIP, sendFilePort);
            receivingFile(fileRecSocket, savePath);        
        }

        private void receivingFile(final Socket socket, final String savePath){
            new Thread(new Runnable(){
                public void run(){
                    try{
                        try{
                            dis = new DataInputStream(socket.getInputStream());
                            String fileName = dis.readUTF();
                            long fileLength = dis.readLong();
                            fos = new FileOutputStream(new File(savePath+fileName));

                            byte[] sendBytes = new byte[1024];
                            int transLen = 0;
                            System.out.println("-----start receiving------");
                            while (true){
                                int read = 0;
                                read = dis.read(sendBytes);
                                if(read == -1)
                                    break;
                                transLen += read;
                                System.out.println("received" + 100*transLen/fileLength+"%...");
                                fos.write(sendBytes,0,read);
                                fos.flush();
                            }
                            System.out.println("-------finished------");
                            socket.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        /*user Leave and exit the system*/
        public void userLeave() throws IOException{
        	Leave leaveProtocol = new Leave(username);
        	outToServer.writeUTF(leaveProtocol.getContent() + "\n");
        	outToServer.flush();
        	System.exit(0);
        }
        
        /**
         * @param message the message that user send to all user through server
         * 
         * */
        public void sendToAll(String message) throws IOException{
        	SendToAll sendToAllProtocol = new SendToAll(username, message);
        	outToServer.writeUTF(sendToAllProtocol.getContent() + "\n");
        	outToServer.flush();
        }
        
        /*send GETLIST protocol to get the all online list*/
        public void getOnlineList() throws IOException{
        	GetList getListProtocol = new GetList();
        	outToServer.writeUTF(getListProtocol.getContent() + "\n");
        	outToServer.flush();
        }
        
        
        /*listen the server port*/
        private void serverListen() throws IOException{
        	new Thread(new Runnable(){
        		public void run(){
        			try{
        				while(true){
        					while((fromServer = inFromServer.readUTF())!=null && fromServer.length()>0)
            				{
        						System.out.println(fromServer);
            					String []options = fromServer.split("\r\n");
                                options = options[0].split(" ");
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
            /*say hello */
    public boolean hello_p2p(String uname) throws IOException{
        String P2PInfo = "";
        Set<Map.Entry<String, String>> allSet=userlist.entrySet();
        Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> me=iter.next();
            String temp = me.getKey();
            System.out.println(temp+me.getValue());
            if (uname.equals(temp)){
                 P2PInfo = me.getValue();
                 break;
            }
        }
        if (P2PInfo.equals("")){
            System.out.println("no this user.");
            return false;
        }
        String []P2PInfos = P2PInfo.split(" ");
        String P2PIp = P2PInfos[0];
        String P2PPortS = P2PInfos[1];
        int P2PPort = Integer.parseInt(P2PPortS);
        System.out.println(P2PIp+P2PPort);
        P2Psocket = new Socket(P2PIp,P2PPort);
        outToP2PServer = new DataOutputStream(P2Psocket.getOutputStream());
        inFromP2PServer = new DataInputStream(P2Psocket.getInputStream());

        HelloMINET helloP2P = new HelloMINET(P2PIp + " " + localP2PPort);
        String helloStr = helloP2P.getContent();
        outToP2PServer.writeUTF(helloStr + "\n");
        outToP2PServer.flush();

        System.out.println(helloStr);
            String fromP2PServer = new String();
            while (true){
                fromP2PServer = inFromP2PServer.readUTF();
                System.out.println("fromServer"+fromP2PServer);
                // System.out.println(fromP2PServer.size());
                if (fromP2PServer != null){
                    String checkHello = "MIRO" + " " + P2PIp + " " + localP2PPort;
                    System.out.println("check " + checkHello);
                    if(fromP2PServer.split("\r\n\n\n")[0].equals(checkHello)){
                        chating_user_list.put(uname, P2Psocket);
                        //process(P2Psocket);
                        heartBeat(P2Psocket);
                        return true;
                    }else 
                        return false;
                }
            }
    }
    
    /*send P2P message*/
    public void sendMessage(String mail, String uname) throws IOException{
        Date Current_Date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateStr = formatter.format(Current_Date);
        P2PMessage header = new P2PMessage(username);
        String mess = header.getContent();
        int content_length = mail.length();
        mess += "Date" + " " + DateStr + "Content-Length" + " " + content_length + "\r\n";
        mess += "Content-Type" + " " + "text/html" + " " + "charset" + " " + "ISO-8859-1" + "\r\n";
        mess += "\r\n" + mail + "\r\n"; 

        String P2PInfo = "";
        Set<Map.Entry<String, Socket>> allSet=chating_user_list.entrySet();
        Iterator<Map.Entry<String, Socket>> iter=allSet.iterator();
        Socket goalSocket = null;
        while(iter.hasNext()){
            Map.Entry<String, Socket> me=iter.next();
            String temp = me.getKey();
            if (uname.equals(temp)){
                goalSocket = me.getValue();
                 break;
            }
        //     while(iter.hasNext()){
        //     Map.Entry<String, Socket> me=iter.next();
        //     Socket temp = me.getValue();
        //     String tempIP = temp.getInetAddress().getHostAddress();
        //     String tempName = me.getKey();
        //     int tempPort = temp.getLocalPort();
        //     if (clientIP.equals(tempIP) && clientName.equals(tempName)){
        //          P2Pname = tempName;
        //          break;
        //     }
        // }
        }
        try{
        	
            DataOutputStream outTo = new DataOutputStream(goalSocket.getOutputStream());
            System.out.println("sending messing" + mess);
            outTo.writeUTF(mess + "\n");
            outTo.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    /*send P2P heartBeat*/
    public void heartBeat(Socket goalSocket) throws IOException{
        timer = new Timer(true);
            HeartBeat heartBeatProtocol = new HeartBeat(username);
            final String sendBeat = heartBeatProtocol.getContent();
            final DataOutputStream outTo = new DataOutputStream(goalSocket.getOutputStream());
            timer.schedule(
            new TimerTask() { 
                public void run(){ 
                    try {
                        outTo.writeUTF(sendBeat + "\n");
                        outTo.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } }, 0, 9*1000);
    }
    
    /*leave P2P chat*/
    public void leave_p2p(String uname) throws IOException{
        Set<Map.Entry<String, Socket>> allSet=chating_user_list.entrySet();
        Iterator<Map.Entry<String, Socket>> iter=allSet.iterator();
        Socket goalSocket = null;
        while(iter.hasNext()){
            Map.Entry<String, Socket> me=iter.next();
            String temp = me.getKey();
            if (uname.equals(temp)){
                goalSocket = me.getValue();
                iter.remove();
                 break;
            }
        }
        P2PLeave P2PLeaveProtocol = new P2PLeave(username);
        DataOutputStream outTo = new DataOutputStream(goalSocket.getOutputStream());
        outTo.writeUTF(P2PLeaveProtocol.getContent()+'\n');
        outTo.flush();
        goalSocket.close();
    }

    /*listen P2P socket*/
   public void process(final Socket connectionSocket) throws IOException{
        new Thread(new Runnable(){
            public void run(){
                boolean flag = true;
                
                try{
                    

                    timer.purge();
                    DataInputStream inFromP2P = new DataInputStream(connectionSocket.getInputStream());
                    DataOutputStream outToP2P = new DataOutputStream(connectionSocket.getOutputStream());
                    while (flag){
                        String sentence;
                        while ((sentence = inFromP2P.readUTF())==null & sentence.length()<=0){
                        }
                        
                        int state = action(sentence);
                        String status = "";
                        switch(state){
                        case 1:
                            status = handshake(sentence,connectionSocket);
                            outToP2P.writeUTF(status + '\n');
                            outToP2P.flush();
                            heartBeat(connectionSocket);
                            break;
                        case 2:
                            leave_P2P_chating(sentence);
                            break;
                        case 3:
                            System.out.println(sentence);
                            P2Pmessage(sentence,connectionSocket);
                            break;
                        case 4:
                            keepP2PBeat(sentence);
                            break;
                        case 5:
                            try{
                                P2PrecFile(connectionSocket,sentence, "/Users/apple/Downloads/");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /*P2P action*/
    public static int action(String message){
        String []options = message.split(" ");
        if (options[0].equals("MINET"))
            return 1;
        else{
            if (options.length <= 1) {
                return 0;
            }
            if (options[1].equals("Leave"))
                return 2;
            else if (options[1].equals("P2PMESSAGE"))
                return 3;
            else if (options[1].equals("BEAT"))
                return 4;
            else if (options[1].equals("P2PFile"))
                return 5;
        }
        return 0;
    }
    
    /*handle hello from P2P*/
    public String handshake(String sentence,Socket connectionSocket){
        String status = "";
        String host_info_port = new String();
        status = sentence.replace("MINET", "MIRO");
        
        String []host_info = sentence.split(" ");
        String clientIP = connectionSocket.getInetAddress().getHostAddress();
        host_info_port = host_info[2];
        
        String P2Pname = new String();
        Set<Map.Entry<String, String>> allSet=userlist.entrySet();
        Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> me=iter.next();
            String temp = me.getValue();
            String []P2PaddrSp = temp.split(" ");
            if (clientIP.equals(P2PaddrSp[0]) && host_info_port.split("\r\n\n")[0].equals(P2PaddrSp[1])){
                 P2Pname = me.getKey();
                 break;
            }
        }
        chating_user_list.put(P2Pname, connectionSocket);
        beat_time.put(P2Pname,"NO");
        P2Ptimer.schedule(new Check_Beat(P2Pname),10000,10000);
        
        return status;
    }
    
    /*send P2Pmessage*/
    public String P2Pmessage(String sentence,Socket connectionSocket) throws IOException{
        String clientIP = connectionSocket.getInetAddress().getHostAddress();
        
        Set<Map.Entry<String, Socket>> allSet=chating_user_list.entrySet();
        Iterator<Map.Entry<String, Socket>> iter=allSet.iterator();
        String P2Pname = new String();
        String []options = sentence.split("\r\n");
        String []host_info = options[0].split(" ");
        String clientName = host_info[2];
        System.out.println(clientName);
        while(iter.hasNext()){
            Map.Entry<String, Socket> me=iter.next();
            Socket temp = me.getValue();
            String tempIP = temp.getInetAddress().getHostAddress();
            String tempName = me.getKey();
            int tempPort = temp.getLocalPort();
            if (clientIP.equals(tempIP) && clientName.equals(tempName)){
                 P2Pname = tempName;
                 break;
            }
        }
        if (P2Pname == null){
            System.out.println("Wrong Username!");
            return "wrong";
        }
        else{
            String []content_info = options[1].split(" ");
            String length_temp = content_info[3];
            int length = Integer.parseInt(length_temp);
            System.out.println(P2Pname + ": " + options[4]);
            return P2Pname + ": " +"\n"+ options[4]+"\n";
        }
    }
    
    /*handle p2p heartBeat*/
    public void keepP2PBeat(String sentence) throws IOException{
        String []option = sentence.split(" ");
        String use_name = option[2].split("[\\n\\r]+")[0];
        
        beat_time.put(use_name,"YES");
    }
    /*p2p server listen*/
    public void P2Plistener() throws IOException{
        new Thread(new Runnable(){
            public void run(){
                try{
                    
                    
                    while (connecting){
                        connectionSocket = welcomeSocket.accept();
                        process(connectionSocket);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*handle P2P leave*/
    private void leave_P2P_chating(String sentence) throws IOException{
        String []options = sentence.split("\r\n");
        options = options[0].split(" ");
        String leaveUser = options[2];
        Set<Map.Entry<String, Socket>> allSet=chating_user_list.entrySet();
        Iterator<Map.Entry<String, Socket>> iter=allSet.iterator();
        Socket goalSocket = null;
        while(iter.hasNext()){
            Map.Entry<String, Socket> me=iter.next();
            String temp = me.getKey();
            if (leaveUser.equals(temp)){
                goalSocket = me.getValue();
                iter.remove();
                 break;
            }
        }
        goalSocket.close();
    } 
    
    class Check_Beat extends TimerTask{
        private String User_Name;
        public Check_Beat(String User_Name){
            this.User_Name = User_Name;
        }
        
        @Override
        public void run(){
            if (beat_time.get(User_Name) == null)
                this.cancel();
            else if (beat_time.get(User_Name).equals("NO")) {
                chating_user_list.remove(User_Name);
                beat_time.remove(User_Name);
                this.cancel();
            }
            else{
                beat_time.put(User_Name,"NO");
            }
        }
    }
    public static void main(String argv[]) throws Exception{
            Client client = new Client();
            connecting = client.hello();
            if(connecting){
                    System.out.println("connection success!");
                    System.out.print("Please login your username: ");
                    /*Before login, find a free port to listen p2p, and when login send the port to server*/
                    client.P2Plistener();
                    Scanner in = new Scanner(System.in);
                    username = in.next();
                    if (client.login(username)){
                            System.out.println("Login success!");
//                                while(true){
//                              	  client.fromServer = client.inFromServer.readLine();
//                              	  System.out.println(client.fromServer);
//                                 }
                            client.serverListen();
                            client.heartBeat(client.clientSocket);
                            client.sendToAll("hello");
                            System.out.println("Please input the username you want to chat with:");
                          
                            // String x = in.next();
                            // if (x.equals("e")){
                            //     client.userLeave();
                            // }
                            String P2Pname = in.next();
                            if (client.hello_p2p(P2Pname)){
                            	System.out.println("P2P connects successfully!");
                            	String mail = in.next();
                            	client.sendMessage(mail, P2Pname);
                            }
                            else
                            	System.out.println("P2P connection error!");
                    }
                    else{
                            System.out.println("Login error!");
                    }
            }
            else{
                    System.out.println("connection error!");
            }
    }
}
