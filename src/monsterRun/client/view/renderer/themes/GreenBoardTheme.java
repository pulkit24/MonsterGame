package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public class GreenBoardTheme extends AbstractBoardTheme {

	@Override
	public String getName() {
		return "Green";
	}

	@Override
	public Color getGridLineColor() {
		return Color.WHITE;
	}

	@Override
	public Color getValidCellColor() {
		return new Color(5, 31, 14);
	}

	@Override
	public Color getInvalidCellColor() {
		return new Color(26, 138, 62);
	}

	@Override
	public Color getInValidShadeCellColor() {
		return new Color(58, 105, 61);
	}
}
