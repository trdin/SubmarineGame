package si.um.feri.ObjectSubmarine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class WorldRender {
    private World world;

    public WorldRender(World world) {
        this.world = world;

    }

    public void renderObjects() {
        renderShells();
        renderSharks();
        renderTorpedos();
    }

    private void renderShells() {
        if (TimeUtils.nanoTime() - world.lastShellTime > Assets.CREATE_SHELL_TIME) world.spawnShell();

        for (Iterator<Shell> it = world.shells.iterator(); it.hasNext(); ) {
            Shell shell = it.next();
            shell.update();
            if (shell.outOfScreen()) it.remove();    // from screen
            if (shell.overlaps(world.sub)) {
                Assets.shellSound.play();
                world.shellsCollectedScore++;
                if (world.shellsCollectedScore % 10 == 0) {
                    Assets.TOP_SPEED_SHARK += 20;
                    Assets.LOW_SPEED_SHARK += 10;
                    if (Assets.CREATE_SHARK_TIME > 1000000) {
                        Assets.CREATE_SHARK_TIME -= 1000000;
                    }

                }    // speeds up
                it.remove();
            }
        }
    }

    private void renderSharks() {
        if (TimeUtils.nanoTime() - world.lastSharkTime > Assets.CREATE_SHARK_TIME) world.spawnShark();

        for (Iterator<Shark> it = world.sharks.iterator(); it.hasNext(); ) {
            Shark shark = it.next();
            shark.update();
            if (shark.outOfScreen()) it.remove();
            if (shark.overlaps(world.sub)) {
                world.sub.hit();
                if (TimeUtils.nanoTime() - world.lastSharkSoundTime > Assets.SHARK_SOUND_LENGTH) {
                    Assets.sharkSound.play();
                    world.lastSharkSoundTime = TimeUtils.nanoTime();
                }
            }
        }
    }
    //TODO fix this in seperate classes
    private void renderTorpedos() {
        for (Iterator<Torpedo> it = world.torpedos.iterator(); it.hasNext(); ) {
            Torpedo torpedo = it.next();
            torpedo.update();
            if (torpedo.outOfScreen()) {
                it.remove();
                continue;
            }
            ;
            for (Iterator<Shark> itShark = world.sharks.iterator(); itShark.hasNext(); ) {
                Shark shark = itShark.next();
                if (torpedo.overlaps(shark)) {
                    it.remove();
                    itShark.remove();
                    Assets.hitSound.play();
                }
            }
        }
    }
}
