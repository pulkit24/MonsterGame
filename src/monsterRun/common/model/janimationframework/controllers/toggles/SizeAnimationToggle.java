package monsterRun.common.model.janimationframework.controllers.toggles;

import java.awt.Component;

import monsterRun.common.model.janimationframework.algorithms.CubicCalculator;
import monsterRun.common.model.janimationframework.controllers.SizeAnimationSequence;
import monsterRun.common.model.janimationframework.implementation.AnimationManager;
import monsterRun.common.model.janimationframework.implementation.IAnimationStatus;

public class SizeAnimationToggle implements IAnimationStatus {

	private boolean expanding;
	private boolean shrinking;

	private AnimationManager shrinkAnimManager;
	private AnimationManager expandAnimManager;

	private double time;

	private int maxWidth;
	private int maxHeight;

	private int minWidth;
	private int minHeight;

	private Component comp;

	private boolean animateWidth;
	private boolean animateHeight;

	public SizeAnimationToggle(Component comp, double time, int minWidth,
			int minHeight, int maxWidth, int maxHeight, boolean animateWidth,
			boolean animateHeight) {
		super();

		this.comp = comp;

		this.time = time;

		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;

		this.minWidth = minWidth;
		this.minHeight = minHeight;

		this.animateWidth = animateWidth;
		this.animateHeight = animateHeight;

		this.expanding = true;
		this.shrinking = false;
	}

	private AnimationManager createAnimationManager(AnimationManager manager,
			int width, int height) {
		if (manager == null) {
			manager = new AnimationManager();
			SizeAnimationSequence seq = new SizeAnimationSequence(comp, time,
					width, height, animateWidth, animateHeight,
					new CubicCalculator());
			manager.add(seq);

			manager.animationStatusChanged.addListener(this);
		}

		return manager;
	}

	public void toggle() {
		if (expanding) {
			if (expandAnimManager != null) {
				expandAnimManager.forceStop();
			}

			expanding = false;

			shrinkAnimManager = createAnimationManager(shrinkAnimManager,
					minWidth, minHeight);

			shrinkAnimManager.start();

			shrinking = true;
		} else if (shrinking) {
			if (shrinkAnimManager != null) {
				shrinkAnimManager.forceStop();
			}

			shrinking = false;

			expandAnimManager = createAnimationManager(expandAnimManager,
					maxWidth, maxHeight);

			expandAnimManager.start();

			expanding = true;
		}
	}

	public boolean isExpanded() {
		return expanding;
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
}
