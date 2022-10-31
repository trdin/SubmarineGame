package si.um.feri.ObjectSubmarine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;



//TODO sub image
public class SubmarineGame extends ApplicationAdapter {

    private World world;
    private WorldRender worldRender;

    @Override
    public void create() {
        world = new World();
        world.create();
        worldRender = new WorldRender(world);
    }


    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        world.drawBg();

        // process user input

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();


        if (world.sub.getHealth() > 0) {    // is game end?
            // move and remove any that are beneath the bottom edge of
            // the screen or that hit the rocket
            if (Gdx.input.isTouched()) world.commandTouched();    // mouse or touch screen
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) world.sub.commandMoveLeft();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) world.sub.commandMoveRight();
            if (Gdx.input.isKeyPressed(Input.Keys.A)) world.sub.commandMoveLeftCorner();
            if (Gdx.input.isKeyPressed(Input.Keys.S)) world.sub.commandMoveRightCorner();

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) world.spawnTorpedo();

            worldRender.renderObjects();

        } else {    // health of rocket is 0 or less
            world.endGame();
        }

        world.drawGame();
    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        world.dispose();
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
