package monsterRun.client.view.popuppanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import monsterRun.common.view.Preference;

public class InitialLoadPanel extends JPanel {
	private static final long serialVersionUID = -4438115161419326842L;

	public InitialLoadPanel() {
		setLayout(new BorderLayout(0, 0));
		setOpaque(false);

		JLabel lblWaitingForThe = new JLabel("Waiting for the server...");
		lblWaitingForThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblWaitingForThe.setFont(Preference.GLOBAL_FONT.deriveFont(20F));
		lblWaitingForThe.setOpaque(false);
		add(lblWaitingForThe);
	}
}
