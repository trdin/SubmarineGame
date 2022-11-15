package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools

class Shark() : PictureGameObject(
    (MathUtils.random(0, Gdx.graphics.getWidth() - Assets.sharkImage.getWidth())).toFloat(),
    Gdx.graphics.getHeight().toFloat(), Assets.sharkImage
), Pool.Poolable {
    companion object {
        val POOL_SHARKS: Pool<Shark> = Pools.get(Shark::class.java, 10)
    }

    var speed = MathUtils.random(Assets.LOW_SPEED_SHARK, Assets.TOP_SPEED_SHARK)
    var scale = MathUtils.random(0.8f, 1.2f)
    var turn = MathUtils.random(0, 1) == 1

    var turnSpeed = MathUtils.random(0f, 5f)
    var angle = MathUtils.random(0f, 360f)

    var sideSpeed = MathUtils.random(0, 2)

    init {
        width *= scale;
        height *= scale;
    }

    fun free() {
        POOL_SHARKS.free(this)
    }

    override fun reset() {
        turnSpeed = MathUtils.random(0f, 5f)
        angle = MathUtils.random(0f, 360f)

        sideSpeed = MathUtils.random(0, 2)
        scale = MathUtils.random(0.8f, 1.2f)
        turn = MathUtils.random(0, 1) == 1

        x = MathUtils.random(0, Gdx.graphics.getWidth() - width.toInt()).toFloat()
        y = Gdx.graphics.getHeight().toFloat()

        width *= scale;
        height *= scale;
    }

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
        if (!Assets.pause) {
            if (turn) {
                angle += turnSpeed
            } else {
                angle -= turnSpeed
            }
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
            image.width,
            image.height,
            turn,
            false
        );
    }

    override fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.rect(
            x,
            y,
            width / 2,
            height / 2,
            width,
            height,
            scale,
            scale,
            angle
        )
    }

}
