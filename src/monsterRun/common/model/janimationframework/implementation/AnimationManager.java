package monsterRun.common.model.janimationframework.implementation;

import java.util.ArrayList;

import monsterRun.common.model.jevents.JEvent;

public class AnimationManager implements IAnimationStatus {

	private AnimationThread animationThread;
	private ArrayList<AnimationSequence> animationSequences;

	public final JEvent<IAnimationStatus> animationStatusChanged = JEvent
			.create(IAnimationStatus.class);

	private IAnimationStatus animStatusListener = animationStatusChanged.get();

	public AnimationManager() {
		this.animationSequences = new ArrayList<AnimationSequence>();
	}

	public AnimationManager(AnimationSequence animationSequence) {
		this();
		this.add(animationSequence);
	}

	public void add(AnimationSequence sequence, double waitTime) {
		sequence.setWaitTime(waitTime);
		this.add(sequence);
	}

	public void add(AnimationSequence sequence) {
		if (!animationSequences.contains(sequence)) {
			this.animationSequences.add(sequence);
		}
	}

	public void remove(AnimationSequence sequence) {
		if (animationSequences.contains(sequence)) {
			animationSequences.remove(sequence);
		}
	}

	public boolean isRunning() {
		return !animationThread.hasStopped();
	}

	public void start() {
		synchronized (this) {
			if (animationThread != null) {
				if (!animationThread.hasStopped()) {
					return;
				}

				animationThread.animationStatusChanged.removeListener(this);
			}

			animationThread = new AnimationThread(animationSequences);
			animationThread.animationStatusChanged.addListener(this);
			animationThread.start();
		}
	}

	public void stop() {
		synchronized (this) {
			if (animationThread != null) {
				animationThread.stopThread();
			}
		}
	}

	public void forceStop() {
		synchronized (this) {
			if (animationThread != null) {
				animationThread.forceStopThread();
			}
		}
	}

	@Override
	public void started() {
		animStatusListener.started();
	}

	@Override
	public void finished() {
		animStatusListener.finished();
	}

	@Override
	public void stopped() {
		animStatusListener.stopped();
	}
}
