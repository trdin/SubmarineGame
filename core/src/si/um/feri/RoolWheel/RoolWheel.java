package si.um.feri.RoolWheel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class RoolWheel extends ApplicationAdapter {

    ShapeRenderer renderer;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture wheelImage;

    private int speed = 2;
    private int x = 0;
    private int y = -10;
    private boolean right = true;
    private int angle = 0;


    @Override
    public void create() {


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        wheelImage = new Texture(Gdx.files.internal("wheel.png"));

    }


    /**
     * Runs every frame.
     */
    @Override
    public void render() {

        // process user input

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
        //if (Gdx.input.isTouched()) commandTouched();

        Gdx.gl.glClearColor(0.5f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
            ///batch.draw(wheelImage, x ,y);
        batch.draw(
                wheelImage,
                x,
                y,
                wheelImage.getWidth() / 2f,
                wheelImage.getHeight() / 2f,
                wheelImage.getWidth(),
                wheelImage.getHeight(),
                1,
                1,
                angle,
                0,
                0,
                wheelImage.getWidth(),
                wheelImage.getHeight(),
                false,
                false
        );
        batch.end();

        if(right){
            x += speed;
            angle -= (int) (speed * 360/(2*3.14*wheelImage.getWidth()/2f));
        }else{
            x-=speed;
            angle += (int) (speed * 360/(2*3.14*wheelImage.getWidth()/2f));
        }
        if (x < 0) {
            right = true;
        } else if (x + wheelImage.getWidth() > Gdx.graphics.getWidth()) {
            right = false;
        }




    }


    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {

    }

  /*  public void commandTouched() {

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        int radius = MathUtils.random(30, 60);
        CircleColor circle = new CircleColor();
        circle.create((int) touchPos.x, (int) touchPos.y, radius);
        circles.add(circle);
    }*/

    private void commandExitGame() {
        Gdx.app.exit();
    }
}