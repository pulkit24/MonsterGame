package monsterRun.server.model.utilities;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import monsterRun.common.model.jevents.JEvent;
import monsterRun.server.model.events.ISysoutMessageCatched;

/**
 *
 * Class that catches the elements to be output to the console so that they can
 * be output to the server GUI instead
 * 
 */
public class SysoutCatcher {
	/**
	 * Event object that should get fired everytime a sysout is caught
	 */
	public static final JEvent<ISysoutMessageCatched> sysoutCatched = JEvent
			.create(ISysoutMessageCatched.class);

	private SysoutCatcher() {
	}

	// Overriding the default write methods of OutputStream
	static {

		OutputStream out = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				String message = String.valueOf((char) b);
				sysoutCatched(message);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				String message = new String(b, off, len);
				sysoutCatched(message);
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
	}

	/**
	 * Fire event when a sysout is caught
	 * 
	 * @param message
	 *            this is the string to be shown on the server GUI
	 */
	private static void sysoutCatched(String message) {
		sysoutCatched.get().sysoutCatched(message);
	}

}
