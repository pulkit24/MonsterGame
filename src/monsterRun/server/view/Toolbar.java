package monsterRun.server.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;

import monsterRun.common.model.ImageStore;
import monsterRun.common.view.MToolbar;
import monsterRun.common.view.Preference;
import monsterRun.server.model.MonsterRunServer;
import monsterRun.server.model.events.IServerStatusChangedListener;

/**
 * A customized JToolBar implementation that is used for the server
 * 
 */
public class Toolbar extends MToolbar implements IServerStatusChangedListener {
	private static final long serialVersionUID = 7931463715064598198L;

	private JButton btnStartServer;
	private JButton btnFullscreen;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            the instance of the server running the game
	 */
	public Toolbar(final MonsterRunServer server) {
		super(null, Preference.FOOTER_BACKGROUND,
				Preference.FOOTER_BACKGROUND_DARK);
		// Listen to the event that gets fired on server
		// state change
		server.serverStatusChanged.addListener(this);

		btnStartServer = new JButton("Start Server");
		btnStartServer.setOpaque(false);
		btnStartServer.addActionListener(new ActionListener() {
			// Start server if it is not up and stop server if it is up
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!server.isAlive()) {
					try {
						server.startServer();
					} catch (IOException e1) {
						System.out.println("Server could not be started.\n"
								+ e1.getMessage());
					}
				} else {
					try {
						server.stopServer();
					} catch (IOException e1) {
						System.out.println("Server could not be stopped.\n"
								+ e1.getMessage());
					}
				}
			}
		});
		add(btnStartServer);

		btnFullscreen = new JButton(ImageStore.get().getIcon("fullscreen.png"));
		btnFullscreen.setOpaque(false);
		btnFullscreen.setToolTipText("Enter/Exit Fullscreen mode");
		btnFullscreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Preference.setFullscreen(!Preference.isFullscreen());
			}
		});
		add(btnFullscreen);
	}

	/**
	 * Method fired when server starts - it changes the text on the button
	 * showed on the toolbar
	 */
	@Override
	public void serverStarted() {
		btnStartServer.setText("Stop Server");
	}

	/**
	 * Method fired when server stops - it changes the text on the button showed
	 * on the toolbar
	 */
	@Override
	public void serverStopped() {
		btnStartServer.setText("Start Server");
	}
}
