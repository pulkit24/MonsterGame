package monsterRun.common.model.janimationframework.controllers;

import java.awt.Component;

import monsterRun.common.model.janimationframework.algorithms.ICalculator;
import monsterRun.common.model.janimationframework.implementation.AnimationSequence;

/**
 *
 * {@link AnimationSequence} that implements position animation of an object
 */
public class PositionAnimationSequence extends AnimationSequence {

	private Component comp;

	private double finalX;
	private double finalY;

	private boolean animateX;
	private boolean animateY;

	private double initialX;
	private double initialY;

	public PositionAnimationSequence(Component comp, double time,
			double finalX, double finalY, boolean animateX, boolean animateY,
			ICalculator iCalculator) {
		super(time, iCalculator);

		this.comp = comp;

		this.finalX = finalX;
		this.finalY = finalY;

		this.animateX = animateX;
		this.animateY = animateY;
	}

	@Override
	public void started() {
		super.started();

		this.initialX = comp.getX();
		this.initialY = comp.getY();
	}

	@Override
	public void finished() {
		super.finished();
		update(100);
	}

	@Override
	public void update(double percentage) {

		int x = (int) initialX;
		int y = (int) initialY;

		if (animateX) {
			x = (int) super.percentageOf(finalX - initialX, percentage);

			x = (int) (initialX + x);
		}

		if (animateY) {
			y = (int) super.percentageOf(finalY - initialY, percentage);

			y = (int) (initialY + y);
		}

		if (comp.getX() != x || comp.getY() != y) {
			comp.setLocation(x, y);
		}
	}
}