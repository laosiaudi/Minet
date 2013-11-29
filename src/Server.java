import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server{
	ArrayList<String>userlist = new ArrayList<String>();
	/**
	*used to handshake, just to send handshake message
	*/
	private void handshake(){

	}

	/**
	*used to deal with user log in
	*/
	private void user_log_in(BufferedReader inFromClient){

	}

	/**
	*return message as status of logging in to user
	*/
	private String status(){
        return "";
	}

	/**
	*deal with send online userlist to user
	*/
	private void send_list(){

	}

	/**
	*send new_user_status to other users
	*/
	private void update_user_list(){

	}

	/**
	*deal with user log out
	*/
	private void user_log_out(){

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
        if (options[0].equals("Minet"))
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
                    switch (state) {
                        case 1:
                            handshake();
                            break;
                        case 2:
                            user_log_in(inFromClient);
                            break;
                        case 3:
                            send_list();
                            break;
                        case 4:
                            user_log_out();
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
		ServerSocket welcomeSocket = new ServerSocket(6788);
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
            server.process(connectionSocket);
        }
    }
}
