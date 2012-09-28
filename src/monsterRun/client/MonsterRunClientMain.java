package monsterRun.client;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import monsterRun.client.view.MonsterFrame;
import monsterRun.common.model.ImageStore;
import monsterRun.common.model.janimationframework.controllers.sprites.ImageSprite;

public class MonsterRunClientMain {
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
		}

		try {

			final MonsterFrame frame = new MonsterFrame();
			frame.setTitle("Monster Run Client");
			frame.setSize(new Dimension(800, 650));
			frame.setMinimumSize(new Dimension(400, 300));
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			new Thread() {
				public void run() {
					try {
						// Load a GIF and randomly picks a frame to be set as
						// the icon
						ImageSprite sp = new ImageSprite(10);
						sp.loadFromGif(ImageStore.get().getImageStream(
								"monster1.gif"));

						int randomFrame = (int) (Math.random()
								* sp.getFramesCount() - 1);

						frame.setIconImage(sp.getFrame(randomFrame));
					} catch (IOException e) {
					}
				}
			}.start();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"The Game could not start :( \n" + e.getLocalizedMessage(),
					"Failed to start the game", JOptionPane.ERROR_MESSAGE);
		}
	}
}