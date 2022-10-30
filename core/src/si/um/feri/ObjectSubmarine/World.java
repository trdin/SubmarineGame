package si.um.feri.ObjectSubmarine;

import static java.sql.DriverManager.println;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class World {
    private Texture shellImage;
    private Texture subImage;
    private Texture sharkImage;
    private Texture bgImage;
    private Texture torpedoImage;
    private Sound shellSound;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    Submarine sub;
    private Array<Shell> shells;    // special LibGDX Array
    private Array<Shark> sharks;
    private Array<Torpedo> torpedos;
    private long lastShellTime;
    private long lastSharkTime;
    private long lastTorpedoTime;
    private int shellsCollectedScore;
    private Sound sharkSound;
    private long lastSharkSoundTime;
    private Sound hitSound;


    public BitmapFont font;


    private static final int SPEED = 600;    // pixels per second
    private static final int SPEED_SHELL = 200; // pixels per second
    private static int TOP_SPEED_SHARK = 150;
    private static int LOW_SPEED_SHARK = 50;
    private static int SPEED_TORPEDO = 300;
    private static final long CREATE_SHELL_TIME = 1000000000;    // ns
    private static long CREATE_SHARK_TIME = 2000000000;// ns
    private static final long FIRE_TORPEDO_TIME = 2000000000;
    private static final long SHARK_SOUND_LENGTH = 1000000000;

    public void create() {
        font = new BitmapFont();
        font.getData().setScale(3);
        shellsCollectedScore = 0;

        subImage = new Texture(Gdx.files.internal("sub.png"));
        shellImage = new Texture(Gdx.files.internal("shell.png"));
        sharkImage = new Texture(Gdx.files.internal("shark.png"));
        shellSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
        bgImage = new Texture(Gdx.files.internal("sea.png"));
        torpedoImage = new Texture(Gdx.files.internal("torpedo.png"));
        sharkSound = Gdx.audio.newSound(Gdx.files.internal("eat.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        sub = new Submarine(Gdx.graphics.getWidth() / 2f, 20, 100, SPEED, subImage);

        shells = new Array<Shell>();
        sharks = new Array<Shark>();
        torpedos = new Array<Torpedo>();
        spawnShell();
        spawnShark();
    }

    public void renderObjects() {
        renderShells();
        renderSharks();
        renderTorpedos();
    }

    public void drawGame() {
        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the rocket, astronauts, asteroids
        batch.begin();
        {    // brackets added just for indent

            sub.draw(batch);
            for (Shark shark : sharks) {
                shark.draw(batch);
            }
            for (Shell shell : shells) {
                shell.draw(batch);
            }
            for (Torpedo torpedo : torpedos) {
                torpedo.draw(batch);
            }
            font.setColor(Color.YELLOW);
            font.draw(batch, "" + shellsCollectedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + sub.getHealth(), 20, Gdx.graphics.getHeight() - 20);

            if (TimeUtils.nanoTime() - lastTorpedoTime > FIRE_TORPEDO_TIME) {
                batch.draw(torpedoImage,Gdx.graphics.getWidth() - 45, Gdx.graphics.getHeight() - 100, torpedoImage.getWidth(), torpedoImage.getHeight());
            }
        }
        batch.end();
    }


    private void renderShells() {
        if (TimeUtils.nanoTime() - lastShellTime > CREATE_SHELL_TIME) spawnShell();

        for (Iterator<Shell> it = shells.iterator(); it.hasNext(); ) {
            Shell shell = it.next();
            shell.update();
            if (shell.outOfScreen()) it.remove();    // from screen
            if (shell.overlaps(sub)) {
                shellSound.play();
                shellsCollectedScore++;
                if (shellsCollectedScore % 10 == 0) {
                    TOP_SPEED_SHARK += 20;
                    LOW_SPEED_SHARK += 10;
                    if (CREATE_SHARK_TIME > 1000000) {
                        CREATE_SHARK_TIME -= 1000000;
                    }

                }    // speeds up
                it.remove();
            }
        }
    }

    private void renderSharks() {
        if (TimeUtils.nanoTime() - lastSharkTime > CREATE_SHARK_TIME) spawnShark();

        for (Iterator<Shark> it = sharks.iterator(); it.hasNext(); ) {
            Shark shark = it.next();
            shark.update();
            if (shark.outOfScreen()) it.remove();
            if (shark.overlaps(sub)) {
                sub.hit();
                if (TimeUtils.nanoTime() - lastSharkSoundTime > SHARK_SOUND_LENGTH) {
                    sharkSound.play();
                    lastSharkSoundTime = TimeUtils.nanoTime();
                }
            }
        }
    }
//TODO fix this in seperate classes
    private void renderTorpedos() {
        for (Iterator<Torpedo> it = torpedos.iterator(); it.hasNext(); ) {
            Torpedo torpedo = it.next();
            torpedo.update();
            if (torpedo.outOfScreen()) {
                it.remove();
                continue;
            }
            ;
            for (Iterator<Shark> itShark = sharks.iterator(); itShark.hasNext(); ) {
                Shark shark = itShark.next();
                if (torpedo.overlaps(shark)) {
                    it.remove();
                    itShark.remove();
                    hitSound.play();
                }
            }
        }
    }

    public void spawnTorpedo() {

        if (TimeUtils.nanoTime() - lastTorpedoTime > FIRE_TORPEDO_TIME) {
            torpedos.add(new Torpedo(sub.x + subImage.getWidth() / 2f, sub.y + subImage.getHeight() / 2f, torpedoImage));
            lastTorpedoTime = TimeUtils.nanoTime();
        }
    }

    public void spawnShell() {
        shells.add(new Shell(shellImage));
        lastShellTime = TimeUtils.nanoTime();
    }

    public void spawnShark() {
        sharks.add(new Shark(MathUtils.random(LOW_SPEED_SHARK, TOP_SPEED_SHARK), sharkImage));
        lastSharkTime = TimeUtils.nanoTime();
    }

    public void drawBg() {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0.9f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bgImage, 0, 0);
        batch.end();
    }

    public void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        sub.x = touchPos.x - subImage.getWidth() / 2f;
    }

    public void dispose() {
        shellImage.dispose();
        sharkImage.dispose();
        subImage.dispose();
        shellSound.dispose();
        bgImage.dispose();
        torpedoImage.dispose();
        batch.dispose();
        font.dispose();
    }

    public void endGame() {
        batch.begin();
        {
            font.setColor(Color.RED);
            font.draw(batch, "The END", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
        }
        batch.end();
    }
}
