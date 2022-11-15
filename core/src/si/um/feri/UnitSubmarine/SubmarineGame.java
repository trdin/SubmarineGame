package si.um.feri.UnitSubmarine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

import si.um.feri.util.ViewportUtils;
import si.um.feri.util.debug.DebugCameraController;
import si.um.feri.util.debug.MemoryInfo;

public class SubmarineGame extends ApplicationAdapter {

    private ShapeRenderer renderer;
    private Viewport viewport;
    private Viewport hudViewport;

    private Texture shellImage;
    private Texture subImage;
    private Texture sharkImage;
    private Texture bgImage;
    private Texture torpedoImage;
    private Sound shellSound;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle sub;

    private Array<Rectangle> shells;    // special LibGDX Array
    private Array<Rectangle> sharks;
    private Array<Rectangle> torpedos;

    private long lastShellTime;
    private long lastSharkTime;
    private long lastTorpedoTime;
    private int shellsCollectedScore;
    private int subHealth;    // starts with 100
    private Sound sharkSound;
    private long lastSharkSoundTime;
    private Sound hitSound;

   /*private DebugCameraController debugCameraController;
    private boolean debug = false;
    private MemoryInfo memoryInfo;*/


    // world units
    private static final float WORLD_WIDTH = 600f;
    private static final float WORLD_HEIGHT = 300f;

    public BitmapFont font;

    // all values are set experimental
    private static final int SPEED = 600;    // pixels per second
    private static final int SPEED_SHELL = 200; // pixels per second
    private static int SPEED_SHARK = 100;    // pixels per second
    private static int SPEED_TORPEDO = 300;
    private static final long CREATE_SHELL_TIME = 1000000000;    // ns
    private static long CREATE_SHARK_TIME = 2000000000;// ns
    private static final long FIRE_TORPEDO_TIME = 2000000000;
    private static final long SHARK_SOUND_LENGTH = 1000000000;

    private DebugCameraController debugCameraController;
    private boolean debug = false;

    @Override
    public void create() {

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        hudViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        //font = new BitmapFont(Gdx.files.internal("fonts/arial-32.fnt"));

        font = new BitmapFont();
        font.getData().setScale(3);
        shellsCollectedScore = 0;
        subHealth = 100;

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);


        // default way to load a texture
        subImage = new Texture(Gdx.files.internal("sub.png"));
        shellImage = new Texture(Gdx.files.internal("shell.png"));
        sharkImage = new Texture(Gdx.files.internal("shark.png"));
        shellSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
        bgImage = new Texture(Gdx.files.internal("sea.png"));
        torpedoImage = new Texture(Gdx.files.internal("torpedo.png"));
        sharkSound = Gdx.audio.newSound(Gdx.files.internal("eat.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));

        // create the camera and the SpriteBatch
       /* camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();*/

        // create a Rectangle to logically represents the rocket
        sub = new Rectangle();
        sub.x = hudViewport.getWorldWidth() / 2f - subImage.getWidth() / 2f;    // center the rocket horizontally
        sub.y = 20;    // bottom left corner of the rocket is 20 pixels above the bottom screen edge
        sub.width = subImage.getWidth();
        sub.height = subImage.getHeight();

        shells = new Array<Rectangle>();
        sharks = new Array<Rectangle>();
        torpedos = new Array<Rectangle>();
        // add first astronaut and asteroid
        spawnShell();
        spawnShark();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelsPerUnit(viewport);
    }


    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        // clear screen
        Gdx.gl.glClearColor(0.3f,0.1f, 0.9f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug;

        if (debug) {
            debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
            debugCameraController.applyTo(camera);
        }

        hudViewport.apply();
        batch.setProjectionMatrix(hudViewport.getCamera().combined);
        batch.begin();
            batch.draw(bgImage, 0,0);

        batch.end();


        // process user input

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();



        if (subHealth > 0) {    // is game end?
            // move and remove any that are beneath the bottom edge of
            // the screen or that hit the rocket
            if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) commandMoveLeft();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) commandMoveRight();
            if (Gdx.input.isKeyPressed(Input.Keys.A)) commandMoveLeftCorner();
            if (Gdx.input.isKeyPressed(Input.Keys.S)) commandMoveRightCorner();

            // check if we need to create a new astronaut/asteroid
            if (TimeUtils.nanoTime() - lastShellTime > CREATE_SHELL_TIME) spawnShell();
            if (TimeUtils.nanoTime() - lastSharkTime > CREATE_SHARK_TIME) spawnShark();
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) spawnTorpedo();

            for (Iterator<Rectangle> it = sharks.iterator(); it.hasNext(); ) {
                Rectangle shark = it.next();
                shark.y -= SPEED_SHARK * Gdx.graphics.getDeltaTime();
                if (shark.y + sharkImage.getHeight() < 0) it.remove();
                if (shark.overlaps(sub)) {
                    subHealth--;
                    if(TimeUtils.nanoTime() - lastSharkSoundTime > SHARK_SOUND_LENGTH){
                        sharkSound.play();
                        lastSharkSoundTime = TimeUtils.nanoTime();
                    }
                }
            }

            for (Iterator<Rectangle> it = shells.iterator(); it.hasNext(); ) {
                Rectangle shell = it.next();
                shell.y -= SPEED_SHELL * Gdx.graphics.getDeltaTime();
                if (shell.y + shellImage.getHeight() < 0) it.remove();    // from screen
                if (shell.overlaps(sub)) {
                    shellSound.play();
                    shellsCollectedScore++;
                    if (shellsCollectedScore % 10 == 0) {
                        SPEED_SHARK += 40;
                        if(CREATE_SHARK_TIME > 1000000){
                            CREATE_SHARK_TIME -= 100000000;
                        }

                    }    // speeds up
                    it.remove();    // smart Array enables remove from Array
                }
            }
            for (Iterator<Rectangle> it = torpedos.iterator(); it.hasNext(); ) {
                Rectangle torpedo = it.next();
                torpedo.y += SPEED_TORPEDO * Gdx.graphics.getDeltaTime();
                if(torpedo.y >  hudViewport.getWorldHeight()) {
                    it.remove();
                    continue;
                };
                for (Iterator<Rectangle> itshark = sharks.iterator(); itshark.hasNext(); ) {
                    Rectangle shark = itshark.next();
                    if (torpedo.overlaps(shark)) {
                        it.remove();
                        itshark.remove();
                        hitSound.play();
                    }
                }
            }

        } else {    // health of rocket is 0 or less
            hudViewport.apply();
            batch.setProjectionMatrix(hudViewport.getCamera().combined);
            batch.begin();
            {
                font.setColor(Color.RED);
                font.draw(batch, "The END", hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f);
            }
            batch.end();
        }

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera


        // begin a new batch and draw the rocket, astronauts, asteroids
        batch.begin();
        {    // brackets added just for indent

            batch.draw(subImage, sub.x, sub.y);
            for (Rectangle shark : sharks) {
                batch.draw(sharkImage, shark.x, shark.y);
            }
            for (Rectangle shell : shells) {
                batch.draw(shellImage, shell.x, shell.y);
            }
            for (Rectangle torpedo : torpedos) {
                batch.draw(torpedoImage, torpedo.x, torpedo.y);
            }
            font.setColor(Color.YELLOW);
            font.draw(batch, "" + shellsCollectedScore, hudViewport.getWorldWidth() - 50, hudViewport.getWorldHeight() - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + subHealth, 20, hudViewport.getWorldHeight() - 20);

            draw();
        }

        batch.end();

        if (debug) {
            renderDebug();
        }

    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        shellImage.dispose();
        sharkImage.dispose();
        subImage.dispose();
        shellSound.dispose();
        bgImage.dispose();
        torpedoImage.dispose();
        batch.dispose();
        font.dispose();
        renderer.dispose();
    }

    private void spawnTorpedo(){
        if (TimeUtils.nanoTime() - lastTorpedoTime > FIRE_TORPEDO_TIME){
            Rectangle torpedo= new Rectangle();
            torpedo.x = sub.x + subImage.getWidth()/2;
            torpedo.y = sub.y + subImage.getHeight()/2;
            torpedo.width = torpedoImage.getWidth();
            torpedo.height = torpedoImage.getHeight();
            torpedos.add(torpedo);
            lastTorpedoTime = TimeUtils.nanoTime();
        }
    }

    private void spawnShell() {
        Rectangle shell = new Rectangle();
        shell.x = MathUtils.random(0, hudViewport.getWorldWidth() - shellImage.getWidth());
        shell.y = hudViewport.getWorldHeight();
        shell.width = shellImage.getWidth();
        shell.height = shellImage.getHeight();
        shells.add(shell);
        lastShellTime = TimeUtils.nanoTime();
    }

    private void spawnShark() {
        Rectangle shark = new Rectangle();
        shark.x = MathUtils.random(0, hudViewport.getWorldWidth() - shellImage.getWidth());
        shark.y = hudViewport.getWorldHeight();
        shark.width = sharkImage.getWidth();
        shark.height = sharkImage.getHeight();
        sharks.add(shark);
        lastSharkTime = TimeUtils.nanoTime();
    }

    private void commandMoveLeft() {
        sub.x -= SPEED * Gdx.graphics.getDeltaTime();
        if (sub.x < 0) sub.x = 0;
    }

    private void commandMoveRight() {
        sub.x += SPEED * Gdx.graphics.getDeltaTime();
        if (sub.x > hudViewport.getWorldWidth() - subImage.getWidth())
            sub.x = hudViewport.getWorldWidth() - subImage.getWidth();
    }

    private void commandMoveLeftCorner() {
        sub.x = 0;
    }

    private void commandMoveRightCorner() {
        sub.x = hudViewport.getWorldWidth() - subImage.getWidth();
    }

    private void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        sub.x = touchPos.x - subImage.getWidth() / 2f;
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }

    private void renderDebug() {
        ViewportUtils.drawGrid(viewport, renderer, 30);

        viewport.apply();

        Color oldColor = new Color(renderer.getColor());
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();
        renderer.setColor(oldColor);
    }

    private void drawDebug() {
        renderer.setColor(Color.CYAN);
        renderer.circle(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 50f, 25);

    }

    private void draw() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        String screenSize = "Screen/Window size: " + screenWidth + " x " + screenHeight + " px";
        String worldSize = "World size: " + (int) worldWidth + " x " + (int) worldHeight + " world units";
        String oneWorldUnit = "One world unit: " + (screenWidth / worldWidth) + " x " + (screenHeight / worldHeight) + " px";

        font.setColor(Color.BLACK);
        font.draw(batch,
                screenSize,
                2f,
                hudViewport.getWorldHeight() - 200f);

        font.draw(batch,
                worldSize,
                2f,
                hudViewport.getWorldHeight() - 250f);

        font.draw(batch,
                oneWorldUnit,
                2f,
                hudViewport.getWorldHeight() - 300f);
    }


}
