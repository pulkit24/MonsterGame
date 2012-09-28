package monsterRun.common.model.janimationframework.implementation;

import java.util.ArrayList;
import java.util.List;

import monsterRun.common.model.jevents.JEvent;

/**
 * 
 * The thread that loops through the {@link AnimationSequence}s and executes
 * them
 */
public class AnimationThread extends Thread {

	private boolean stopped;
	private boolean forceStopped;

	private List<AnimationSequence> animationSequences;

	public final JEvent<IAnimationStatus> animationStatusChanged = JEvent
			.create(IAnimationStatus.class);

	private IAnimationStatus animStatusListener = animationStatusChanged.get();

	private AnimationThread() {
		this.setDaemon(true);

		this.stopped = false;
		this.forceStopped = false;

		this.setPriority(MAX_PRIORITY);
	}

	public AnimationThread(ArrayList<AnimationSequence> animationSequences) {
		this();
		this.animationSequences = animationSequences;
	}

	public void forceStopThread() {
		forceStopped = true;
	}

	public void stopThread() {
		if (!stopped) {
			stopped = true;
			animStatusListener.finished();
		}
	}

	public boolean hasStopped() {
		return stopped || forceStopped;
	}

	@Override
	public void run() {

		animStatusListener.started();

		int enabledAnimations = 0;

		for (AnimationSequence sequence : animationSequences) {
			if (sequence.isEnabled()) {
				enabledAnimations += 1;
				sequence.started();
			}
		}

		double initialStartTime = System.currentTimeMillis();

		double percent;
		double currentTime;
		double timeDifference;

		while ((!stopped) && (!forceStopped) && (enabledAnimations > 0)) {

			for (AnimationSequence sequence : animationSequences) {
				if (sequence.isEnabled()) {

					currentTime = System.currentTimeMillis();

					if (sequence.hasStarted()) {
						timeDifference = currentTime - sequence.getStartTime();
					} else {
						timeDifference = currentTime - initialStartTime;

						if (timeDifference >= sequence.getWaitTime()) {
							sequence.setStartTime(currentTime);
							sequence.setStarted();
						} else {
							continue;
						}
					}

					// Calculates what percentage of time has elapsed since the
					// animation started
					percent = sequence.getAnimationProvider()
							.getCalculatedTime(sequence.getTime(),
									timeDifference);

					if (timeDifference >= sequence.getTime()) {
						enabledAnimations -= 1;
						sequence.setEnabled(false);
						sequence.finished();
						continue;
					}

					if (sequence.getLastUpdatedPercentage() != percent) {
						sequence.update(percent);
						sequence.setLastUpdatedPercentage(percent);
					}
				}
			}

			// Sleeps to avoid resource overuse
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}

		if (stopped) {
			for (AnimationSequence sequence : animationSequences) {
				if (sequence.isEnabled()) {
					sequence.update(100.00);
					sequence.finished();
				}
			}

			animStatusListener.finished();
		} else if (forceStopped) {
			animStatusListener.stopped();
		} else {
			animStatusListener.finished();
		}

		for (AnimationSequence sequence : animationSequences) {
			sequence.reset();
		}

		if (!hasStopped()) {
			stopThread();
		}
	}
}
