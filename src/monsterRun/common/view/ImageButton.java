package monsterRun.common.view;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	private static final long serialVersionUID = -918877310536623433L;

	public ImageButton() {
		super();
		this.removeFormatting();
	}

	public ImageButton(Action a) {
		super(a);
		this.removeFormatting();
	}

	public ImageButton(Icon icon) {
		super(icon);
		this.removeFormatting();
	}

	public ImageButton(String text, Icon icon) {
		super(text, icon);
		this.removeFormatting();
	}

	public ImageButton(String text) {
		super(text);
		this.removeFormatting();
	}

	private void removeFormatting() {
		this.setOpaque(false);
		this.setBorder(null);
		this.setContentAreaFilled(false);
	}
}
