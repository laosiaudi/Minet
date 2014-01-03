package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.Font;

public class personal extends JFrame {
	static private Client clientOb;
	static private p2pchat p2p;
		
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					personal frame = new personal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	public JList list = new JList();
	private final JTextField textField = new JTextField();
	private final JTextArea textArea = new JTextArea();
	/**
	 * @wbp.nonvisual location=-31,7
	 */

	
	
	/**
	 * Create the frame.
	 */
	public personal(String user_name,final Client clientOb) {
		
		final String[] name = {};
		this.clientOb = clientOb;
	    final DefaultListModel dlm = new DefaultListModel();
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnChat = new JButton("chat");
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
						System.out.println(list.getSelectedValue());
						String name = list.getSelectedValue().toString();
						try{
							if(clientOb.hello_p2p(name)){
								System.out.println("P2P connects successfully!");
                            	
							}
						}catch(Exception ee){ee.printStackTrace();}
						
						Socket p2psocket = clientOb.chating_user_list.get(name);
						p2p = new p2pchat(clientOb,name,p2psocket); 
						
						p2p.setTitle(name);
						p2p.setVisible(true);
						
				
			
			
			}
		});
		//JList list = new JList();
		btnChat.setBounds(109, 245, 107, 25);
		contentPane.add(btnChat);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				  if(e.getKeyCode() == KeyEvent.VK_ENTER){
						try{
							String text = textField.getText();
							clientOb.sendToAll(text);
							textField.setText("");
						
							}
							catch(Exception es){}
						}
				  }
			
		});
		textField.setBounds(0, 189, 321, 55);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(217, 245, 54, -1);
		contentPane.add(btnNewButton);
		String textArea_text = ""; 
		JButton btnEnter = new JButton("enter");
		btnEnter.setFont(new Font("Monaco", btnEnter.getFont().getStyle(), 13));
		btnEnter.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				try{
				String text = textField.getText();
				clientOb.sendToAll(text);
				textField.setText("");
				}
				catch(Exception es){}
			}
		});
		btnEnter.setBounds(210, 245, 107, 25);
			contentPane.add(btnEnter);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 0, 321, 177);
			contentPane.add(scrollPane);
			scrollPane.setViewportView(textArea);
			new Thread(new Runnable(){
	            public void run(){
	                try{
	                    //ServerSocket welcomeSocket;
	                    Socket connectionSocket = new Socket();
	                    //welcomeSocket = new ServerSocket(0);
	                    //clientOb.localP2PPort = welcomeSocket.getLocalPort();
	                    System.out.println(clientOb.localP2PPort + "-.-.-.-.");
	                    while (clientOb.connecting){
	                        connectionSocket = clientOb.welcomeSocket.accept();
	                        //process(clientOb.connectionSocket);
	                        DataInputStream inFromP2P = new DataInputStream(connectionSocket.getInputStream());
	                        DataOutputStream outToP2P = new DataOutputStream(connectionSocket.getOutputStream());
	                        String sentence;
	                        while ((sentence = inFromP2P.readUTF())==null & sentence.length()<=0){
                        	}
	                         System.out.println(sentence);
	                         int state = clientOb.action(sentence);
	                         String status = "";
	                         switch(state){
	                             case 1:
	                            	System.out.println(sentence);
	                            	status = clientOb.handshake(sentence,connectionSocket);
	                            	System.out.println(status);
	                            	outToP2P.writeUTF(status + '\n');
	                            	clientOb.heartBeat(connectionSocket);
	                            	break;
	                            default:
	                            	break;
	                         }
	                            		
	                            	
	                        String p2pname = "";
	                        p2pchat newp2p = new p2pchat(clientOb,p2pname,connectionSocket); 
	                        newp2p.setTitle(p2pname);
							newp2p.setVisible(true);
	                        
	                    }
	                }catch(IOException e){
	                    e.printStackTrace();
	                }
	            }
	        }).start();
		
		
		
     	new Thread(new Runnable(){
    		public void run(){
    			try{
    				while(true){
    					
    					while((clientOb.fromServer = clientOb.inFromServer.readUTF())!=null && clientOb.fromServer.length()>0)
        				{
    						
    						// int i = 0;
        					String []options = clientOb.fromServer.split("\r\n");
        					options = options[0].split(" ");
        					System.out.println(clientOb.fromServer);
            		        if (options[1].equals("LIST")){
            		        	System.out.println("----------------------inList");
            		        	clientOb.listOnline();
            		        	dlm.clear();
            		        	for(String key : clientOb.userlist.keySet()){
            		        	//	name[i++] = key;
            		        		dlm.addElement(key);
            		        		
            		        	}
            		        	//JList list = new JList();
            		        	list.setModel(dlm);
            		    		list.setBounds(337, 6, 107, 266);
            		    		
            		    		contentPane.add(list);
            		        	
            		        }
            		        	
            		        else if (options[1].equals("UPDATE")){
            		        	System.out.println("updating");
            		        	clientOb.updateOnline();
            		        	dlm.clear();
            		        	for(String key : clientOb.userlist.keySet()){
            		        		System.out.println(key);
                		        	dlm.addElement(key);
                		        		
                		        }
            		       
            		        	//JList list = new JList();
            		        	list.setModel(dlm);
            		    		list.setBounds(337, 6, 107, 266);
            		    		
            		    		contentPane.add(list);
            		        	
            		        }
            		         
            		        else if (options[1].equals("CSMESSAGE")){
                		        System.out.println("csmmm");
                		        textArea.setText(textArea.getText()+options[2]+"("+options[4]+" "+options[5]+")"+":"+"\n"+options[8]+"\n");
                		        textArea.setCaretPosition(textArea.getDocument().getLength()-1);
            		        	//csMessage();	
            		        }
            		       
            		       // else if (options[1].equals("CSMESSAGE"))
            		     
        				}
    				}
        				
        				
    				
    					
		
    			}catch(IOException e){
    				e.printStackTrace();
    			}
    		}
    	}).start();
     	
		
		//JList list = new JList(clientOb.name);
		//list.setBounds(337, 6, 107, 266);
		
		//contentPane.add(list);
		this.setTitle(user_name);
			
		
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
	}
}
