/** @Cetin
 * Handles entire graphic work for the client side.
 * Usage:
 * Start with constructor.
 * Use show...() functions to show different menus.
 * To show game map, use setGameMap() followed by refresh().
 */
package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import components.Debug;
import components.grid.Cell;
import components.grid.GameMap;
import components.model.User;

public class GUI{
	/* Graphical variables&elements*/
	private JFrame appWindow;
	private int windowSize = 500;
	private Font fontNormal, fontLarge;
	private JPanel hud;
	private JPanel gameBoard;

	private ImageIcon playerIcon;
	private ImageIcon monsterIcon;

	/* Variables to show in heads up display panel */
	private int resetsLeft;

	/* User input */
	private ArrayList<Integer> userInput; // User input keys: U D, L, R
	private boolean closed = false; // Application closed
	private User userDetails = null; // encapsulates user data
	private boolean isUserDataAvailable = false;

	/* Toggle to hide all GUI */
	boolean hidden = false;

	public GUI(){
		this.hidden = false;
		initGUI();
	}

	public GUI(Boolean hidden){
		this.hidden = hidden;
		initGUI();
	}

	/* Prepare all */
	private void initGUI(){
		/* Set appearance-looking */
		fontNormal = new Font("Segoe UI", Font.PLAIN, 14);
		fontLarge = new Font("Segoe UI", Font.BOLD, 18);
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			// TODO Auto-generated catch block
			System.err.println("Look and feel gave up: " + e.toString());
		}

		/* Initialize graphics */
		appWindow = new JFrame("PK Game"); // main window
		appWindow.addWindowListener(createWindowListener()); // listen for window close
		appWindow.addKeyListener(createKeyListener()); // listen for arrow keys
		userInput = new ArrayList<Integer>();// user input store in ArrayList

		/* Display the window */
		appWindow.setSize(windowSize, windowSize);
		appWindow.setLocationRelativeTo(null); // placed in center of window
		appWindow.setVisible(!hidden);

		/* Turn double buffering on */
		appWindow.getRootPane().setDoubleBuffered(true);

		/* Prepare icons for the Monster and the Player */
		String currentDir = System.getProperty("user.dir");
		String separator = System.getProperty("file.separator");
		String workdir = currentDir + separator + "graphics" + separator;
		playerIcon = new ImageIcon(workdir + "player.png");
		monsterIcon = new ImageIcon(workdir + "monster.png");
	}

	/* Ask the user to enter  user name*/
	public User getPlayerDetails(){
		return userDetails;
		// JLabel text = new JLabel("Choose a player name: ");
		// text.setFont(fontNormal);
		// return (String)JOptionPane.showInputDialog(appWindow, text, "Configuration Step 1", JOptionPane.PLAIN_MESSAGE, null, null,
		// System.getProperty("user.name"));
	}

	/* Get/confirm server's ip address and port from the player and also allow  new ip and port */
	public String[] getHostDetails(Boolean connectionAttemptFailed, String defaultHost, String defaultPort){
		JLabel hostAddressText;
		JLabel hostPortText = new JLabel("Host Port: ");
		int messageType;

		if(!connectionAttemptFailed){
			hostAddressText = new JLabel(wrapped("Please enter the network details of the server hosting the game.<br />Host Address: "),
					SwingConstants.CENTER);
			messageType = JOptionPane.PLAIN_MESSAGE;
		}else{
			hostAddressText = new JLabel(
					wrapped("Could not connect to the server. Please make sure you have entered the correct details.<br />Host Address: "),
					SwingConstants.CENTER);
			messageType = JOptionPane.ERROR_MESSAGE;
		}

		hostAddressText.setFont(fontNormal);
		hostPortText.setFont(fontNormal);

		String host = (String)JOptionPane.showInputDialog(appWindow, hostAddressText, "Configuration Step 2", messageType, null, null,
				defaultHost);
		String port = (String)JOptionPane.showInputDialog(appWindow, hostPortText, "Configuration Step 3", messageType, null, null,
				defaultPort);

		String hostDetails[] = new String[2];
		hostDetails[0] = host;
		hostDetails[1] = port;
		return hostDetails;
	}

	/* Ask player to pick a start point  or position  which cell to start*/
	public int getPreferredPosition(){
		JLabel text = new JLabel("Select a position: ");
		text.setFont(fontNormal);
		String[] options = {"Top Left", "Top Right", "Bottom Right", "Bottom Left"};
		return JOptionPane.showOptionDialog(appWindow, text, "Configuration Step 3", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, "Top Left");
	}

	/* Let users get to know you're trying to connect */
	public void showConnectionProgress(){
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));

		/* Texts */
		JLabel waitingText = new JLabel(wrapped("Connecting..."), SwingConstants.CENTER);

		waitingText.setFont(fontNormal);

		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		menu.add(waitingText);
		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		showContent(menu);
	}

	/* Let users get to know you're connected - but the game's has not started yet */
	public void showConnectionConfirmation(){
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));

		/* Texts */
		JLabel confirmationText = new JLabel(wrapped("Connected!"), SwingConstants.CENTER);
		JLabel waitingText = new JLabel(wrapped("Waiting for other players to connect..."), SwingConstants.CENTER);

		confirmationText.setFont(fontLarge);
		waitingText.setFont(fontNormal);

		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		menu.add(confirmationText);
		menu.add(waitingText);
		menu.add(Box.createVerticalGlue()); // to keep things vertically centered
		showContent(menu);
	}

	/* Pop up message for dead */
	public void showDefeatMessage(int score){
		JLabel text = new JLabel(wrapped("You were killed!<br/>Your score: " + score));
		text.setFont(fontLarge);
		JOptionPane.showMessageDialog(appWindow, text, "Game Over", JOptionPane.ERROR_MESSAGE, null);
	}

	/* Pop up message for victory/winner */
	public void showVictoryMessage(int score){
		JLabel text = new JLabel(wrapped("You won!<br/>Your score: " + score));
		text.setFont(fontLarge);
		JOptionPane.showMessageDialog(appWindow, text, "Game Over", JOptionPane.INFORMATION_MESSAGE, null);
	}

	/* Use to redraw the latest HUD and game map */
	public void refresh(){
		/* Combine board with HUD */
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		container.add(hud);
		container.add(gameBoard);

		/* Add all this to the window/ container */
		showContent(container);
	}

	/* Returns the next keyboard movement key pressed by the user */
	public int getUserInput(){
		int key;
		/* Get the key in FIFO */
		synchronized(userInput){
			if(userInput.isEmpty()) return -1;
			key = userInput.remove(0);
		}
		/* Is it a move key? */
		if(key == KeyEvent.VK_UP) return GameMap.UP;
		if(key == KeyEvent.VK_RIGHT) return GameMap.RIGHT;
		if(key == KeyEvent.VK_DOWN) return GameMap.DOWN;
		if(key == KeyEvent.VK_LEFT) return GameMap.LEFT;
		/* Is it the ESC key for reset? */
		if(key == KeyEvent.VK_ESCAPE) return GameMap.RESET;
		return -1;
	}

	/* Was the window closed by the user? */
	public Boolean isClosed(){
		return closed;
	}

	private WindowListener createWindowListener(){
		return new WindowListener(){
			@Override
			public void windowOpened(WindowEvent e){
				// TODO Auto-generated method stub
			}

			@Override
			public void windowIconified(WindowEvent e){
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeiconified(WindowEvent e){
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeactivated(WindowEvent e){
				// TODO Auto-generated method stub
			}

			@Override
			public void windowClosing(WindowEvent e){
				closed = true;
			}

			@Override
			public void windowClosed(WindowEvent e){
				// TODO Auto-generated method stub
			}

			@Override
			public void windowActivated(WindowEvent e){
				// TODO Auto-generated method stub
			}
		};
	}

	private KeyListener createKeyListener(){
		return new KeyListener(){
			@Override
			public void keyTyped(KeyEvent ke){
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent arg0){
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent key){
				if(key.isActionKey() || key.getKeyCode() == KeyEvent.VK_ESCAPE){
					synchronized(userInput){
						userInput.add(key.getKeyCode());
						Debug.log("GUI", "key pressed!");
					}
				}
			}
		};
	}

	/* Prepare the panel for the Heads Up Display at the top */
	private void drawHUD(){
		/* Generates a heads up display panel displaying stats */
		hud = new JPanel();
		hud.setLayout(new BoxLayout(hud, BoxLayout.LINE_AXIS));

		JLabel controlsHelp = new JLabel(wrapped("Move with arrow keys | Press ESC to reset to centre"));
		controlsHelp.setFont(fontNormal);

		JLabel resetsLeftLabel = new JLabel(wrapped("Resets Left: " + resetsLeft));
		resetsLeftLabel.setFont(fontNormal);
		resetsLeftLabel.setBackground(Color.darkGray);

		hud.add(controlsHelp);
		hud.add(resetsLeftLabel);
	}

	/* Handy function to display whatever panel you want in the GUI window */
	private void showContent(JPanel panel){
		/* Clears window and puts the content */
		appWindow.setSize(windowSize, windowSize);
		appWindow.getContentPane().removeAll();
		appWindow.getContentPane().add(panel);
		appWindow.setVisible(!hidden);
	}

	/* Update the title showing the player's id and name */
	public void setPlayerDetails(int playerId, String playerName){
		appWindow.setTitle("Player " + playerId + " - " + playerName);
	}

	/* Prepare the panel to show the game map */
	public void setGameMap(GameMap gameMap){
		/* Get map elements */
		int length = gameMap.getGridSquareSize();
		Cell cells[][] = gameMap.getCells();

		/* Graphical elements */
		gameBoard = new JPanel(new GridLayout(length, length));

		/* Add cells to the grid */
		for(int y = length - 1; y >= 0; y--){
			for(int x = 0; x < length; x++){
				JPanel cell = new JPanel();
				cell.setSize(40, 40);
				cell.setBorder(BorderFactory.createLineBorder(Color.gray));
				if(cells[x][y].isValid()) cell.setBackground(Color.lightGray);
				if(cells[x][y].getStatus() == Cell.OCCUPIED){
					/* Show player number as indication/which player */
					JLabel playerText;
					if(cells[x][y].getOccupant() == 0) playerText = new JLabel("", monsterIcon, SwingConstants.CENTER); // monster
					else playerText = new JLabel(cells[x][y].toString(), playerIcon, SwingConstants.CENTER);
					playerText.setFont(fontNormal);
					cell.add(playerText);
				}
				gameBoard.add(cell);
			}
		}

		windowSize = 500 * gameMap.getGridSquareSize() / 9;
	}

	/* Set the reset counter shown in the heads up display panel */
	public void setResetsLeft(int resetsLeft){
		this.resetsLeft = resetsLeft;
		drawHUD(); // refresh the heads up display panel
	}

	/* Handy utility to wrap text in HTML - used for making text automatically wrap around */
	private String wrapped(String text){
		return "<html><body style='width=100%'>" + text + "</html>";
	}

	public void forceClose(){
		/* Usually for autonomic threads such as Monster */
		closed = true;
	}

	public void showLoginWindow(User previousValues){
		isUserDataAvailable = false;
		new Master(this, previousValues);
	}

	public boolean isUserDataAvailable(){
		return isUserDataAvailable;
	}

	public void setUserData(User user){
		this.userDetails = user;
		isUserDataAvailable = true;
	}

}
