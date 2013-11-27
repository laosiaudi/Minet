import java.net.*;
import java.io.*;

public class Server{
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