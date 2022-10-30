package si.um.feri.ObjectSubmarine

import com.badlogic.gdx.math.Rectangle

open class GameObject(var x:Float, var y: Float, var width: Int, var height: Int){
    lateinit var bounds: Rectangle
    init {
        bounds = Rectangle(x, y , width.toFloat(), height.toFloat())
    }

    fun overlaps(element: GameObject): Boolean {
        return bounds.overlaps(element.bounds)
    }

}