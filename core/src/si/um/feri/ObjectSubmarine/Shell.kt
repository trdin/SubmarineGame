package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils

//var image = Texture(Gdx.files.internal("shell.png"));

class Shell(
    image: Texture
) : PictureGameObject(
    (MathUtils.random(0, Gdx.graphics.getWidth() - image.getWidth())).toFloat(),
    Gdx.graphics.getHeight().toFloat(), image
) {
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

}