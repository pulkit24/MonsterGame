package monsterRun.common.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * 
 * A set of methods and variables that can be used by both the server and client
 * {@link JFrame}s to give consistency to the look and feel.
 */
public class Preference {

	private static JFrame frame;

	public static final Font GLOBAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font FORMS_FONT = GLOBAL_FONT.deriveFont(18.0F);

	public static final Color HEADER_BACKGROUND = new Color(0, 153, 255);
	public static final Color HEADER_FOREGROUND = new Color(255, 255, 255);

	public static final Color HEADER_BACKGROUND_DARK = new Color(5, 146, 240);
	public static final Color HEADER_BACKGROUND_DARKER = new Color(7, 137, 224);

	public static final Color FOOTER_BACKGROUND = new Color(220, 20, 60);
	public static final Color FOOTER_FOREGROUND = HEADER_FOREGROUND;

	public static final Color FOOTER_BACKGROUND_DARK = new Color(200, 5, 45);

	private static boolean isFullscreen;

	public synchronized static void initialize(JFrame aFrame) {
		if (frame == null) {
			frame = aFrame;
			setUIFont(new FontUIResource(GLOBAL_FONT));
		}
	}

	public static void requestDefaultButtonFocus(JButton button) {
		frame.getRootPane().setDefaultButton(button);
	}

	public static void requestComponentFocus(Component component) {
		component.requestFocusInWindow();
	}

	public static void repaintFrame() {
		frame.repaint();
	}

	public static void validateFrame() {
		frame.validate();
	}

	public static boolean isFullscreen() {
		return isFullscreen;
	}

	/**
	 * Makes a {@link JFrame} go fullscreen
	 * 
	 * @param flag
	 */
	public static synchronized void setFullscreen(boolean flag) {
		if (flag) {
			frame.setVisible(false);
			frame.dispose();
			frame.setUndecorated(true);
			GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().setFullScreenWindow(frame);
			frame.setVisible(true);

			isFullscreen = true;
		} else {
			frame.setVisible(false);
			frame.dispose();
			frame.setUndecorated(false);
			GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().setFullScreenWindow(null);

			frame.setVisible(true);

			isFullscreen = false;
		}
	}

	/**
	 * Sets the font of all the GUI elements to the default Segoe UI font
	 * 
	 * @param fontResource
	 */
	public static void setUIFont(FontUIResource fontResource) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontResource);
			}
		}

		keys = null;
	}
}
