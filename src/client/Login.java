package client;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Login extends JFrame
{
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
	
	public Login()
	{
		super();
		createLogin();
		this.setVisible(true);
	}
	
	private void createLogin()
	{
		labelUserName = new JLabel();
	    labelPassword = new JLabel();
		textFieldUserName = new JTextField();
		passwordField= new JPasswordField();
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
		
		textFieldUserName.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				textFieldUserName_actionPerformed(e);
			}
		});
		
		// jPasswordField for password 
		passwordField.setForeground(new Color(0, 0, 255));
		passwordField.setToolTipText("Enter password");
		passwordField.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				passwordField_actionPerformed(e);
			}
		});
		
		// jButton  buton for login
		
		buttonLogin.setBackground(new Color(204, 204, 204));
		buttonLogin.setForeground(new Color(0, 0, 255));
		buttonLogin.setText("LOGIN");
		
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonLogin_actionPerformed(e);
			}
		});
		//
		// contentPane
		//
		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		contentPane.setBackground(new Color(204, 204, 204));
		addComponent(contentPane, labelUserName, 20,16,80,10);
		addComponent(contentPane, labelPassword, 20,50,80,10);
		addComponent(contentPane, textFieldUserName, 100,14,200,23);
		addComponent(contentPane, passwordField, 100,48,200,23);
		addComponent(contentPane, buttonLogin, 150,75,83,28);

		// login Area
	
		this.setTitle("MEMBER  LOGIN");
		this.setLocation(new Point(80, 200));
		this.setSize(new Dimension(330, 150));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);//or true
		//this.setResizable(rootPaneCheckingEnabled);
	}
	/* set/add Component not  With Layout Manager( Positioning x, y, with High)*/
	private void addComponent(Container container,Component c,int x,int y,int w,int h)
	{
		c.setBounds(x,y,w,h);
		container.add(c);
	}
	
	private void textFieldUserName_actionPerformed(ActionEvent e)
	{
		
	}
	private void passwordField_actionPerformed(ActionEvent e)
	{
		
	}
	private void buttonLogin_actionPerformed(ActionEvent e)
	{
		
		String username = new String(textFieldUserName.getText());
		String password = new String(passwordField.getPassword());
		
		if(username.equals("") || password.equals("")) // If password and user Name is empty > Do this >>>
				{
					buttonLogin.setEnabled(false);
				JLabel errorFields = new JLabel("<HTML><FONT COLOR = Blue>You must enter a username and password to login.</FONT></HTML>");	
				
				JOptionPane.showMessageDialog(null,errorFields); 
				textFieldUserName.setText("");
	   		    passwordField.setText("");
	   		    buttonLogin.setEnabled(true);
				this.setVisible(true);
				}
				else
				{
JLabel optionLabel = new JLabel("<HTML><FONT COLOR = Blue>You Entered</FONT><FONT COLOR = RED> <B>"+username+"</B></FONT> <FONT COLOR = Blue>as your User Name.<BR> Is this correct!</FONT></HTML>");
//JLabel optionLabel = new JLabel("<HTML><FONT COLOR = Blue>You Entered</FONT><FONT COLOR = RED> <B>"+password+"</B></FONT> <FONT COLOR = Blue>as your User Name.<BR> Is this correct!</FONT></HTML>");
int confirm =JOptionPane.showConfirmDialog(null,optionLabel);
switch(confirm){       // Switch > Case
	   	case JOptionPane.YES_OPTION:  // Attempts to Login user
	   	buttonLogin.setEnabled(false);   // Set button enable to false to prevent 2 login attempts
	   	this.setVisible(false);  //hide register form
	   	break;
            case JOptionPane.NO_OPTION:   // No button click Going to back. Set text to "")
	   		buttonLogin.setEnabled(false);
	   		textFieldUserName.setText("");
	   		passwordField.setText("");
	   		buttonLogin.setEnabled(true);
            break;
	   		
	   		case JOptionPane.CANCEL_OPTION:  // Cancel button click Going to  back Set text to "")
	   	    buttonLogin.setEnabled(false);
	   	    textFieldUserName.setText("");
	   	    passwordField.setText("");
	   	    buttonLogin.setEnabled(true);
	   	    break;	
	   	   }   //Switch
	   }
	   }
	/*
	public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
		new Login();
	};
	*/
}
 