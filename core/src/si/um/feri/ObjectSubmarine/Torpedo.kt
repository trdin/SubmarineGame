package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils

class Torpedo (
    x : Float,
    y : Float,
    image: Texture
) : PictureGameObject(
    x, y, image
) {
    var speed = 300

    fun update() {
        y += speed * Gdx.graphics.deltaTime
    }

    fun outOfScreen(): Boolean {
        return y >  Gdx.graphics.height
    }

}