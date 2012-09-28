package monsterRun.client.view.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import monsterRun.client.model.Facade;
import monsterRun.client.model.FramesPerSecondTimer;
import monsterRun.client.model.GameBoard;
import monsterRun.client.model.entities.AbstractEntity;
import monsterRun.client.model.entities.PlayerEntity;
import monsterRun.client.model.events.IThemeChanged;
import monsterRun.client.view.renderer.themes.AbstractBoardTheme;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.view.G2dUtilities;

/**
 * 
 * The panel that renders the game board
 */
public class RendererPanel extends JPanel implements Runnable, IThemeChanged {
	private static final long serialVersionUID = -3089243469563077150L;

	// The width/height of the cells
	private int unit;

	// The x and y of the boards top left corner
	private int x;
	private int y;

	// Width and height of the renderer panel
	private int width;
	private int height;

	// Total number of rows/columns on the board
	private int rows;
	private int columns;

	// The length/breadth of the rendered board
	private int length;
	private int breadth;

	// The renderer thread
	private Thread rendererThread;

	private boolean pauseRendering;
	private boolean displayDebugInfo;

	private FramesPerSecondTimer framesPerSecTimer;

	private boolean themeChanged;
	private AbstractBoardTheme theme;// Current theme

	// The board renderer as an image for reuse for faster rendering
	private BufferedImage lastRenderedBoard;

	private GameBoard gameBoard;

	public RendererPanel(Facade facade) {
		theme = facade.getTheme();
		gameBoard = facade.getGameBoard();
		themeChanged = false;
		pauseRendering = false;
		displayDebugInfo = true;

		setOpaque(false);
		setIgnoreRepaint(true);

		framesPerSecTimer = new FramesPerSecondTimer();

		facade.themeChanged.addListener(this);

		// The renderer thread
		rendererThread = new Thread(this);
		rendererThread.setPriority(Thread.MIN_PRIORITY);
		rendererThread.setDaemon(true);
		rendererThread.start();
	}

	public void pauseRendering(boolean pause) {
		this.pauseRendering = pause;
	}

	@Override
	public void themeChanged(AbstractBoardTheme theme) {
		if (this.theme != theme) {
			this.theme = theme;
			this.themeChanged = true;
			repaint();
		}
	}

	@Override
	public void run() {
		// The renderer loop
		while (true) {

			if (!pauseRendering) {
				repaint();
			}

			// Wait to avoid high CPU usage.
			try {
				// Default 15
				Thread.sleep(20);
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g2) {
		if (pauseRendering) {
			return;
		}

		Graphics2D g = G2dUtilities.turnOnAntialiasing(g2);
		super.paintComponent(g);

		this.calculateRequiredVariables();

		// Game board rendering
		{
			// Redraw board only if window was resized since last render
			if (lastRenderedBoard == null
					|| lastRenderedBoard.getWidth() != width
					|| lastRenderedBoard.getHeight() != height || themeChanged) {

				if (themeChanged) {
					themeChanged = false;
				}

				reDrawGameBoard();
			}

			// Draw the board graphics
			g.drawImage(lastRenderedBoard, 0, 0, width, height, null);
		}

		// Draw players and monsters
		{
			drawSprite(g, gameBoard.getCurrentPlayer());

			List<AbstractEntity> entities = gameBoard.getAllEntities();

			for (int i = 0; i < entities.size(); i++) {
				drawSprite(g, entities.get(i));
			}
		}

		// Display frames per second
		{
			if (displayDebugInfo) {
				g.setColor(G2dUtilities.invertColor(theme.getInvalidCellColor()));
				g.drawString(framesPerSecTimer.toString(), width - 80,
						height - 1);
			}
		}

		// Increase render count
		framesPerSecTimer.increaseRenderCount();
	}

	private void drawSprite(Graphics2D g, AbstractEntity e) {

		if (e == null) {
			return;
		}

		int wh = unit - (unit / 4);
		int y = this.calculateCellY(e.getPosition().getRow());
		int x = this.calculateCellX(e.getPosition().getColumn());

		x = x + (unit + 4) / 8;
		y = y + (unit + 4) / 8;

		double angle = 0;

		switch (e.getDirection()) {
		case UP:
			angle = -90;
			break;
		case DOWN:
			angle = 90;
			break;
		case LEFT:
			angle = 180;
			break;
		case RIGHT:
			angle = 0;
			break;
		default:
			angle = 0;
			break;
		}

		Image sprite = e.getCalculatedSprite();
		if (sprite != null) {

			if (e instanceof PlayerEntity) {
				sprite = G2dUtilities.rotateImage(sprite, angle);
			} else {
				if (e.getDirection() == Direction.LEFT) {
					sprite = G2dUtilities.flipImage(sprite);
				}
			}

			g.drawImage(sprite, x, y, wh, wh, null);
		}
	}

	public void redrawBoard() {
		calculateRequiredVariables();
		reDrawGameBoard();
	}

	/**
	 * Draws the game board background and saves it as an {@link Image}
	 */
	private void reDrawGameBoard() {

		if (width <= 0 || height <= 0) {
			return;
		}

		lastRenderedBoard = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = G2dUtilities.turnOnAntialiasing(lastRenderedBoard
				.createGraphics());

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {

				Color colur = gameBoard.isValidCell(r, c) ? theme
						.getValidCellColor() : theme.getInvalidCellColor();

				int x = this.calculateCellX(c);
				int y = this.calculateCellY(r);

				g.setColor(colur);
				g.fillRect(x, y, unit, unit);

				// Draw shades
				{
					g.setColor(theme.getInValidShadeCellColor());

					if (!gameBoard.isValidCell(r, c)) {

						int sw = unit / 6;

						if (gameBoard.isValidCell(r - 1, c - 1)) {
							g.fillRect(x, y, sw, sw);
						}

						if (gameBoard.isValidCell(r - 1, c)) {
							g.fillRect(x, y, unit, sw);
						}

						if (gameBoard.isValidCell(r, c - 1)) {
							g.fillRect(x, y, sw, unit);
						}
					}
				}

				// Draw dividing lines
				{
					g.setColor(theme.getGridLineColor());

					if (gameBoard.isValidCell(r, c)) {
						g.drawRect(x, y, unit, unit);
					} else {
						if (gameBoard.isValidCell(r, c - 1)) {
							g.drawLine(x, y, x, y + unit);
						}
						if (gameBoard.isValidCell(r - 1, c)) {
							g.drawLine(x, y, x + unit, y);
						}
					}
				}

			}
		}

		g.dispose();
	}

	private int calculateCellX(int column) {
		return x + (column * unit);
	}

	private int calculateCellY(int row) {
		return y + (row * unit);
	}

	/**
	 * Calculates all the required variables needed for rendering based on the
	 * size of the {@link RendererPanel}. Makes sure that the board keeps it's
	 * ratio.
	 */
	private void calculateRequiredVariables() {
		width = this.getWidth();
		height = this.getHeight();

		rows = gameBoard.getRows();
		columns = gameBoard.getColumns();

		if (columns > rows) {
			int xu = width / columns;
			unit = (xu * rows) > height ? height / rows : xu;
		} else {
			int yu = height / rows;
			unit = (yu * columns) > width ? width / columns : yu;
		}

		length = unit * rows;
		breadth = unit * columns;

		x = (width - breadth) / 2;
		y = (height - length) / 2;
	}
}
