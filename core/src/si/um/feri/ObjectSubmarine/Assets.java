package si.um.feri.ObjectSubmarine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    public static final int SPEED = 600;    // pixels per second
    public static final int SPEED_SHELL = 200; // pixels per second
    public static int TOP_SPEED_SHARK = 150;
    public static int LOW_SPEED_SHARK = 50;
    public static final long CREATE_SHELL_TIME = 1000000000;    // ns
    public static long CREATE_SHARK_TIME = 2000000000;// ns
   // public static final long FIRE_TORPEDO_TIME = 2000000000;
    public static final long SHARK_SOUND_LENGTH = 1000000000;
    public static long POWER_UP_TIME =  2000000000;
    public static long POWER_UP_LENGTH =  2000000000;

    public static Boolean pause = false;

    static Texture shellImage;
    static Texture subImage;
    static Texture sharkImage;
    static Texture bgImage;
    static Texture torpedoImage;
    static Texture ammoImage;
    static Sound shellSound;
    static Sound hitSound;
    static Sound sharkSound;

    public static void load(){
        subImage = new Texture(Gdx.files.internal("sub.png"));
        shellImage = new Texture(Gdx.files.internal("shell.png"));
        sharkImage = new Texture(Gdx.files.internal("shark.png"));
        shellSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
        bgImage = new Texture(Gdx.files.internal("sea.png"));
        torpedoImage = new Texture(Gdx.files.internal("torpedo.png"));
        sharkSound = Gdx.audio.newSound(Gdx.files.internal("eat.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        ammoImage = new Texture(Gdx.files.internal("ammo.png"));
    }

    public static void dispose() {
        shellImage.dispose();
        sharkImage.dispose();
        subImage.dispose();
        shellSound.dispose();
        bgImage.dispose();
        torpedoImage.dispose();
        ammoImage.dispose();
    }
}
