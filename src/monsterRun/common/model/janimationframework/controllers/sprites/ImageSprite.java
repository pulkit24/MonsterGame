package monsterRun.common.model.janimationframework.controllers.sprites;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * 
 * Manages a GIF file and stores all the frames. Returns appropriate frame based
 * on the frame rate
 */
public class ImageSprite {

	private ArrayList<Image> sprites;

	private int lastSpriteIndex;
	private double millisPerSpriteChange;
	private double lastSpriteChangeMillis;

	/**
	 * @param framesPerSecond
	 *            The frequency at which the frames should be returned
	 */
	public ImageSprite(double framesPerSecond) {
		lastSpriteIndex = 0;
		millisPerSpriteChange = 1000.00 / framesPerSecond;
		lastSpriteChangeMillis = System.currentTimeMillis();

		sprites = new ArrayList<Image>();
	}

	/**
	 * Returns the appropriate GIF frame based on the frame rate and the last
	 * frame fetching time.
	 * 
	 * @return
	 */
	public Image getCalculatedSprite() {
		if (sprites == null || sprites.size() == 0) {
			return null;
		}

		double delta = System.currentTimeMillis() - lastSpriteChangeMillis;
		int indexChange = (int) (delta / millisPerSpriteChange);
		int nextIndex = lastSpriteIndex + indexChange;

		if (nextIndex != lastSpriteIndex) {
			if (nextIndex >= sprites.size()) {
				nextIndex = 0;
			}

			lastSpriteChangeMillis = System.currentTimeMillis();
			lastSpriteIndex = nextIndex;
		}

		return sprites.get(lastSpriteIndex);
	}

	public int getFramesCount() {
		return sprites.size();
	}

	public Image getFrame(int frame) {
		if (frame >= getFramesCount()) {
			frame = frame % getFramesCount();
		}

		return sprites.get(frame);
	}

	public void addSprite(Image sprite) {
		sprites.add(sprite);
	}

	/**
	 * Loads a GIF image from file and stores the individual frames in an
	 * {@link ArrayList}
	 * 
	 * @param strean
	 * @throws IOException
	 */
	public void loadFromGif(InputStream strean) throws IOException {

		ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
		ImageInputStream in = ImageIO.createImageInputStream(strean);
		reader.setInput(in);

		for (int i = 0, count = reader.getNumImages(true); i < count; i++) {
			BufferedImage image = reader.read(i);
			addSprite(image);
		}

		in.close();
		reader.dispose();
	}
}
