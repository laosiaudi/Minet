import java.net.*;

import java.io.*;
import java.util.ArrayList;

public class ServerTest{
	private void process(final Socket connectionSocket) throws IOException{
    	new Thread(new Runnable(){
            public void run(){
                try{
					while(true){
						//BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						BufferedInputStream inFromClient = new BufferedInputStream(connectionSocket.getInputStream());
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
						StringBuilder temp = new StringBuilder();
                        String ss;
                        int ch;
                        boolean flag = false;
			            while (0 <= (ch = inFromClient.read())){
                            if (ch == '\r')
                                flag = true;
                            if (ch == '\n' && flag == true)
                                break;
                            temp.append((char)ch);
                              //for (int i = 0;i < temp.length();i ++)
                              //  System.out.println(temp.charAt(i));
                        }
                        ss = temp.toString();  
                        String options[] = ss.split("[\\n\\r]+");
                        System.out.println("dfdfd");
                        System.out.println(options[0]);
                        System.out.println(options[1]);
                        clientSentence += temp;
			            //temp = inFromClient.readLine();
			             
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
