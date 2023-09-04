package bk.github.camerax

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.sqrt

class ViewPortGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {

    private val strokePaint = Paint().apply {
        strokeWidth = STROKE_WIDTH
        color = STROKE_COLOR
        style = Paint.Style.STROKE
        setShadowLayer(0.5f, 0f, 0f, Color.BLACK)
    }

    override fun draw(canvas: Canvas) {
        val padding = dp2pixel(16f)
        val square = overlay.width * overlay.height * SQUARE_RATIO
        val width = sqrt(square / ASPECT_RATIO).coerceAtMost(overlay.width - padding)
        val height = (width * ASPECT_RATIO).coerceAtMost(overlay.height - padding)
        val length = dp2pixel(24f).coerceAtMost(width / 2f).coerceAtMost(height / 2f)
        val x = (overlay.width - width) / 2f
        val y = (overlay.height - height) / 2f

        val path = Path().apply {
            moveTo(x + length, y)
            rLineTo(-length, 0f)
            rLineTo(0f, +length)
            rMoveTo(0f, height - length - length)
            rLineTo(0f, +length)
            rLineTo(+length, 0f)
            rMoveTo(width - length - length, 0f)
            rLineTo(+length, 0f)
            rLineTo(0f, -length)
            rMoveTo(0f, length + length - height)
            rLineTo(0f, -length)
            rLineTo(-length, 0f)
        }
        canvas.drawPath(path, strokePaint)
    }

    private fun dp2pixel(dp: Float) = dp * overlay.context.resources.displayMetrics.density

    companion object {
        private const val SQUARE_RATIO = 0.18127f
        private const val ASPECT_RATIO = 0.35294f    //  17x6

        private const val STROKE_WIDTH = 2f
        private const val STROKE_COLOR = Color.YELLOW
    }
}