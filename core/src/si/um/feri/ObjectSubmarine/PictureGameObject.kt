package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle ;

open class PictureGameObject(
    x: Float,
    y: Float,
    var image: Texture,
    ) : Rectangle(x, y, image.width.toFloat(), image.height.toFloat()) {

    open fun draw(batch: SpriteBatch){
        batch.draw(image, x ,y)
    }
}