package bk.github.camerax

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class InferenceInfoGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {

    private val textPaint = Paint().apply {
        textSize = TEXT_SIZE
        color = TEXT_COLOR
        setShadowLayer(0.5f, 0f, 0f, Color.BLACK)
    }

    override fun draw(canvas: Canvas) {
        val x = TEXT_SIZE * 0.5f
        val y = TEXT_SIZE * 1.5f

        val text = "Image size ${overlay.imageWidth}x${overlay.imageHeight}"
        canvas.drawText(text, x, y, textPaint)
    }

    companion object {
        private const val TEXT_SIZE = 50f
        private const val TEXT_COLOR = Color.WHITE
    }
}