package client;




import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Master extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JButton buttonLogin;
	private JButton buttonRegister;
	private JPanel contentPane;
	
	public Master()
	{
		super();
		createMaster();
		this.setVisible(true);
	}
	
	private void createMaster()
	{
		buttonLogin = new JButton();
		buttonRegister = new JButton();
		contentPane = (JPanel)this.getContentPane();
		buttonLogin.setBackground(new Color(220, 204, 204));
		buttonLogin.setForeground(new Color(0, 0, 255));
		buttonLogin.setText("LOGIN");
		
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonLogin_actionPerformed(e);
			}
		});
		
		buttonRegister.setBackground(new Color(220, 204, 204));
		buttonRegister.setForeground(new Color(0, 0, 255));
		buttonRegister.setText("REGISTER");
		
		buttonRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonRegister_actionPerformed(e);
			}
		});
		
		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		contentPane.setBackground(new Color(192, 192, 192));
		
		addComponent(contentPane, buttonLogin, 150,20,83,28);
		addComponent(contentPane, buttonRegister, 20,20,83,28);

		this.setTitle("MEMBER  LOGIN");
		this.setLocation(new Point(80, 200));
		this.setSize(new Dimension(300, 100));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);	
	}
	/* set/add Component not  With Layout Manager( Positioning x, y, with High)*/
	private void addComponent(Container container,Component c,int x,int y,int w,int h)
	{
		c.setBounds(x,y,w,h);
		container.add(c);
	}
	
	private void buttonLogin_actionPerformed(ActionEvent e)
	{

		new Login();
	   	this.setVisible(false); 
		
      }
	
	private void buttonRegister_actionPerformed(ActionEvent e)
	{
		//this.setVisible(true);	
		new Register();
	}
	
	
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
		new Master();
	};
	
}
 