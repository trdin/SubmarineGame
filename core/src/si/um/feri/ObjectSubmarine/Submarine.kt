package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3

class Submarine(
    x: Float,
    y: Float,
    var health : Int,
    var speed: Int,
    image : Texture
) : PictureGameObject(x, y, image) {

    init{
        super.x -= image.width/2f
    }
    fun hit(){ health-- }

    fun commandMoveLeft() {
        x -= speed * Gdx.graphics.deltaTime
        if (x < 0) x = 0f
    }

    fun commandMoveRight() {
        x += speed * Gdx.graphics.deltaTime
        if (x > Gdx.graphics.width - width)
            x = (Gdx.graphics.width - width).toFloat()
    }

    fun commandMoveLeftCorner() {
        x = 0f
    }

    fun commandMoveRightCorner() {
        x = (Gdx.graphics.width - width).toFloat()
    }

    /*private void commandMoveLeft() {
        sub.x -= SPEED * Gdx.graphics.getDeltaTime();
        if (sub.x < 0) sub.x = 0;
    }

    private void commandMoveRight() {
        sub.x += SPEED * Gdx.graphics.getDeltaTime();
        if (sub.x > Gdx.graphics.getWidth() - subImage.getWidth())
            sub.x = Gdx.graphics.getWidth() - subImage.getWidth();
    }

    private void commandMoveLeftCorner() {
        sub.x = 0;
    }

    private void commandMoveRightCorner() {
        sub.x = Gdx.graphics.getWidth() - subImage.getWidth();
    }*/

}