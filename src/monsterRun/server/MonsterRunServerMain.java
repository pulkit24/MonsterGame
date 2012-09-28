package monsterRun.server;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import monsterRun.common.model.ImageStore;
import monsterRun.server.view.MonsterServerFrame;

public class MonsterRunServerMain {
	public static void main(String[] args) {
		// Set the look and feel of the GUI
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
		}

		// Create and customize the frame for the server GUI
		try {
			MonsterServerFrame frame = new MonsterServerFrame();
			frame.setTitle("Monster Run Server");
			frame.setSize(new Dimension(700, 550));
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			frame.setIconImage(ImageStore.get().getIcon("server.png")
					.getImage());

		} catch (Exception e) {
			// Handle exception that is thrown when the server fails to start
			JOptionPane.showMessageDialog(
					null,
					"The Server could not start :( \n"
							+ e.getLocalizedMessage(),
					"Failed to start the server.", JOptionPane.ERROR_MESSAGE);
		}
	}
}
