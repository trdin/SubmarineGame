package si.um.feri.ObjectSubmarine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.util.ViewportUtils;
import si.um.feri.util.debug.DebugCameraController;
import si.um.feri.util.debug.MemoryInfo;
//TODO fix  odboj shell powerup ??
//TODO  rotate shape shark

public class World {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    Submarine sub;
    Array<Shell> shells;    // special LibGDX Array
    Array<Shark> sharks;
    Array<Torpedo> torpedoes;
    Array<PowerUp> powerUps;
    long lastShellTime;
    long lastSharkTime;
    private long lastTorpedoTime;
    int shellsCollectedScore;
    long lastSharkSoundTime;
    long lastPowerUpTime;

    public BitmapFont font;

    DebugCameraController debugCameraController;
    MemoryInfo memoryInfo;
    boolean debug = false;

    private ShapeRenderer shapeRenderer;
    public Viewport viewport;


    public void create() {
        font = new BitmapFont();
        font.getData().setScale(2);
        shellsCollectedScore = 0;
        Assets.load();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        // debug
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        memoryInfo = new MemoryInfo(500);

        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);


        sub = new Submarine();

        shells = new Array<Shell>();
        sharks = new Array<Shark>();
        torpedoes = new Array<Torpedo>();
        powerUps = new Array<PowerUp>();

        lastPowerUpTime = TimeUtils.nanoTime();
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
            for (Torpedo torpedo : torpedoes) {
                torpedo.draw(batch);
            }
            for(PowerUp powerUp: powerUps){
                powerUp.draw(batch);
            }
            font.setColor(Color.YELLOW);
            font.draw(batch, "" + shellsCollectedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + sub.getHealth(), 20, Gdx.graphics.getHeight() - 20);

            if (sub.getPower()) {
                batch.draw(Assets.torpedoImage, Gdx.graphics.getWidth() - 45, Gdx.graphics.getHeight() - 100, Assets.torpedoImage.getWidth(), Assets.torpedoImage.getHeight());
            }
        }
        batch.end();
        if(debug){
            drawDebug();
        }
    }

    public void drawDebug(){
        debugCameraController.applyTo(camera);
        batch.begin();
        {
            // the average number of frames per second
            GlyphLayout layout = new GlyphLayout(font, "FPS:" + Gdx.graphics.getFramesPerSecond());
            font.setColor(Color.YELLOW);
            font.draw(batch, layout, Gdx.graphics.getWidth() - layout.width, Gdx.graphics.getHeight() - 50);

            // number of rendering calls, ever; will not be reset unless set manually
            font.setColor(Color.YELLOW);
            font.draw(batch, "RC:" + batch.totalRenderCalls, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 20);

            memoryInfo.render(batch, font);
        }
        batch.end();

        batch.totalRenderCalls = 0;
        ViewportUtils.drawGrid(viewport, shapeRenderer, 50);

        // print rectangles
        shapeRenderer.setProjectionMatrix(camera.combined);
        // https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        {
            shapeRenderer.setColor(1, 1, 0, 1);
           /* for (Rectangle asteroid : asteroids) {
                shapeRenderer.rect(asteroid.x, asteroid.y, asteroidImage.getWidth(), asteroidImage.getHeight());
            }
            for (Rectangle astronaut : astronauts) {
                shapeRenderer.rect(astronaut.x, astronaut.y, astronautImage.getWidth(), astronautImage.getHeight());
            }
            shapeRenderer.rect(rocket.x, rocket.y, rocketImage.getWidth(), rocketImage.getHeight());*/

            for (Shark shark : sharks) {
                shark.drawDebug(shapeRenderer);
            }
            for (Shell shell : shells) {
                shell.drawDebug(shapeRenderer);
            }
            for (Torpedo torpedo : torpedoes) {
                torpedo.drawDebug(shapeRenderer);
            }
            for(PowerUp powerUp: powerUps){
                powerUp.drawDebug(shapeRenderer);
            }
            sub.drawDebug(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public void spawnTorpedo() {
        if(sub.getPower() &&  TimeUtils.nanoTime() - lastPowerUpTime > Assets.POWER_UP_LENGTH*3){
            sub.setPower(false);
        }

        if (sub.getPower() && TimeUtils.nanoTime() - lastTorpedoTime > 100000000 ) {
            torpedoes.add(Torpedo.Companion.create(sub.x + Assets.subImage.getWidth() / 2f, sub.y + Assets.subImage.getHeight() / 2f));
            lastTorpedoTime = TimeUtils.nanoTime();
        }
    }

    public void spawnPowerUp() {
        powerUps.add(PowerUp.Companion.getPOOL_POWER_UPS().obtain());
        lastPowerUpTime = TimeUtils.nanoTime();
        Assets.POWER_UP_TIME += 10000000;
    }

    public void spawnShell() {
        shells.add(Shell.Companion.getPOOL_SHELLS().obtain());
        lastShellTime = TimeUtils.nanoTime();
    }

    public void spawnShark() {
        sharks.add(Shark.Companion.getPOOL_SHARKS().obtain());
        lastSharkTime = TimeUtils.nanoTime();
    }

    public void drawBg() {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0.9f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(Assets.bgImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    public void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        sub.x = touchPos.x - Assets.subImage.getWidth() / 2f;
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
            font.draw(batch, "The END", Gdx.graphics.getHeight() / 2f - 25, Gdx.graphics.getHeight() / 2f + 25);
            font.draw(batch, "To reset press R", Gdx.graphics.getHeight() / 2f - 50, Gdx.graphics.getHeight() / 2f - 25);
        }
        batch.end();
        Assets.pause = true;
    }

    public void pause() {
        batch.begin();
        {
            font.setColor(Color.RED);
            font.draw(batch, "Paused", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
        }
        batch.end();
    }

    public void reset() {
        Assets.pause = false;
        shellsCollectedScore = 0;

        lastPowerUpTime = TimeUtils.nanoTime();
        Assets.POWER_UP_TIME =  2000000000;
        Assets.TOP_SPEED_SHARK = 150;
        Assets.LOW_SPEED_SHARK = 50;

        sub = new Submarine();
        for (Shark shark : sharks) {
            shark.free();
        }
        for (Shell shell : shells) {
            shell.free();
        }
        for (Torpedo torpedo : torpedoes) {
            torpedo.free();
        }
        for(PowerUp powerUp: powerUps){
            powerUp.free();
        }

        shells = new Array<Shell>();
        sharks = new Array<Shark>();
        torpedoes = new Array<Torpedo>();

        spawnShell();
        spawnShark();


    }
}
