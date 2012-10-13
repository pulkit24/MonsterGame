/** @Cetin
 * Shows the control for registering/logging in.
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import components.model.User;

public class Master extends JFrame{
	private static final long serialVersionUID = 1L;
	private JButton buttonLogin;
	private JButton buttonRegister;
	private JPanel contentPane;
	
	private User user=null;
	
	private GUI guiReference;

	public Master(GUI guiReference, User user){
		this.guiReference = guiReference;
		this.user = user;
		createMaster();
		this.setVisible(true);
	}

	private void createMaster(){
		buttonLogin = new JButton();
		buttonRegister = new JButton();
		contentPane = (JPanel)this.getContentPane();
		buttonLogin.setBackground(new Color(220, 204, 204));
		buttonLogin.setForeground(new Color(0, 0, 255));
		buttonLogin.setText("LOGIN");

		buttonLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				buttonLogin_actionPerformed(e);
			}
		});

		buttonRegister.setBackground(new Color(220, 204, 204));
		buttonRegister.setForeground(new Color(0, 0, 255));
		buttonRegister.setText("REGISTER");

		buttonRegister.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				buttonRegister_actionPerformed(e);
			}
		});

		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		contentPane.setBackground(new Color(192, 192, 192));

		addComponent(contentPane, buttonLogin, 150, 20, 83, 28);
		addComponent(contentPane, buttonRegister, 20, 20, 83, 28);

		this.setTitle("MEMBER  LOGIN");
//		this.setLocation(new Point(80, 200));
		this.setLocationRelativeTo(null); // placed in center of window
		this.setSize(new Dimension(300, 100));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);
	}

	/* set/add Component not  With Layout Manager( Positioning x, y, with High)*/
	private void addComponent(Container container, Component c, int x, int y, int w, int h){
		c.setBounds(x, y, w, h);
		container.add(c);
	}

	private void buttonLogin_actionPerformed(ActionEvent e){
		if(user!=null)
			new Login(guiReference, user.getUsername());
		else
			new Login(guiReference, "");
		this.setVisible(false);
	}

	private void buttonRegister_actionPerformed(ActionEvent e){
		new Register(guiReference, user);
		this.setVisible(false);
	}

}
