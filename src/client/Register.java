package client;



import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

import javax.swing.*;
public class Register extends JFrame
{
	
	private static final long serialVersionUID = 1L;
	// Variables declare
	private JLabel firstNameLabel, surnameLabel, addressLabel, phoneNumberLabel, usernameLabel, passwordLabel; 
	private JTextField  firstNameField, surnameField, addressField, phoneNumberFiled, usernameField;
	private JPasswordField passwordField;
	private JButton submitButton;
	//private JPanel panel;
	private JPanel contentPane;
	
	public Register()
	{
		super();
		createRegister();
		this.setVisible(true);
	}
	
	private void createRegister()
	{
		
		
	    firstNameLabel = new JLabel();
		firstNameField = new JTextField();
		
		surnameLabel = new JLabel();
		surnameField = new JTextField();
		
		addressLabel = new JLabel();
		addressField = new JTextField();
		
		phoneNumberLabel = new JLabel();
		phoneNumberFiled = new JTextField();
		
		usernameLabel = new JLabel();
		usernameField = new JTextField();

		passwordLabel = new JLabel();
		passwordField = new JPasswordField();

		submitButton =new JButton();	
		
		contentPane = (JPanel)this.getContentPane();
		
		//first name
		firstNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		firstNameLabel.setForeground(new Color(0, 0, 255));
		firstNameLabel.setText("First Name");
		
		firstNameField.setForeground(new Color(0, 0, 255));
		firstNameField.setSelectedTextColor(new Color(0, 0, 255));
		firstNameField.setToolTipText("Enter your First Name");
		
		//Surname
		surnameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		surnameLabel.setForeground(new Color(0, 0, 255));
		surnameLabel.setText("Surname");
		
		surnameField.setForeground(new Color(0, 0, 255));
		surnameField.setSelectedTextColor(new Color(0, 0, 255));
		surnameField.setToolTipText("Enter your Surname");
		
		//Address
		addressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		addressLabel.setForeground(new Color(0, 0, 255));
		addressLabel.setText("Address");
		
		addressField.setForeground(new Color(0, 0, 255));
		addressField.setSelectedTextColor(new Color(0, 0, 255));
		addressField.setToolTipText("Enter your Address");
		
		//Phone number
		phoneNumberLabel.setHorizontalAlignment(SwingConstants.LEFT);
		phoneNumberLabel.setForeground(new Color(0, 0, 255));
		phoneNumberLabel.setText("Phone Number");
		
		phoneNumberFiled.setForeground(new Color(0, 0, 255));
		phoneNumberFiled.setSelectedTextColor(new Color(0, 0, 255));
	    phoneNumberFiled.setToolTipText("Enter your Phone number Only Digit and - ");
		
		// jLabel for user Name
	    usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		usernameLabel.setForeground(new Color(0, 0, 255));
		usernameLabel.setText("User Name:");
	
		usernameField.setForeground(new Color(0, 0, 255));
		usernameField.setSelectedTextColor(new Color(0, 0, 255));
	    usernameField.setToolTipText("Enter your User Name");
	    
		// jLabel for password
		passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
		passwordLabel.setForeground(new Color(0, 0, 255));
		passwordLabel.setText("Password");
	 
		passwordField.setForeground(new Color(0, 0, 255));
		passwordField.setToolTipText("Enter password");
		usernameField.setToolTipText("Enter your password");	
		
		/*usernameField.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				textFieldUserName_actionPerformed(e);
			}
		});
		
		
		passwordField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				passwordField_actionPerformed(e);
			}
		});*/
		
		// jButton  button for submit
		
		submitButton.setBackground(new Color(204, 204, 255));
		submitButton.setForeground(new Color(0, 0, 255));
		submitButton.setText("SUBMIT");
		
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonLogin_actionPerformed(e);
			}
		});
		
		// contentPane
		
		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		contentPane.setBackground(new Color(200, 200, 255));
		
		addComponent(contentPane, firstNameLabel, 15,18,150,10);
		addComponent(contentPane, surnameLabel, 15,52,150,10);
		addComponent(contentPane, addressLabel,  15,84,150,10);
		addComponent(contentPane, phoneNumberLabel,15,120,150,10);
		addComponent(contentPane, usernameLabel, 15,155,150,10);
		addComponent(contentPane, passwordLabel, 15,189,150,10);
		
		addComponent(contentPane, firstNameField, 110,14,193,23);
		addComponent(contentPane, surnameField, 110,48,193,23);
		addComponent(contentPane, addressField, 110,82,193,23);
		addComponent(contentPane, phoneNumberFiled, 110,116,193,23);
		addComponent(contentPane, usernameField, 110,150,193,23);
		addComponent(contentPane, passwordField, 110,184,193,23);
		
		addComponent(contentPane, submitButton, 217,218,85,28);

		// Registrations
	
		this.setTitle("REGISTRATIONS FORM");
		this.setLocation(new Point(450, 300));
		this.setSize(new Dimension(330, 300));
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
	
	/*private void textFieldUserName_actionPerformed(ActionEvent e)
	{
		
	}
	private void passwordField_actionPerformed(ActionEvent e)
	{
		
	}*/
	private void buttonLogin_actionPerformed(ActionEvent e)
	{
	
		String firstName = new String(firstNameField.getText());
		String surname = new String( surnameField.getText());
		String address = new String(addressField.getText());
		String phoneNumber = new String(phoneNumberFiled.getText());
		String username = new String(usernameField.getText());
		String password = new String(passwordField.getPassword());
		Matcher phn = Pattern.compile("[^\\d\\s-]").matcher(phoneNumber);
		// If password and user Name is empty > Do this >>>
		if(firstName.isEmpty() || surname.isEmpty() || address.isEmpty()|| phoneNumber.isEmpty() ||username.isEmpty() || password.isEmpty() ) 
				{
					submitButton.setEnabled(false);
				//JLabel errorFields = new JLabel("<HTML><FONT COLOR = Blue >You must Fill all the Fields before Submit</FONT></HTML>");	
				
				JLabel errorFields = new JLabel("<HTML><FONT COLOR = Red >You must Fill all the Text Fields before Submit</FONT></HTML>");	
				
				JOptionPane.showMessageDialog(null,errorFields);
				
				/*addressField.setText("");
				surnameField.setText("");
				firstNameField.setText("");
				phoneNumberFiled.setText("");
				usernameField.setText("");
	   		    passwordField.setText("");
	   		    */
	   		    submitButton.setEnabled(true);
				this.setVisible(true);
				}
		
         //if in the phone number field there is a LETER do this 
		else if (phn.find()) {
       	 submitButton.setEnabled(false);
       	 JLabel errorFields = new JLabel("<HTML><FONT COLOR = Red >You Enter LETER in phone Field</FONT></HTML>");
       	 JOptionPane.showMessageDialog(null,errorFields);
       	 submitButton.setEnabled(true);
			 this.setVisible(true);
             }
         
        
				else
				{
					
					 
					
					
JLabel optionLabel = new JLabel("<HTML><FONT COLOR = Red>You Entered</FONT><FONT COLOR = RED> <B>"+username+"</B></FONT> <FONT COLOR = Red>as your User Name.<BR> Is this correct!</FONT></HTML>");
//JLabel optionLabel = new JLabel("<HTML><FONT COLOR = Blue>You Entered</FONT><FONT COLOR = RED> <B>"+password+"</B></FONT> <FONT COLOR = Blue>as your User Name.<BR> Is this correct!</FONT></HTML>");
int confirm =JOptionPane.showConfirmDialog(null,optionLabel);
switch(confirm){       // Switch > Case
	   	case JOptionPane.YES_OPTION:  // Attempts to Login user
	   	submitButton.setEnabled(false);   // Set button enable to false to prevent 2 login attempts
	   	this.setVisible(false);  //hide register form
	   	break;
            case JOptionPane.NO_OPTION:   // No button click Going to back. Set text to "")
	   		submitButton.setEnabled(false);
	   		addressField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			phoneNumberFiled.setText("");
			usernameField.setText("");
   		    passwordField.setText("");
	   		submitButton.setEnabled(true);
            break;
	   		
	   		case JOptionPane.CANCEL_OPTION:  // Cancel button click Going to  back Set text to "")
	   	    submitButton.setEnabled(false);
	   	    
	   	    addressField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			phoneNumberFiled.setText("");
			usernameField.setText("");
		    passwordField.setText("");
	   	    submitButton.setEnabled(true);
	   	    break;	
	   	   }   //Switch
	   }
	   }
	
	/*public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
		new Register();
	};
	*/
}
 