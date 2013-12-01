import java.io.*;
import java.net.*;

class ClientTest{
	public static void main(String argv[])throws Exception{
		String sentence;
		String modifiedSentence;
		while (true){
			/*BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));*/
			//Socket clientSocket = new Socket("127.0.0.1",6788);
			//DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			//BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
			//sentence = inFromUser.readLine();
			//sentence = "minet lll \ndddd dddd\n";
            //outToServer.writeBytes(sentence + '\n');
			//modifiedSentence = inFromServer.readLine();
			//System.out.println("From Server: " + modifiedSentence);
		    ///clientSocket.close();
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket("127.0.0.1",6788);
            
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            String Test = inFromUser.readLine();

            outToServer.writeBytes("yyy\n\rhhhh\n\r" + '\n');

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String Result = inFromServer.readLine();
            System.out.println(Result);
		}
	}
}
