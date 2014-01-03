import java.net.*;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TimerTask;
import javax.swing.text.html.HTMLDocument.Iterator;
import java.util.Timer;
public class Server{
    static Map<String,Socket> online_user_list = new HashMap<String, Socket>();//key:user_name value:socket
    static Map<String, String> userlist = new HashMap<String, String>();//key:user_name value:ip port
    static Map<String, String> beat_time = new HashMap<String, String>();//key:user_name value:whether user is online
    boolean flag = false;
        
    Timer timer = new Timer();//timer 
    /**
        *used to handshake, just to send handshake message
        */
        private String handshake(String clientSentence){
            String Status = "";

            Status = clientSentence.replace("MINET", "MIRO");
            System.out.println(Status);
            return Status;

        }

        /**
        *used to deal with user log in
        */
        private String user_log_in(String inFromClient, Socket connectionSocket) throws IOException{  //debugged
        flag = false;
        String Status = "";
        String []options = inFromClient.split(" ");
        
        String User_Name = options[2].split("[\\r\\n]+")[0];
        
        String Port_Num = options[3].split("[\\r\\n]+")[0];
        String IP_Num = connectionSocket.getInetAddress().getHostAddress();

        String User_Info = IP_Num + "," + Port_Num;
        //System.out.println(User_Info);
        Status = "";
        if (userlist.get(User_Name) != null) {
                  String Request_Line = "CS1.0" + " " + "STATUS" + " " + "0" + "\r\n";
              
              String Message = "The user name already exists";
              String Entity_Body = Message + "\r\n";

              /**
               * Set the time format
               */
              SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
              Date Current_Date = new Date(System.currentTimeMillis());
              String DateStr = formatter.format(Current_Date);
              /** The size of entity body
               */
              String Content_Length = String.valueOf(Entity_Body.getBytes().length);
       
              String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + Content_Length + "\r\n";

              Status += Request_Line + Header_Line + "\r\n" + Entity_Body;
              flag = true;
              return Status;
        }
           // return false;
        else {
            userlist.put(User_Name, User_Info);
            
            String Request_Line = "CS1.0" + " " + "STATUS" + " " + "1" + "\r\n";

            /**
             * Set the time format
             */

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date Current_Date = new Date(System.currentTimeMillis());
            String DateStr = formatter.format(Current_Date);
            String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + "0" + "\r\n";
            //System.out.println(DateStr);


            /**
             * If the user logs in successfully, then the entity body is set to be empty
             */
            String Entity_Body = "\r\n";

            Status = Request_Line + Header_Line + "\r\n" + Entity_Body;
            beat_time.put(User_Name,"NO");
            update_onlinelist(update_user_list(User_Name, 1, Port_Num, connectionSocket));
            online_user_list.put(User_Name, connectionSocket);
            for(String key : online_user_list.keySet()) {
                System.out.println(key);
                System.out.println("---------------------------------");
            }
            timer.schedule(new Check_Beat(User_Name),1000,10000);
            System.out.println("gg"); 
            return Status;

            
        }
            
      //  System.out.println(Status);

        }

        
        /**
        *deal with send online userlist to user
        */
        private String send_list(){
            String Status  = "";
            String Request_Line = "CS1.0" + " " + "LIST" + "\r\n";

            String Entity_Body = "";
            /**
             * Traverse the user list
             */
            for (String key : userlist.keySet()) {
                String value = userlist.get(key);
                String []temp = value.split(",");

                Entity_Body += key + " " + temp[0] + " " + temp[1] + "\r\n";
            }

            Date Current_Date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String DateStr = formatter.format(Current_Date);

            String Content_Length = String.valueOf(Entity_Body.getBytes().length);
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            String Header_Line = "Date" + " " + DateStr + "Content-Length" + Content_Length + "\r\n";
            Status = Request_Line + Header_Line + "\r\n" + Entity_Body;
            return Status;

        }

        /**
        *send new_user_status to other users
        */
        private String update_user_list(String Name, int i, String Port_Num,  Socket connectionSocket){
            String Status = "";

            String Request_Line = "";


            Date Current_Date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String DateStr = formatter.format(Current_Date);
            String IP_Num;
            if (connectionSocket != null)
                IP_Num = connectionSocket.getInetAddress().getHostAddress();

            else
                IP_Num = "8888";
            String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + "0" + "\r\n";

            String Entity_Body = "\r\n";
            if (i == 0) 
                Request_Line = "CS1.0" + " " + "UPDATE" + " " + "0" + " " + Name + " " +  IP_Num + "," + Port_Num + "\r\n";
            else
                Request_Line = "CS1.0" + " " + "UPDATE" + " " + "1" + " " + Name + " "  + IP_Num + "," + Port_Num + "\r\n";

            Status += Request_Line + Header_Line + Entity_Body;
            return Status;
    }

    /**
    *
    *deal with broadcast userlist
    */
    private void update_onlinelist(String status) throws  IOException{
            
    try {
            for(String key : online_user_list.keySet()) {
            System.out.println("---------------------------------");
            Socket _socket = online_user_list.get(key);
            DataOutputStream outToClient = new DataOutputStream(_socket.getOutputStream());
            outToClient.writeBytes(status + '\n');
        }
    }catch(Exception e){
            e.printStackTrace();
        }
    }

        /**
        *deal with user log out
        */
        private String user_log_out(String clientSentence, Socket connectionSocket){
            String []options = clientSentence.split(" ");
            String User_Name = options[2].split("[\\n\\r]+")[0];
            //String Port_Num = options[3].split("[\\n\\r]+")[0];
            userlist.remove(User_Name);
            online_user_list.remove(User_Name);
            beat_time.remove(User_Name);
            return update_user_list(User_Name,0, "8888", connectionSocket);
        }

        /**
        *deal with group talk
        */
        private void csmessage(){

        }

        /**
        *deal with user heart-beat
        */
        private void keep_beat(String clientSentence){
            String []options = clientSentence.split(" ");
            String User_Name = options[2].split("[\\n\\r]+")[0];
            beat_time.put(User_Name,"YES");
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
        System.out.println(message + "-----;;;");
        String []options = message.split(" ");
        System.out.println(options.length);
        if (options[0].equals("MINET"))
            return 1;
        else{
            if (options.length < 1)
                return 0;
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
    private void csmessage(String clientSentence)throws IOException {
        String Request_Line = "";
        String Header_Line = "";
        String []options = clientSentence.split(" ");
        String User_Name = options[2].split("[\\n\\r]+")[0];
        String []s = clientSentence.split("[\\n\\r]+");

        String Entity_Body = s[1];

        Date Current_Date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateStr = formatter.format(Current_Date);
        int len = Entity_Body.length();

        Request_Line = "CS1.0" +" "+"CSMESSAGE"+" "+User_Name+" ";
        Header_Line +="Date"+" "+DateStr+" "+"Content_Length"+" "+len+" ";
        
        String status = Request_Line + Header_Line +  Entity_Body;
        System.out.println("status="+status);
        try{
            System.out.println("here!!");
            for(String key : online_user_list.keySet()) {
       
                Socket _socket = online_user_list.get(key);
                System.out.println("username ---"+key);
                DataOutputStream outToClient = new DataOutputStream(_socket.getOutputStream());
                outToClient.writeBytes(status + '\n');
            }
        }catch(Exception e){e.printStackTrace();}  


    }
    private String getUserName(String message){
    
        String []options = message.split("[\\n\\r]+")[0].split(" ");
        return options[2];
    }
    private void process(final Socket connectionSocket) throws IOException{
                new Thread(new Runnable(){
            public void run(){
                boolean flag_ = true;
                String User_Name ="";
                try{
                    timer.purge();
                    BufferedInputStream inFromClient = new BufferedInputStream(connectionSocket.getInputStream());
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    while (flag_){
                        String clientSentence;
                        StringBuilder temp = new StringBuilder();
                        int ch;
                        boolean flag = false;
                        int pre = '\0';
                        while(0 <= (ch = inFromClient.read())) {
                            
                            if (ch == '\n' && pre == '\n')
                                break;
                            temp.append((char)ch);
                            pre = ch;
                        }
                        clientSentence = temp.toString();
                        System.out.println(clientSentence);
                        int state = action(clientSentence);
                        if (state == 2)
                            User_Name = getUserName(clientSentence);
                        String Status = "";
                        switch (state) {
                            case 1:
                                Status = handshake(clientSentence);
                                outToClient.writeBytes(Status + '\n');
                                break;
                            case 2:
                                Status = user_log_in(clientSentence, connectionSocket);
                                outToClient.writeBytes(Status + '\n');
                                if (flag == false){
                                    Status = send_list();
                                    outToClient.writeBytes(Status + '\n');
                                }
                                break;
                            case 3:
                                Status = send_list();
                                outToClient.writeBytes(Status + '\n');
                                break;
                            case 4:
                                Status = user_log_out(clientSentence, connectionSocket);
                                //outToClient.writeBytes(Status + '\n');
                                update_onlinelist(Status);
                                flag_ = false;
                                break;
                            case 5:
                               csmessage(clientSentence);
                               break;
                            case 6:
                               keep_beat(clientSentence);
                               break;

                        }
                    }
                }catch(Exception e){
                        e.printStackTrace();
                        if (flag == false){
                            System.out.println("dddddd" + User_Name + "------");
                            userlist.remove(User_Name);
                            Socket failsocket = online_user_list.get(User_Name);
                            online_user_list.remove(User_Name);
                            beat_time.remove(User_Name);
                            String status = update_user_list(User_Name,0,"8888",failsocket);
                            try{
                                update_onlinelist(status);
                            }catch(Exception ee){
                                ee.printStackTrace();
                            }
                        }
                    }
                
            }
            
        }).start();
        }

    //the class is to time 10s to check whether user is online
    class Check_Beat extends TimerTask{
        private String User_Name;
        public Check_Beat(String User_Name){
            this.User_Name = User_Name;
        }
        @Override
        public void run() {
            try{
               if (beat_time.get(User_Name) == null)
                   this.cancel();
               else if (beat_time.get(User_Name).equals("NO")) {
                    userlist.remove(User_Name);
                    Socket failsocket = online_user_list.get(User_Name);
                    online_user_list.remove(User_Name);
                    beat_time.remove(User_Name);
                    String status = update_user_list(User_Name,0,"8888",failsocket);
                    try{
                        update_onlinelist(status);
                    }catch(Exception ee){
                        ee.printStackTrace();
                    }
                             
                    this.cancel();

               }
               else{
                    beat_time.put(User_Name,"NO");
               }
            }catch(Exception e){
                userlist.remove(User_Name);
                Socket failsocket = online_user_list.get(User_Name);
                online_user_list.remove(User_Name);
                beat_time.remove(User_Name);
                String status = update_user_list(User_Name,0,"8888",failsocket);
                try{
                    update_onlinelist(status);
                }catch(Exception ee){
                    ee.printStackTrace();
                }
                this.cancel();


            }
        }
        
    }
    public static void main(String argv[]) throws Exception{
                
                String clientSentence;
                Server server = new Server();
                ServerSocket welcomeSocket = new ServerSocket(6788);
                while(true){
                        Socket connectionSocket = welcomeSocket.accept();
                        server.process(connectionSocket);
                }
    }
}
