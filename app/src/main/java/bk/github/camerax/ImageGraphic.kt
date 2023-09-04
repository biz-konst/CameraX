package bk.github.camerax

import android.graphics.Bitmap
import android.graphics.Canvas

class ImageGraphic(overlay: GraphicOverlay, private val bitmap: Bitmap) :
    GraphicOverlay.Graphic(overlay) {

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, transformationMatrix, null)
    }
}