package gui;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JEditorPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;


public class About extends JDialog {
	
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public About() {
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel label = new JLabel(" \u5173\u4E8E\u6211\u4EEC");
		label.setBounds(192, 28, 61, 16);
		contentPanel.add(label);
		
		JLabel label_1 = new JLabel("\u56E2\u961F\u6210\u5458\uFF1A");
		label_1.setBounds(82, 66, 75, 16);
		contentPanel.add(label_1);
		
		JLabel label_2 = new JLabel("\u9EC4\u654F                \u2014\u2014\u2014\u2014-\u5BA2\u6237\u7AEF");
		label_2.setBounds(124, 98, 216, 16);
		contentPanel.add(label_2);
		
		JLabel label_3 = new JLabel("\u9EC4\u6D69\u7136\t\t  \u2014\u2014\u2014\u2014-\u5BA2\u6237\u7AEF");
		label_3.setBounds(124, 126, 216, 16);
		contentPanel.add(label_3);
		
		JLabel lblui = new JLabel("\u9093\u5353\u5F6C\t\t  \u2014\u2014\u2014\u2014-\u5BA2\u6237\u7AEF\uFF0CUI");
		lblui.setBounds(124, 154, 227, 16);
		contentPanel.add(lblui);
		
		JLabel label_5 = new JLabel("\u8521\u8FDB\t\t         \u2014\u2014\u2014\u2014-\u670D\u52A1\u7AEF");
		label_5.setBounds(124, 182, 216, 16);
		contentPanel.add(label_5);
		
		JLabel label_6 = new JLabel("\u52B3\u601D\t                \u2014\u2014\u2014\u2014-\u670D\u52A1\u7AEF\n");
		label_6.setBounds(124, 210, 216, 16);
		contentPanel.add(label_6);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			JLabel label_4 = new JLabel("\u4E86\u89E3\u66F4\u591A:");
			buttonPane.add(label_4);
			
			JLabel lblhttpsgithubcomlaosiaudiminet = new JLabel("<html><a href=\"https://github.com/laosiaudi/Minet\">https://github.com/laosiaudi/Minet</a></html>\n");
			lblhttpsgithubcomlaosiaudiminet.setFont(new Font("Monaco", lblhttpsgithubcomlaosiaudiminet.getFont().getStyle(), 13));
			lblhttpsgithubcomlaosiaudiminet.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try{
					Desktop.getDesktop().browse(new java.net.URI("https://github.com/laosiaudi/Minet"));
					}catch(Exception ee){}
				}
			});
			buttonPane.add(lblhttpsgithubcomlaosiaudiminet);
		}
	}
}
