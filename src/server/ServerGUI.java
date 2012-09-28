/** @Cetin
 * GUI display when server start: shows the start button, player count and game status.
 * Use constructor to run, any of the show...() functions to display.
 * Use isClosed() to executable if game was canceled, and isStarted to executable if game has started
 */
package server;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ServerGUI implements ActionListener{
	private JFrame appWindow;
	private int windowSize = 250;
	private Font fontNormal;

	private Boolean isClosed = false;
	private Boolean isStarted = false;

	public ServerGUI(){
		initGUI();
	}

	private void initGUI(){
		/* Set appearance */
		fontNormal = new Font("Segoe UI", Font.PLAIN, 14);
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			// TODO Auto-generated catch block
			System.err.println("Look and feel gave up: " + e.toString());
		}

		/* Initialize graphics */
		appWindow = new JFrame("Host Server"); // main window
		appWindow.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent arg0){
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0){
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0){
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0){
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0){
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent arg0){
				// TODO Auto-generated method stub
			}

			@Override
			public void windowActivated(WindowEvent arg0){
				// TODO Auto-generated method stub
			}
		}); // listen for window close

		/* Turn on double buffering */
		appWindow.getRootPane().setDoubleBuffered(true);

		/* Display the window */
		appWindow.setSize(windowSize, windowSize);
		appWindow.setLocationRelativeTo(null); // placed in center of window
		appWindow.setVisible(true);
	}

	public void showGameSetupMenu(){
		/* Reset */
		isClosed = false;
		isStarted = false;
		
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.LINE_AXIS));

		/* Texts */
		JButton startButton = new JButton();
		startButton.setActionCommand("start");
		startButton.setText("Start New Game");
		startButton.grabFocus();
		startButton.addActionListener(this);

		menu.add(Box.createHorizontalGlue()); // to keep things horizontal centered
		menu.add(startButton);
		menu.add(Box.createHorizontalGlue()); // to keep things horizontal centered
		showContent(menu);
		
		startButton.requestFocusInWindow();
	}
	/* Number of player */
	public int getPlayerCount() throws NumberFormatException{
		JLabel text = new JLabel("Enter the number of players: ");
		text.setFont(fontNormal);
		String response = (String)JOptionPane.showInputDialog(appWindow, text, "Setup Step 1", JOptionPane.PLAIN_MESSAGE, null, null, "2");
		return Integer.parseInt(response);
	}
      /* Game running message*/
	public void showGameRunning(int port){
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
		JPanel submenu = new JPanel();
		submenu.setLayout(new BoxLayout(submenu, BoxLayout.LINE_AXIS));

		/* Texts */
		JLabel confirmationText = new JLabel("Game is running", SwingConstants.CENTER);
		JButton closeButton = new JButton();
		closeButton.setActionCommand("close");
		closeButton.setText("Cancel Game");
		closeButton.addActionListener(this);

		confirmationText.setFont(fontNormal);

		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		menu.add(confirmationText);
		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		
		submenu.add(Box.createHorizontalGlue()); // to keep things horizontal centered
		submenu.add(closeButton);
		submenu.add(Box.createHorizontalGlue()); // to keep things horizontal centered
		menu.add(submenu);
		
		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		showContent(menu);
	}

	private void showContent(JPanel panel){
		/* Clears window and puts the content */
		appWindow.setSize(windowSize, windowSize);
		appWindow.getContentPane().removeAll();
		appWindow.getContentPane().add(panel);
		appWindow.setVisible(true);
	}

	public void actionPerformed(ActionEvent e){
		if("close".equals(e.getActionCommand())){
			isClosed = true;
			JButton button = (JButton)e.getSource();
			button.setText("Cancelling...");
			button.setEnabled(false);
		}
		else if("start".equals(e.getActionCommand())) isStarted = true;
	}

	public Boolean isClosed(){
		return isClosed;
	}

	public Boolean isStarted(){
		return isStarted;
	}
}
