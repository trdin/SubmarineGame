package si.um.feri.odbojZoge;


import static org.graalvm.compiler.replacements.Log.println;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import si.um.feri.ObjectSubmarine.World;


class CircleColor {
    public Circle circle;
    public Color color;
    public int x;
    public int y;
    public int radius;
    public int height;
    public boolean down;
    public int speed;
    public int lastSpeedIncerease = 0;

    public int speedTime = 100000000;

    public void create(int x, int y, int radius) {
        color = new Color(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 1);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.height = y;
        this.down = true;
        speed = 0;
    }

    public void update() {

        if(speed > -1 ) {
            if (down) {
                if (TimeUtils.nanoTime() - lastSpeedIncerease > speedTime) {
                    speed += 1;
                }
                this.y -= speed;
                if (!((y - radius) > 0)) {
                    down = false;
                    speed -= 1;
                }
            } else {
                if (TimeUtils.nanoTime() - lastSpeedIncerease > speedTime) {
                    speed -= 1;
                }
                y += speed;
                if (speed == 0) {
                    down = true;
                }
            }
        }

    }

}

public class OdbojZoge extends ApplicationAdapter {

    ShapeRenderer renderer;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Array<CircleColor> circles;


    @Override
    public void create() {


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        circles = new Array<CircleColor>();
    }


    /**
     * Runs every frame.
     */
    @Override
    public void render() {

        // process user input

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
        if (Gdx.input.isTouched()) commandTouched();

        Gdx.gl.glClearColor(0.5f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);




        for (CircleColor circle : circles) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(circle.color);
            renderer.circle(circle.x, circle.y, circle.radius);
            renderer.end();
            circle.update();
        }

    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {

    }

    public void commandTouched() {

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        int radius = MathUtils.random(30, 60);
        CircleColor circle = new CircleColor();
        circle.create((int) touchPos.x, (int) touchPos.y, radius);
        circles.add(circle);
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
