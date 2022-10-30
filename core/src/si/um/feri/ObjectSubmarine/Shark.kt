package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils

class Shark(
    var speed: Int,
    image: Texture
) : PictureGameObject(
    (MathUtils.random(0, Gdx.graphics.getWidth() - image.getWidth())).toFloat(),
    Gdx.graphics.getHeight().toFloat(), image
) {
    var scale = MathUtils.random(0.8f, 1.2f)
    var turn = MathUtils.random(0, 1) == 1

    var turnDirection = MathUtils.random(0, 1) == 1
    var turnSpeed = MathUtils.random(0f, 5f)
    var angle = MathUtils.random(0f, 360f)

    var sideSpeed = MathUtils.random(0, 2)

    //var speed = 100
    fun update() {
        y -= speed * Gdx.graphics.getDeltaTime()
        if (turn) {
            x += sideSpeed
        } else {
            x -= sideSpeed
        }

        if (x < 0) {
            turn = true
        } else if (x + width > Gdx.graphics.width) {
            turn = false
        }
    }


    fun outOfScreen(): Boolean {
        return y + height < 0
    }

    override fun draw(batch: SpriteBatch) {
        //batch.draw(image, x, y, width / 2, height / 2, width, height)
        //batch.draw(super.image, x, y, width / 2, height / 2, width, height, scale, scale, 0f)
        if (turnDirection) {
            angle += turnSpeed
        } else {
            angle -= turnSpeed
        }

        batch.draw(
            image,
            x,
            y,
            width / 2,
            height / 2,
            width,
            height,
            scale,
            scale,
            angle,
            0,
            0,
            width.toInt(),
            height.toInt(),
            turn,
            false
        );
    }

}
