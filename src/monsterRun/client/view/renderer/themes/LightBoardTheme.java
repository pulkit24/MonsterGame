package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public class LightBoardTheme extends AbstractBoardTheme {

	@Override
	public String getName() {
		return "Light";
	}

	@Override
	public Color getGridLineColor() {
		return new Color(150, 0, 0);
	}

	@Override
	public Color getValidCellColor() {
		return Color.WHITE;
	}

	@Override
	public Color getInvalidCellColor() {
		return new Color(238, 138, 136);
	}

	@Override
	public Color getInValidShadeCellColor() {
		return new Color(128, 128, 128);
	}
}
