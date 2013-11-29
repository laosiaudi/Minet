import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerTest{
	private void process(final Socket connectionSocket) throws IOException{
    	new Thread(new Runnable(){
            public void run(){
                try{
					while(true){
						BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			            //String temp = inFromClient.readLine();
			            /*String temp;
			            while ((temp = inFromClient.readLine()) != null && temp.length() != 0){
			                System.out.println(temp);
			                clientSentence += temp;
			                //temp = inFromClient.readLine();
			            }
			            inFromClient.close();
						//clientSentence = inFromClient.readLine();
			            String []options = clientSentence.split(" ");
						System.out.println(clientSentence);
			            System.out.println("pro is " + options[0]);
						//outToClient.writeBytes(clientSentence);*/
						String clientSentence = "";
						String temp;
						while ((temp = inFromClient.readLine()) != null && temp.length() != 0){
			                System.out.println(temp);
			                clientSentence += temp;
			                //temp = inFromClient.readLine();
			            }
			            
			            //inFromClient.close();
		        	}
		            
				}catch(IOException e){
                    e.printStackTrace();
                }finally{

                }

			}	
		}).start();

	}
	public static void main(String argv[]) throws Exception{
		String clientSentence = "";
        ServerTest server = new ServerTest();
		ServerSocket welcomeSocket = new ServerSocket(6788);
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
            server.process(connectionSocket);
        }
	}
		
}
