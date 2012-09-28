package monsterRun.client.view.sidebar;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import monsterRun.client.model.entities.PlayerEntity;
import monsterRun.client.model.events.ILifeCountChanged;
import monsterRun.common.model.ImageStore;
import monsterRun.common.view.Preference;

public class OtherPlayerLabel extends JLabel implements ILifeCountChanged {
	private static final long serialVersionUID = 7337937769868961028L;

	private String killed;
	private PlayerEntity entity;

	public void initialize(PlayerEntity entity) {
		this.entity = entity;

		killed = "";

		setForeground(Preference.FOOTER_FOREGROUND);

		Dimension size = getPreferredSize();

		Image image = ImageStore.get().resizeImage(entity.getSprite(4),
				(int) size.getHeight(), (int) size.getHeight());

		setIcon(new ImageIcon(image));
		updateText();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		entity.lifeCountChanged.addListener(this);
	}

	public void kill() {
		killed = "[Died] ";
		updateText();
	}

	@Override
	public void lifeCountChanged(PlayerEntity player, int count) {
		updateText();
	}

	private void updateText() {
		setText("<html>" + killed + entity.getName() + "<br/>x"
				+ entity.getLives() + "</html>");
	}

	public PlayerEntity getPlayerEntity() {
		return entity;
	}
}
