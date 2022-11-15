package si.um.feri.UnitSubmarine;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import si.um.feri.UnitSubmarine.SubmarineGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncherUnit {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle( "World units example");
		config.setWindowedMode(1200, 600);
		/*config.height = 600;
		config.forceExit = false;*/
		new Lwjgl3Application(new SubmarineGame(), config);
	}
}
