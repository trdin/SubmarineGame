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
    private SpriteBatch batch;
    private OrthographicCamera camera;
    Submarine sub;
    Array<Shell> shells;    // special LibGDX Array
    Array<Shark> sharks;
    Array<Torpedo> torpedos;
    long lastShellTime;
    long lastSharkTime;
    private long lastTorpedoTime;
    int shellsCollectedScore;
    long lastSharkSoundTime;

    public BitmapFont font;


    public void create() {
        font = new BitmapFont();
        font.getData().setScale(3);
        shellsCollectedScore = 0;
        Assets.load();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        sub = new Submarine(Gdx.graphics.getWidth() / 2f, 20, 100, Assets.subImage);

        shells = new Array<Shell>();
        sharks = new Array<Shark>();
        torpedos = new Array<Torpedo>();
        spawnShell();
        spawnShark();
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

            if (TimeUtils.nanoTime() - lastTorpedoTime > Assets.FIRE_TORPEDO_TIME) {
                batch.draw(Assets.torpedoImage,Gdx.graphics.getWidth() - 45, Gdx.graphics.getHeight() - 100, Assets.torpedoImage.getWidth(), Assets.torpedoImage.getHeight());
            }
        }
        batch.end();
    }

    public void spawnTorpedo() {

        if (TimeUtils.nanoTime() - lastTorpedoTime > Assets.FIRE_TORPEDO_TIME) {
            torpedos.add(new Torpedo(sub.x +  Assets.subImage.getWidth() / 2f, sub.y +  Assets.subImage.getHeight() / 2f,  Assets.torpedoImage));
            lastTorpedoTime = TimeUtils.nanoTime();
        }
    }

    public void spawnShell() {
        shells.add(new Shell( Assets.shellImage));
        lastShellTime = TimeUtils.nanoTime();
    }

    public void spawnShark() {
        sharks.add(new Shark(MathUtils.random(Assets.LOW_SPEED_SHARK, Assets.TOP_SPEED_SHARK),  Assets.sharkImage));
        lastSharkTime = TimeUtils.nanoTime();
    }

    public void drawBg() {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0.9f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw( Assets.bgImage, 0, 0);
        batch.end();
    }

    public void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        sub.x = touchPos.x -  Assets.subImage.getWidth() / 2f;
    }

    public void dispose() {
        Assets.dispose();
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
