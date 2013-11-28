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
	private void user_log_in(){

	}

	/**
	*return message as status of logging in to user
	*/
	private String status(){

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
	private void keeo_beat(){

	}

    /**
     *deal with deciding which action to take according to the message
     */
	public int action(String message){

    }
    public static void main(String argv[]) throws Exception{
		
		String clientSentence;

		ServerSocket welcomeSocket = new ServerSocket(6789);
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			outToClient.writeBytes(clientSentence);

		}
	}
}
