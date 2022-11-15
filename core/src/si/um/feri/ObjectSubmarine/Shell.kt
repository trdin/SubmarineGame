package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools

//var image = Texture(Gdx.files.internal("shell.png"));

class Shell : PictureGameObject(
    (MathUtils.random(0, Gdx.graphics.getWidth() - Assets.shellImage.getWidth())).toFloat(),
    Gdx.graphics.getHeight().toFloat(), Assets.shellImage
), Pool.Poolable{
    companion object{
        val POOL_SHELLS: Pool<Shell> = Pools.get(Shell::class.java, 10)
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
        }else if( x > Gdx.graphics.width - width){
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

    fun free(){ POOL_SHELLS.free(this)}

}