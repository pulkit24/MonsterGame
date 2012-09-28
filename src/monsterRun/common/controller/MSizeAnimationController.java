package monsterRun.common.controller;

import java.awt.Component;

import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.common.model.janimationframework.controllers.toggles.SizeAnimationToggle;
import monsterRun.common.model.janimationframework.implementation.IAnimationStatus;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.view.Preference;

/**
 *
 * Extends the SizeAnimationToggle in the animation framework to add frame
 * repainting when animation finishes.
 */
public class MSizeAnimationController extends SizeAnimationToggle {

	private RendererPanel renderer;

	public final JEvent<IAnimationStatus> animationStatusChanged = JEvent
			.create(IAnimationStatus.class);

	public MSizeAnimationController(RendererPanel renderer, Component comp,
			double time, int minWidth, int minHeight, int maxWidth,
			int maxHeight, boolean animateWidth, boolean animateHeight) {

		super(comp, time, minWidth, minHeight, maxWidth, maxHeight,
				animateWidth, animateHeight);

		this.renderer = renderer;
	}

	@Override
	public void started() {
		super.started();

		if (renderer != null) {
			renderer.pauseRendering(true);
		}

		animationStatusChanged.get().started();
	}

	@Override
	public void finished() {
		super.finished();

		if (renderer != null) {
			renderer.pauseRendering(false);
		}

		Preference.repaintFrame();
		Preference.validateFrame();

		animationStatusChanged.get().finished();
	}

}
