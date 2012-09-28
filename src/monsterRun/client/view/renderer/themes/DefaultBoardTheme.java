package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public class DefaultBoardTheme extends AbstractBoardTheme {

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public Color getGridLineColor() {
		return Color.WHITE;
	}

	@Override
	public Color getValidCellColor() {
		return Color.BLACK;
	}

	@Override
	public Color getInvalidCellColor() {
		return Color.LIGHT_GRAY;
	}

	@Override
	public Color getInValidShadeCellColor() {
		return Color.GRAY;
	}
}
