package monsterRun.common.model;

import java.awt.Image;
import java.io.InputStream;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 *
 * Singleton class that manages image loading and caching
 * 
 */
public class ImageStore {

	private static final String IMAGE_LOCATION = "monsterRun/common/images/";

	private static ImageStore instance = new ImageStore();
	private HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

	private ImageStore() {
	}

	public synchronized static ImageStore get() {
		return instance;
	}

	public InputStream getImageStream(String imageLocalPath) {
		return ImageStore.class.getClassLoader().getResourceAsStream(
				IMAGE_LOCATION + imageLocalPath);
	}

	public ImageIcon getIcon(String imageLocalPath) {
		if (!icons.containsKey(imageLocalPath)) {

			ImageIcon icon = new ImageIcon(ImageStore.class.getClassLoader()
					.getResource(IMAGE_LOCATION + imageLocalPath));

			icons.put(imageLocalPath, icon);

			return icon;
		}

		return icons.get(imageLocalPath);
	}

	public ImageIcon getResizedIcon(String imageLocalPath, int width, int height) {
		return new ImageIcon(getResizedIconImage(imageLocalPath, width, height));
	}

	public Image getResizedIconImage(String imageLocalPath, int width,
			int height) {
		ImageIcon icon = getIcon(imageLocalPath);
		Image img = icon.getImage().getScaledInstance(width, height,
				Image.SCALE_SMOOTH);

		return img;
	}

	public Image resizeImage(Image image, int width, int height) {
		return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
}
