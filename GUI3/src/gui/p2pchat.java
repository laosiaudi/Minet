package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class p2pchat extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea; 
	private Socket _socket;
	private String username;
	private Timer timer = new Timer();
	public Client clientOb;
	private p2pchat local;
	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
			//	try {
				//	p2pchat frame = new p2pchat();
					//frame.setVisible(true);
			//	} catch (Exception e) {
				//	e.printStackTrace();
				//}
			}
	});
	}
	 /*P2P action*/
    private static int action(String message){
        String []options = message.split(" ");
        if (options[0].equals("MINET"))
            return 1;
        else{
            if (options.length <= 1) {
                return 0;
            }
            if (options[1].equals("Leave"))
                return 2;
            else if (options[1].equals("P2PMESSAGE"))
                return 3;
            else if (options[1].equals("BEAT"))
                return 4;
            else if (options[1].equals("P2PFile"))
                return 5;
        }
        return 0;
    }
    
	/**
	 * Create the frame.
	 */
	public p2pchat(Client c,String name,Socket p2psocket) {
		clientOb = c;
		username = name;
		local = this;
		_socket = p2psocket;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Monaco", textArea.getFont().getStyle(), 13));
		textField = new JTextField();
		
		 new Thread(new Runnable(){
	            public void run(){
	                boolean flag = true;
	                
	                try{
	                    
	                	  System.out.println("-----------------");
	                     timer.purge();
	                    /*BufferedReader inFromP2P = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
	                    DataOutputStream outToP2P = new DataOutputStream(_socket.getOutputStream());
	                    while (flag){
	                        String sentence;
	                        StringBuilder temp = new StringBuilder();
	                        int ch;
	                        int pre = '\0';
	                        while ( 0 <= (ch = inFromP2P.read())){
	                        	System.out.println((char)ch);
	                            if (ch == '\n' && pre == '\n')
	                                break;
	                            temp.append((char)ch);
	                            pre = ch;
	                        }
	                        System.out.println("dddddddddddddddddd");
	                        sentence = temp.toString();*/
	                     DataInputStream inFromP2P = new DataInputStream(_socket.getInputStream());
	                     DataOutputStream outToP2P = new DataOutputStream(_socket.getOutputStream());
	                     while (flag){
	                	  	
	                        String sentence;
	                        while ((sentence = inFromP2P.readUTF())==null & sentence.length()<=0){
	                        }
	                        int state = action(sentence);
	                        String status = "";
	                        switch(state){
	                        case 1:
	                            status =clientOb.handshake(sentence,_socket);
	                            outToP2P.writeBytes(status + '\n');
	                            clientOb.heartBeat(_socket);
	                            break;
	                        case 2:
	                            status = clientOb.leave_P2P_chating(sentence);
	                            flag = false;
	                            break;
	                        case 3:
	                            System.out.println(sentence);
	                            String []options = sentence.split("\r\n");
	                            String []host_info = options[0].split(" ");
	                            String clientName = host_info[2];
	                            username = clientName;
	                            local.setTitle(username);
	                            try{
	                            	String text = clientOb.P2Pmessage(sentence,_socket);
	                            	textArea.setText(textArea.getText() + text);
	                            }catch(Exception ee){}
	                            
	                            break;
	                        case 4:
	                            clientOb.keepP2PBeat(sentence);
	                            break;
	                        
	                    	case 5:
	                    		try{
	                    			String []optionss = sentence.split("\r\n");
		                            String []hostt_info = optionss[0].split(" ");
		                            String clienttName = hostt_info[2];
		                            username = clienttName;
	                    			JFileChooser savefile = new JFileChooser();
	                    			savefile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	                    			String filepath = "";
	                    			int returnVal = savefile.showSaveDialog(local);
	                    			if (returnVal == savefile.APPROVE_OPTION){
	                    				filepath = savefile.getSelectedFile().getPath(); 
	                    			}
	                    			filepath = filepath + "/";
	                    			
	                    			if (filepath.equals("/") == false){
	                    				
	                    				clientOb.P2PrecFile(_socket,sentence, filepath);
	                    				textArea.setText(textArea.getText()+"Sending file completed!" +"\n");
	    						    
	                    				textArea.setCaretPosition(textArea.getDocument().getLength()-1);
	                    			}
	                    			else{
	                    				textArea.setText(textArea.getText()+"Denied!" +"\n");
		    						    
	                    				textArea.setCaretPosition(textArea.getDocument().getLength()-1);
	                    				clientOb.sendMessage("Denied!",username);
	                    			}
	                    		}catch (Exception e){
	                    			e.printStackTrace();
	                    		}
	                    		break;
                        
                        	}
	                    }
	                }catch(IOException e){
	                    e.printStackTrace();
	                }
	            }
	        }).start();
	
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				  if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
						try{
							String text = textField.getText();
							
							
							textField.setText("");
						    clientOb.sendMessage(text,username);
						    System.out.println("Sending!!!!!!!!!!!!!");
						    System.out.println(text);
						    textArea.setText(textArea.getText()+ clientOb.username+":"+"\n"+text+"\n");
						    
						    textArea.setCaretPosition(textArea.getDocument().getLength()-1);
							}
							catch(Exception es){}
						}
				  }
			
		});
		textField.setBounds(6, 198, 310, 74);
		contentPane.add(textField);
		textField.setColumns(10);
		
		
		textArea.setBounds(6, 6, 438, 180);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6,6,438,180);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(textArea);
		
		JButton btnSendFile = new JButton("·¢ËÍÎÄ¼þ..");
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(local);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = fileChooser.getSelectedFile();
					System.out.println(file.getAbsolutePath());
					try{
						clientOb.P2PsendFile(username,file.getAbsolutePath());
						
					}catch(Exception ee){}
				}
			}
		});
		btnSendFile.setBounds(328, 198, 117, 29);
		contentPane.add(btnSendFile);
		
		
		
	}	
}
