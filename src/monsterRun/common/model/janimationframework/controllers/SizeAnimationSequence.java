package monsterRun.common.model.janimationframework.controllers;

import java.awt.Component;
import java.awt.Dimension;

import monsterRun.common.model.janimationframework.algorithms.ICalculator;
import monsterRun.common.model.janimationframework.implementation.AnimationSequence;

/**
 * 
 * {@link AnimationSequence} that implements size animation of an object
 */
public class SizeAnimationSequence extends AnimationSequence {

	private Component comp;

	private double finalWidth;
	private double finalHeight;

	private boolean animateWidth;
	private boolean animateHeight;

	private double initialWidth;
	private double initialHeight;

	public SizeAnimationSequence(Component comp, double time,
			double finalWidth, double finalHeight, boolean animateWidth,
			boolean animateHeight, ICalculator iCalculator) {

		super(time, iCalculator);

		this.comp = comp;

		this.finalWidth = finalWidth;
		this.finalHeight = finalHeight;

		this.animateWidth = animateWidth;
		this.animateHeight = animateHeight;
	}

	@Override
	public void started() {
		super.started();

		this.initialWidth = comp.getWidth();
		this.initialHeight = comp.getHeight();
	}

	@Override
	public void finished() {
		super.finished();

		int width = (int) (animateWidth ? finalWidth : this.initialWidth);
		int height = (int) (animateHeight ? finalHeight : this.initialHeight);

		comp.setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void update(double percentage) {

		int width = (int) initialWidth;
		int height = (int) initialHeight;

		if (animateHeight) {
			height = (int) super.percentageOf(finalHeight - initialHeight,
					percentage);
			height = (int) (initialHeight + height);
		}

		if (animateWidth) {
			width = (int) super.percentageOf(finalWidth - initialWidth,
					percentage);
			width = (int) (initialWidth + width);
		}

		comp.setSize(new Dimension(width, height));

		comp.validate();
		comp.repaint();
	}
}