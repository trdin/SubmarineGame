package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools

class PowerUp : PictureGameObject(
    (MathUtils.random(0, Gdx.graphics.getWidth() - Assets.ammoImage.getWidth())).toFloat(),
    Gdx.graphics.getHeight().toFloat(), Assets.ammoImage
) , Pool.Poolable{

    companion object{
        val POOL_POWER_UPS: Pool<PowerUp> = Pools.get(PowerUp::class.java, 2)
    }

    var speed = MathUtils.random(100, 200)
    var turn = MathUtils.random(0, 1) == 1
    var sideSpeed = MathUtils.random(0, 1)

    fun update() {
        y -= speed * Gdx.graphics.getDeltaTime()
        if (turn) {
            x += sideSpeed
        } else {
            x -= sideSpeed
        }

        if(x < 0 ){
            turn = true
        }else if( x > Gdx.graphics.width){
            turn = false
        }

    }
    fun outOfScreen(): Boolean {
        return y + height < 0
    }

    override fun reset() {
        x = MathUtils.random(0, Gdx.graphics.getWidth() - Assets.shellImage.getWidth()).toFloat()
        y = Gdx.graphics.getHeight().toFloat();
        speed = MathUtils.random(100, 200)
        turn = MathUtils.random(0, 1) == 1
        sideSpeed = MathUtils.random(0, 1)
    }

    fun free(){ POOL_POWER_UPS.free(this)}

}