package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public class BlueBoardTheme extends AbstractBoardTheme {

	@Override
	public String getName() {
		return "Blue";
	}

	@Override
	public Color getGridLineColor() {
		return new Color(7, 62, 105);
	}

	@Override
	public Color getValidCellColor() {
		return new Color(17, 130, 221);
	}

	@Override
	public Color getInvalidCellColor() {
		return new Color(180, 183, 244);
	}

	@Override
	public Color getInValidShadeCellColor() {
		return new Color(69, 126, 126);
	}
}
