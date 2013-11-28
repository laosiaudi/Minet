import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerTest{
	
    public static void main(String argv[]) throws Exception{
		
		String clientSentence;

		ServerSocket welcomeSocket = new ServerSocket(6789);
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println(clientSentence);
			//outToClient.writeBytes(clientSentence);

		}
	}
}
