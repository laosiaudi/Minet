package gui;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.awt.Color;

import javax.swing.UIManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {

	private JFrame frmMinet;
	private JTextField textField;
	private String user_name;
	private Client clientOb = new Client() ;
	private personal per;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}
	
	boolean hello() {
		try{
			return clientOb.hello();
		}catch(Exception e){
			
		}
		return true;
	}
	
	boolean login(){
		try{
			return clientOb.login(user_name);
		}catch(Exception e){
			
		}
		return true;
	}
	

	/**4
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMinet = new JFrame();
		
		frmMinet.setTitle("Minet");
		frmMinet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		frmMinet.setBounds(100, 100, 450, 300);
		
		frmMinet.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("user_name");
		lblNewLabel.setBounds(67, 115, 91, 16);
		frmMinet.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				 if(e.getKeyCode() == KeyEvent.VK_ENTER){
					
					    user_name = textField.getText();
					    
						clientOb.username = user_name;
						Client.connecting = hello();
						if (Client.connecting == false){
							JOptionPane.showMessageDialog(null, "connection error!");
						}
						else{
							System.out.println("connection success!");
							
							if (login()){
							try{
								
								System.out.println("Login success!");
								clientOb.heartBeat(clientOb.clientSocket);
								per = new personal(user_name,clientOb);
							}catch(Exception ew)	{}
								per.setVisible(true);
								
								
								frmMinet.hide();
								//System.out.println("yessss");
								
								
								new Thread(new Runnable(){
			                        public void run(){
			                        	try{
			                        		ServerSocket welcomeSocket;
			        						Socket connectionSocket;
			    							welcomeSocket = new ServerSocket(6789);
			    							while (Client.connecting){
			    								connectionSocket = welcomeSocket.accept();
			    								
			    								clientOb.process(connectionSocket);
			    							}
			    						}catch(Exception e){
			    							e.printStackTrace();
			    						}
			                        }
			                               
			                        }).start();
								
							}
							else{
								JOptionPane.showMessageDialog(null, "login error!");
							}
						}
				 }
			}
		});
		textField.setBounds(189, 109, 134, 28);
		frmMinet.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnLogin = new JButton("login");
		btnLogin.setBounds(178, 188, 117, 29);
		frmMinet.getContentPane().add(btnLogin);
		frmMinet.setVisible(true);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				user_name = textField.getText();
				
				//clientOb.username = user_name;
				
				try{
					System.out.println(user_name.getBytes());
				clientOb.username = new String(user_name.getBytes(),"GBK");
				System.out.println(new String(user_name.getBytes(),"GBK"));
				}catch(Exception ee){}
				
				Client.connecting = hello();
				if (Client.connecting == false){
					JOptionPane.showMessageDialog(null, "connection error!");
				}
				else{
					System.out.println("connection success!");
					if (login()){
					try{
						
						System.out.println("Login success!");
						clientOb.heartBeat(clientOb.clientSocket);
						per = new personal(user_name,clientOb);
					}catch(Exception eee){}
						per.setVisible(true);
						
						
						frmMinet.hide();
						//System.out.println("yessss");
						
						
						new Thread(new Runnable(){
	                        public void run(){
	                        	try{
	                        		ServerSocket welcomeSocket;
	        						Socket connectionSocket;
	    							welcomeSocket = new ServerSocket(6789);
	    							while (Client.connecting){
	    								connectionSocket = welcomeSocket.accept();
	    								
	    								clientOb.process(connectionSocket);
	    							}
	    						}catch(Exception e){
	    							e.printStackTrace();
	    						}
	                        }
	                               
	                        }).start();
						
					}
					else{
						JOptionPane.showMessageDialog(null, "login error!");
					}
				}
				
			}
		});
		
		
		
	}
}
