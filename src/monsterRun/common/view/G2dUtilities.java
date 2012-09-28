package monsterRun.common.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class G2dUtilities {

	/**
	 * Turns the Anti-aliasing on/off for a {@link Graphics2D}
	 * 
	 * @param g
	 * @return
	 */
	public static Graphics2D turnOnAntialiasing(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		return g2;
	}

	/**
	 * Rotates an image on the specified angle
	 * 
	 * @param image
	 * @param angle
	 * @return
	 */
	public static Image rotateImage(Image image, double angle) {

		double radian = angle * (Math.PI / 180);

		BufferedImage playerBuff = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		playerBuff.getGraphics().drawImage(image, 0, 0, null);

		AffineTransform at = new AffineTransform();
		at.rotate(radian, playerBuff.getWidth() / 2, playerBuff.getHeight() / 2);

		BufferedImageOp bio = new AffineTransformOp(at,
				AffineTransformOp.TYPE_BILINEAR);

		Image rotatedImage = bio.filter(playerBuff, null);

		return rotatedImage;
	}

	/**
	 * Horizontally flips an image
	 * 
	 * @param image
	 * @return
	 */
	public static Image flipImage(Image image) {

		BufferedImage playerBuff = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		playerBuff.getGraphics().drawImage(image, 0, 0, null);

		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		Image flippedImage = op.filter(playerBuff, null);

		return flippedImage;
	}

	public static Color invertColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(),
				255 - c.getBlue());
	}
}
