package monsterRun.common.model.janimationframework.implementation;

import monsterRun.common.model.janimationframework.algorithms.ICalculator;
import monsterRun.common.model.janimationframework.algorithms.LinearCalculator;

public abstract class AnimationSequence implements IAnimationStatus {

	private boolean enabled;
	private boolean started;

	private double waitTime;
	private double startTime;
	private double animationTime;
	private double lastUpdatedPercentage;

	private ICalculator animationProvider;

	public AnimationSequence(double animationTime) {
		this(animationTime, new LinearCalculator());
	}

	public AnimationSequence(double animationTime, ICalculator animationProvider) {
		this.enabled = true;
		this.started = false;

		this.waitTime = 0.00;
		this.startTime = 0.00;
		this.lastUpdatedPercentage = 0.00;
		this.animationTime = animationTime;

		this.animationProvider = animationProvider;
	}

	public void reset() {
		this.enabled = true;
		this.started = false;

		this.startTime = 0.00;
		this.lastUpdatedPercentage = 0.00;
	}

	public double percentageOf(double value, double percentage) {
		return (value / 100.00) * percentage;
	}

	public void setLastUpdatedPercentage(double percentage) {
		this.lastUpdatedPercentage = percentage;
	}

	public double getLastUpdatedPercentage() {
		return lastUpdatedPercentage;
	}

	public ICalculator getAnimationProvider() {
		return animationProvider;
	}

	public void setTime(double milliseconds) {
		this.animationTime = milliseconds;
	}

	public double getTime() {
		return animationTime;
	}

	public void setWaitTime(double time) {
		this.waitTime = time;
	}

	public double getWaitTime() {
		return this.waitTime;
	}

	public void setStartTime(double time) {
		this.startTime = time;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setStarted() {
		this.started = true;
	}

	public boolean hasStarted() {
		return this.started;
	}

	@Override
	public void started() {
	}

	@Override
	public void finished() {
	}

	@Override
	public void stopped() {

	}

	public abstract void update(double percent);

}
