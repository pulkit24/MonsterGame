package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public class RedBoardTheme extends AbstractBoardTheme {

	@Override
	public String getName() {
		return "Monster";
	}

	@Override
	public Color getGridLineColor() {
		return Color.RED;
	}

	@Override
	public Color getValidCellColor() {
		return Color.BLACK;
	}

	@Override
	public Color getInvalidCellColor() {
		return new Color(250, 11, 17);
	}

	@Override
	public Color getInValidShadeCellColor() {
		return new Color(220, 102, 102);
	}
}
