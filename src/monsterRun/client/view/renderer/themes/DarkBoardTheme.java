package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public class DarkBoardTheme extends AbstractBoardTheme {

	@Override
	public String getName() {
		return "Dark";
	}
	
	@Override
	public Color getGridLineColor() {
		return new Color(42, 83, 126);
	}

	@Override
	public Color getValidCellColor() {
		return Color.BLACK;
	}

	@Override
	public Color getInvalidCellColor() {
		return new Color(21, 82, 100);
	}

	@Override
	public Color getInValidShadeCellColor() {
		return new Color(36, 146, 177);
	}
}
