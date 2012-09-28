package monsterRun.server.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

import monsterRun.common.model.Constants;
import monsterRun.common.view.Preference;
import monsterRun.server.model.MonsterRunServer;
import monsterRun.server.model.events.IServerStatusChangedListener;
import monsterRun.server.model.events.ISysoutMessageCatched;
import monsterRun.server.model.utilities.SysoutCatcher;

/**
 * Frame for the server GUI
 * 
 */
public class MonsterServerFrame extends JFrame implements
		IServerStatusChangedListener, ISysoutMessageCatched {

	private static final long serialVersionUID = -1907425271677938346L;

	private MonsterRunServer server;

	private JPanel container;

	private Toolbar toolbar;
	private JLabel statusBar;

	private JTextArea display;
	private JScrollPane scrollPane;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 *             this is thrown when there is a problem with the server
	 *             startup
	 */
	public MonsterServerFrame() throws Exception {
		Preference.initialize(this);

		server = new MonsterRunServer(Constants.PORT);
		// Listen to the event that gets fired when server state changes
		server.serverStatusChanged.addListener(this);
		// Listen to the event that gets fired when something is to be
		// printed to the console or in this case the server GUI
		SysoutCatcher.sysoutCatched.addListener(this);

		container = (JPanel) getContentPane();
		container.setLayout(new BorderLayout());

		container.setBackground(Color.WHITE);

		toolbar = new Toolbar(server);
		container.add(toolbar, BorderLayout.NORTH);

		Font font = new Font("Monospaced", Font.PLAIN, 12);

		display = new JTextArea();
		display.setFont(font);
		display.setEditable(false);

		scrollPane = new JScrollPane(display);
		scrollPane.setBorder(null);
		container.add(scrollPane, BorderLayout.CENTER);

		statusBar = new JLabel("Not Started");
		statusBar.setOpaque(true);
		statusBar.setBackground(Preference.HEADER_BACKGROUND);
		statusBar.setForeground(Preference.HEADER_FOREGROUND);

		container.add(statusBar, BorderLayout.SOUTH);

		display.setText("Click the \"Stop Server\" button on the toolbar to stop the Game Server\n\n");
		// Start server
		server.startServer();
	}

	/**
	 * Prints the output to be sent to console, on the server GUI
	 * 
	 * @param message
	 *            the string to be printed on the server GUI
	 */
	@Override
	public void sysoutCatched(String message) {
		try {
			Document doc = display.getDocument();
			doc.insertString(doc.getLength(), message, null);
			display.setCaretPosition(doc.getLength() - 1);
		} catch (Exception ex) {
		}
	}

	/**
	 * Method fired when server starts - it changes the text on the toolbar
	 */
	@Override
	public void serverStarted() {
		statusBar.setText("Server Started");
	}

	/**
	 * Method fired when server stops - it changes the text on the toolbar
	 */
	@Override
	public void serverStopped() {
		statusBar.setText("Server Stopped");
	}
}
