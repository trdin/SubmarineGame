package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools

class Torpedo(
    x: Float,
    y: Float,
) : PictureGameObject(
    x, y, Assets.torpedoImage
), Pool.Poolable {
    companion object {
        val POOL_TORPEDOES: Pool<Torpedo> = Pools.get(Torpedo::class.java, 20)
        fun create(x: Float, y: Float): Torpedo {
            var torpedo = POOL_TORPEDOES.obtain()
            torpedo.setXY(x,y)
            return torpedo;
        }
    }

    constructor() : this(0f, 0f)

    var speed = 300

    fun update() {
        y += speed * Gdx.graphics.deltaTime
    }

    fun outOfScreen(): Boolean {
        return y > Gdx.graphics.height
    }

    override fun reset() {
        x = 0f
        y = 0f
    }
    fun free(){ POOL_TORPEDOES.free(this)}

    fun setXY(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}