import java.net.*;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Server{
    Map<String, String> userlist = new HashMap<String, String>();//key:user_name value:ip port
    Map<String, Socket> online_user_list = new HashMap<String, String>();//key:user_name value:socket
    //String Status = "";
	//ArrayList<String>userlist = new ArrayList<String>();
	/**
	*used to handshake, just to send handshake message
	*/
	private String handshake(String clientSentence){
        String Status = "";

        Status = clientSentence.replace("MINET", "MIRO");
        return Status;

	}

	/**
	*used to deal with user log in
	*/
	private String user_log_in(String inFromClient, Socket connectionSocket){ //debugged
        String Status = "";
        String []options = inFromClient.split(" ");
        
        String User_Name = options[2].split("\n\r")[0];

        String Port_Num = options[3].split("\n\r")[0];
        String IP_Num = connectionSocket.getInetAddress().getHostAddress();

        String User_Info = IP_Num + "," + Port_Num;
        //System.out.println(User_Info);
        Status = "";
        if (userlist.get(User_Name) != null) {
        	  String Request_Line = "CS1.0" + " " + "STATUS" + " " + "0" + "\n\r";
              

              String Message = "The user name already exists";
              String Entity_Body = Message + "\n\r";

              /**
               * Set the time format
               */
              SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
              Date Current_Date = new Date(System.currentTimeMillis());
              String DateStr = formatter.format(Current_Date);
              /** The size of entity body
               */
              String Content_Length = String.valueOf(Entity_Body.getBytes().length);
       
              String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + Content_Length + "\n\r";

              Status += Request_Line + Header_Line + "\n\r" + Entity_Body;
              return Status;
        }
           // return false;
        else {
            userlist.put(User_Name, User_Info);
            online_user_list.put(User_Name, connectionSocket);
            String Request_Line = "CS1.0" + " " + "STATUS" + " " + "1" + "\n\r";

            /**
             * Set the time format
             */

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date Current_Date = new Date(System.currentTimeMillis());
            String DateStr = formatter.format(Current_Date);
            String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + "0" + "\n\r";
            System.out.println(DateStr);


            /**
             * If the user logs in successfully, then the entity body is set to be empty
             */
            String Entity_Body = "\n\r";

            Status = Request_Line + Header_Line + "\n\r" + Entity_Body;
            return Status;

            
        }
            
        System.out.println(Status);

	}

	
	/**
	*deal with send online userlist to user
	*/
	private String send_list(){
        String Status  = "";
        String Request_Line = "CS1.0" + " " + "LIST" + "\n\r";

        String Entity_Body = "";
        /**
         * Traverse the user list
         */
        for (String key : userlist.keySet()) {
            String value = userlist.get(key);
            String []temp = value.split(",");

            Entity_Body += key + " " + temp[0] + " " + temp[1] + "\n\r";
        }

        Date Current_Date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateStr = formatter.format(Current_Date);

        String Content_Length = String.valueOf(Entity_Body.getBytes().length);
       // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String Header_Line = "Date" + " " + DateStr + "Content-Length" + Content_Length + "\n\r";
        Status = Request_Line + Header_Line + "\n\r" + Entity_Body;
        return Status;

	}

	/**
	*send new_user_status to other users
	*/
	private String update_user_list(String Name, int i){
        String Status = "";

        String Request_Line = "";


        Date Current_Date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateStr = formatter.format(Current_Date);

        String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + "0" + "\n\r";

        String Entity_Body = "\n\r";
        if (i == 0) 
            Request_Line = "CS1.0" + " " + "UPDATE" + " " + "0" + " " + Name + "\n\r";
        else
            Request_Line = "CS1.0" + " " + "UPDATE" + " " + "1" + " " + Name + "\n\r";

        Status += Request_Line + Header_Line + Entity_Body;
        return Status;
    }

    /**
    *
    *deal with broadcast userlist
    */
    private void update_onlinelist(String status) {
        Iterator iter = online_user_list.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            Socket _socket = entry.getValue();
            DataOutputStream outToClient = new DataOutputStream(_socket.getOutputStream());
            outToClient.writeBytes(status + '\n');
        }
    }

	/**
	*deal with user log out
	*/
	private String user_log_out(String clientSentence){
        String []options = clientSentence.split(" ");
        String User_Name = options[2].split("\n\r")[0];
        userlist.remove(User_Name);
        online_user_list.remove(User_Name);
        return update_user_list(User_Name,0);
	}

	/**
	*deal with group talk
	*/
	private void csmessage(){

	}

	/**
	*deal with user heart-beat
	*/
	private void keep_beat(){
        
	}

    /**
    *deal with deciding which action to take according to the message
    *1---handshake
    *2---userLogin
    *3--usergetList
    *4--userleave
    *5--user_send_group_message
    *6--user_send_beat
    */
	private static int action(String message){
        String []options = message.split(" ");
        if (options[0].equals("MINET"))
            return 1;
        else{
            if (options[1].equals("LOGIN"))
                return 2;
            else if (options[1].equals("GETLIST"))
                return 3;
            else if (options[1].equals("LEAVE"))
                return 4;
            else if (options[1].equals("MESSAGE"))
                return 5;
            else if (options[1].equals("BEAT"))
                return 6;

        }
        return -1; 
    }
    private void process(final Socket connectionSocket) throws IOException{
		new Thread(new Runnable(){
            public void run(){
                try{
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                    String clientSentence = inFromClient.readLine();
                    int state = action(clientSentence);
                    String Status = "";
                    switch (state) {
                        case 1:
                            Status = handshake(clientSentence);
                            outToClient.writeBytes(Status + '\n');
                            break;
                        case 2:
                            Status = user_log_in(clientSentence, connectionSocket);
                            outToClient.writeBytes(Status + '\n');
                            break;
                        case 3:
                            Status = send_list();
                            outToClient.writeBytes(Status + '\n');
                            break;
                        case 4:
                            Status = user_log_out(clientSentence);
                            //outToClient.writeBytes(Status + '\n');
                            update_onlinelist(Status);
                            break;
                        case 5:
                           csmessage();
                           break;
                        case 6:
                           keep_beat();
                           break;

                    }
                    inFromClient.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
	}
    public static void main(String argv[]) throws Exception{
		
		String clientSentence;
        Server server = new Server();
		ServerSocket welcomeSocket = new ServerSocket(7000);
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
            server.process(connectionSocket);
        }
    }
}
