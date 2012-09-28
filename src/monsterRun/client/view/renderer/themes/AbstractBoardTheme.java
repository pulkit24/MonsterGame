package monsterRun.client.view.renderer.themes;

import java.awt.Color;

public abstract class AbstractBoardTheme {

	@Override
	public String toString() {
		return this.getName() + " Theme";
	}

	public abstract String getName();

	public abstract Color getGridLineColor();

	public abstract Color getValidCellColor();

	public abstract Color getInvalidCellColor();

	public abstract Color getInValidShadeCellColor();
}
