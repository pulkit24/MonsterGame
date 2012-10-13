/** @Cetin
 * Shows the registration form.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

public class Register extends JFrame{

	private static final long serialVersionUID = 1L;
	// Variables declare
	private JLabel firstNameLabel, surnameLabel, addressLabel, phoneNumberLabel, usernameLabel, passwordLabel;
	private JTextField firstNameField, surnameField, addressField, phoneNumberFiled, usernameField;
	private JPasswordField passwordField;
	private JButton submitButton;
	// private JPanel panel;
	private JPanel contentPane;

	private GUI guiReference;

	public Register(GUI guiReference, User user){
		this.guiReference = guiReference;
		createRegister(user);
		this.setVisible(true);
	}

	private void createRegister(User user){

		if(user != null){
			firstNameLabel = new JLabel();
			firstNameField = new JTextField(user.getFirstName());

			surnameLabel = new JLabel();
			surnameField = new JTextField(user.getSurname());

			addressLabel = new JLabel();
			addressField = new JTextField(user.getAddress());

			phoneNumberLabel = new JLabel();
			phoneNumberFiled = new JTextField(user.getPhone());

			usernameLabel = new JLabel();
			usernameField = new JTextField(user.getUsername());

			passwordLabel = new JLabel();
			passwordField = new JPasswordField();

		}else{
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
		}

		submitButton = new JButton();

		contentPane = (JPanel)this.getContentPane();

		// first name
		firstNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		firstNameLabel.setForeground(new Color(0, 0, 255));
		firstNameLabel.setText("First Name");

		firstNameField.setForeground(new Color(0, 0, 255));
		firstNameField.setSelectedTextColor(new Color(0, 0, 255));
		firstNameField.setToolTipText("Enter your First Name");

		// Surname
		surnameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		surnameLabel.setForeground(new Color(0, 0, 255));
		surnameLabel.setText("Surname");

		surnameField.setForeground(new Color(0, 0, 255));
		surnameField.setSelectedTextColor(new Color(0, 0, 255));
		surnameField.setToolTipText("Enter your Surname");

		// Address
		addressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		addressLabel.setForeground(new Color(0, 0, 255));
		addressLabel.setText("Address");

		addressField.setForeground(new Color(0, 0, 255));
		addressField.setSelectedTextColor(new Color(0, 0, 255));
		addressField.setToolTipText("Enter your Address");

		// Phone number
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

		// jButton button for submit

		submitButton.setBackground(new Color(204, 204, 255));
		submitButton.setForeground(new Color(0, 0, 255));
		submitButton.setText("SUBMIT");

		submitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					buttonLogin_actionPerformed(e);
				}catch(RemoteException e1){
					// TODO Auto-generated catch block
					System.err.println(e1.toString());
				}
			}
		});

		// contentPane

		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		contentPane.setBackground(new Color(200, 200, 255));

		addComponent(contentPane, firstNameLabel, 15, 18, 150, 10);
		addComponent(contentPane, surnameLabel, 15, 52, 150, 10);
		addComponent(contentPane, addressLabel, 15, 84, 150, 10);
		addComponent(contentPane, phoneNumberLabel, 15, 120, 150, 10);
		addComponent(contentPane, usernameLabel, 15, 155, 150, 10);
		addComponent(contentPane, passwordLabel, 15, 189, 150, 10);

		addComponent(contentPane, firstNameField, 110, 14, 193, 23);
		addComponent(contentPane, surnameField, 110, 48, 193, 23);
		addComponent(contentPane, addressField, 110, 82, 193, 23);
		addComponent(contentPane, phoneNumberFiled, 110, 116, 193, 23);
		addComponent(contentPane, usernameField, 110, 150, 193, 23);
		addComponent(contentPane, passwordField, 110, 184, 193, 23);

		addComponent(contentPane, submitButton, 217, 218, 85, 28);

		// Registrations

		/* Error */
		if(user != null) JOptionPane.showMessageDialog(contentPane, "Username already taken", "Attention", JOptionPane.ERROR_MESSAGE, null);

		this.setTitle("REGISTRATIONS FORM");
		// this.setLocation(new Point(450, 300));
		this.setLocationRelativeTo(null); // placed in center of window
		this.setSize(new Dimension(330, 300));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);// or true
		// this.setResizable(rootPaneCheckingEnabled);
	}

	/* set/add Component not  With Layout Manager( Positioning x, y, with High)*/
	private void addComponent(Container container, Component c, int x, int y, int w, int h){
		c.setBounds(x, y, w, h);
		container.add(c);
	}

	private void buttonLogin_actionPerformed(ActionEvent e) throws RemoteException{

		String firstName = new String(firstNameField.getText());
		String surname = new String(surnameField.getText());
		String address = new String(addressField.getText());
		String phoneNumber = new String(phoneNumberFiled.getText());
		String username = new String(usernameField.getText());
		String password = new String(passwordField.getPassword());
		Matcher phn = Pattern.compile("[^\\d\\s-]").matcher(phoneNumber);
		// If password and user Name is empty > Do this >>>
		if(firstName.isEmpty() || surname.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || username.isEmpty()
				|| password.isEmpty()){
			submitButton.setEnabled(false);
			// JLabel errorFields = new JLabel("<HTML><FONT COLOR = Blue >You must Fill all the Fields before Submit</FONT></HTML>");

			JLabel errorFields = new JLabel("<HTML><FONT COLOR = Red >You must Fill all the Text Fields before Submit</FONT></HTML>");

			JOptionPane.showMessageDialog(null, errorFields);

			submitButton.setEnabled(true);
			this.setVisible(true);
		}

		// if in the phone number field there is a LETER do this
		else if(phn.find()){
			submitButton.setEnabled(false);
			JLabel errorFields = new JLabel("<HTML><FONT COLOR = Red >You Enter LETER in phone Field</FONT></HTML>");
			JOptionPane.showMessageDialog(null, errorFields);
			submitButton.setEnabled(true);
			this.setVisible(true);
		}

		else{
			submitButton.setEnabled(false); // Set button enable to false to prevent 2 login attempts
			this.setVisible(false); // hide register form
			User user = new User(firstName, surname, address, phoneNumber, username, password);

			/* Make consumable by main GUI */
			synchronized(guiReference){
				guiReference.setUserData(user);
			}
		}

	}

}
