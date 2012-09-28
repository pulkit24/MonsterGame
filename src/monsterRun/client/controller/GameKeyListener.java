package monsterRun.client.controller;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import monsterRun.client.model.events.IPlayerMoveRequested;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.jevents.JEvent;

/**
 *
 * Manages key input. Listens to arrow key and escape key presses and clamps the
 * interval to a specified value
 */
public class GameKeyListener implements KeyListener, KeyEventDispatcher {

	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
	private boolean escape;

	private int lastKey;
	private double initialTime;
	private boolean processedOnce;

	private double moveInterval;

	private boolean enabled;

	/**
	 * Gets fired when the player presses an arrow key
	 */
	public final JEvent<IPlayerMoveRequested> moveRequested = JEvent
			.create(IPlayerMoveRequested.class);

	public GameKeyListener(double moveInterval) {
		turnOffAllPresses();

		this.lastKey = 0;

		this.processedOnce = false;
		this.moveInterval = moveInterval;

		this.initialTime = System.currentTimeMillis();
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (!enabled) {
			return false;
		}

		if (e.getID() == KeyEvent.KEY_PRESSED) {
			keyPressed(e);
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
			keyReleased(e);
		}

		return false;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (lastKey != e.getKeyCode()) {
			processedOnce = false;
		}

		switchKeyBooleans(e, true);

		if (!processedOnce) {
			move();
			resetTimer();
			processedOnce = true;
		}

		// Fires the move event only if the interval time has passed
		if (hasMoveIntervalElapsed()) {
			move();
			resetTimer();
		}

		lastKey = e.getKeyCode();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switchKeyBooleans(e, false);
		processedOnce = false;
	}

	private void move() {

		Direction direction = null;

		if (up) {
			direction = Direction.UP;
		} else if (down) {
			direction = Direction.DOWN;
		} else if (left) {
			direction = Direction.LEFT;
		} else if (right) {
			direction = Direction.RIGHT;
		} else if (escape) {
			direction = Direction.CENTER;
		}

		moveRequested.get().moveRequested(direction);
	}

	private void switchKeyBooleans(KeyEvent e, boolean switchValue) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			turnOffAllPresses();
			up = switchValue;
			break;
		case KeyEvent.VK_DOWN:
			turnOffAllPresses();
			down = switchValue;
			break;
		case KeyEvent.VK_LEFT:
			turnOffAllPresses();
			left = switchValue;
			break;
		case KeyEvent.VK_RIGHT:
			turnOffAllPresses();
			right = switchValue;
			break;
		case KeyEvent.VK_ESCAPE:
			turnOffAllPresses();
			escape = switchValue;
			break;
		default:
			break;
		}
	}

	private void resetTimer() {
		initialTime = System.currentTimeMillis();
	}

	private boolean hasMoveIntervalElapsed() {
		return ((System.currentTimeMillis() - initialTime) >= moveInterval);
	}

	private void turnOffAllPresses() {
		up = false;
		down = false;
		left = false;
		right = false;
		escape = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
