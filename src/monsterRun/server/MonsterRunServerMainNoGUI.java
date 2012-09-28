package monsterRun.server;

import monsterRun.common.model.Constants;
import monsterRun.server.model.MonsterRunServer;

public class MonsterRunServerMainNoGUI {
	public static void main(String[] args) throws Exception {
		// Create a server without GUI, it is used for testing purposes
		MonsterRunServer server = new MonsterRunServer(Constants.PORT);
		server.startServer();
	}
}
