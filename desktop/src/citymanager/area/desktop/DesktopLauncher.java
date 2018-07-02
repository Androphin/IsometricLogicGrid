package citymanager.area.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import citymanager.area.Init;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.width = Init.screenEmulator.getWidth();
//		config.height = Init.screenEmulator.getHeight();
		config.width = Init.SCREEN_WIDTH;
		config.height = Init.SCREEN_HEIGHT;
		new LwjglApplication(new Init(), config);
	}
}
