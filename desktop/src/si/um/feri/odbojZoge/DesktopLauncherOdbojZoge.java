package si.um.feri.odbojZoge;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import si.um.feri.odbojZoge.OdbojZoge;

public class DesktopLauncherOdbojZoge {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("submarine-game");
        new Lwjgl3Application(new OdbojZoge(), config);
    }
}
