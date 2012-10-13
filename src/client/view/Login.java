/** @Cetin
 * Shows the login form.
 * Usage:
 * Simply initialize new object. 
 * 	Pass arguments to fill in the default values of the form
 * 	(useful for showing previously entered "autofill" values)
 */
package client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import components.model.User;

public class Login extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Variables declare
	private JLabel labelUserName;
	private JLabel labelPassword;
	private JTextField textFieldUserName;
	private JPasswordField passwordField;
	private JButton buttonLogin;

	private JPanel contentPane;

	private GUI guiReference;

	public Login(GUI guiReference, String defaultUsername){
		this.guiReference = guiReference;

		createLogin(defaultUsername);
		this.setVisible(true);
	}

	private void createLogin(String defaultUsername){
		labelUserName = new JLabel();
		labelPassword = new JLabel();
		textFieldUserName = new JTextField(defaultUsername);
		passwordField = new JPasswordField();
		buttonLogin = new JButton();

		contentPane = (JPanel)this.getContentPane();

		// jLabel for user Name
		labelUserName.setHorizontalAlignment(SwingConstants.LEFT);
		labelUserName.setForeground(new Color(0, 0, 255));
		labelUserName.setText("User Name:");

		// jLabel for password
		labelPassword.setHorizontalAlignment(SwingConstants.LEFT);
		labelPassword.setForeground(new Color(0, 0, 255));
		labelPassword.setText("Password:");

		// jTextField for user name
		textFieldUserName.setForeground(new Color(0, 0, 255));
		textFieldUserName.setSelectedTextColor(new Color(0, 0, 255));
		textFieldUserName.setToolTipText("Enter your User Name");

		textFieldUserName.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				textFieldUserName_actionPerformed(e);
			}
		});

		// jPasswordField for password
		passwordField.setForeground(new Color(0, 0, 255));
		passwordField.setToolTipText("Enter password");
		passwordField.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				passwordField_actionPerformed(e);
			}
		});

		// jButton buton for login

		buttonLogin.setBackground(new Color(204, 204, 204));
		buttonLogin.setForeground(new Color(0, 0, 255));
		buttonLogin.setText("LOGIN");

		buttonLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					buttonLogin_actionPerformed(e);
				}catch(RemoteException e1){
					// TODO Auto-generated catch block
					System.err.println(e1.toString());
				}
			}
		});
		//
		// contentPane
		//
		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		contentPane.setBackground(new Color(204, 204, 204));
		addComponent(contentPane, labelUserName, 20, 16, 80, 10);
		addComponent(contentPane, labelPassword, 20, 50, 80, 10);
		addComponent(contentPane, textFieldUserName, 100, 14, 200, 23);
		addComponent(contentPane, passwordField, 100, 48, 200, 23);
		addComponent(contentPane, buttonLogin, 150, 75, 83, 28);

		// login Area

		/* Error */
		if(defaultUsername != null && !defaultUsername.equals("")) JOptionPane.showMessageDialog(contentPane, "Invalid username/password",
				"Attention", JOptionPane.ERROR_MESSAGE, null);

		this.setTitle("MEMBER  LOGIN");
		// this.setLocation(new Point(80, 200));
		this.setLocationRelativeTo(null); // placed in center of window
		this.setSize(new Dimension(330, 150));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);// or true
		// this.setResizable(rootPaneCheckingEnabled);
	}

	/* set/add Component not  With Layout Manager( Positioning x, y, with High)*/
	private void addComponent(Container container, Component c, int x, int y, int w, int h){
		c.setBounds(x, y, w, h);
		container.add(c);
	}

	private void textFieldUserName_actionPerformed(ActionEvent e){

	}

	private void passwordField_actionPerformed(ActionEvent e){

	}

	private void buttonLogin_actionPerformed(ActionEvent e) throws RemoteException{

		String username = new String(textFieldUserName.getText());
		String password = new String(passwordField.getPassword());

		if(username.equals("") || password.equals("")) // If password and user Name is empty > Do this >>>
		{
			buttonLogin.setEnabled(false);
			JLabel errorFields = new JLabel("<HTML><FONT COLOR = Blue>You must enter a username and password to login.</FONT></HTML>");

			JOptionPane.showMessageDialog(null, errorFields);
			textFieldUserName.setText("");
			passwordField.setText("");
			buttonLogin.setEnabled(true);
			this.setVisible(true);
		}else{
			buttonLogin.setEnabled(false); // Set button enable to false to prevent 2 login attempts
			this.setVisible(false); // hide register form

			/* Make consumable by main GUI */
			synchronized(guiReference){
				guiReference.setUserData(new User(username, password));
			}

		}
	}
}
