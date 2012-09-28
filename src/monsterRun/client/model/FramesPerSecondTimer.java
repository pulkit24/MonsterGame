package monsterRun.client.model;

/**
 *
 * Used to calculate frames per second of a thread
 */
public class FramesPerSecondTimer {

	private int framesCount;
	private int framesPerSecond;
	private double lastFrameCountTime;

	public FramesPerSecondTimer() {
		framesCount = 0;
		framesPerSecond = 0;
		lastFrameCountTime = System.currentTimeMillis();
	}

	public void increaseRenderCount() {
		if (System.currentTimeMillis() - lastFrameCountTime >= 1000.0) {

			framesPerSecond = framesCount;

			framesCount = 0;
			lastFrameCountTime = System.currentTimeMillis();
		}

		framesCount++;
	}

	public int getFramesPerSecond() {
		return framesPerSecond;
	}

	@Override
	public String toString() {
		return getFramesPerSecond() + " frames/sec";
	}
}
