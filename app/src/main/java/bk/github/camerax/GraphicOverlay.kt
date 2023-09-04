package bk.github.camerax

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import androidx.collection.arraySetOf

class GraphicOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val lock = Any()
    private val graphics = arrayListOf<Graphic>()

    private var scaleFactor = 1f
    private val transformationMatrix = Matrix()

    private var postScaleWidthOffset = 0f
    private var postScaleHeightOffset = 0f

    var imageWidth: Int = 0
        private set
    var imageHeight: Int = 0
        private set
    private var isImageFlipped: Boolean = false
    private var needUpdateTransformation: Boolean = true

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            updateTransformationIfNeeded()
            graphics.forEach { it.draw(canvas) }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        needUpdateTransformation = true
    }

    fun add(graphic: Graphic) =
        synchronized(lock) {
            graphics.add(graphic)
        }

    fun remove(graphic: Graphic) =
        synchronized(lock) {
            graphics.remove(graphic)
        }.also {
            postInvalidate()
        }

    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }
        postInvalidate()
    }

    fun setImageSourceInfo(width: Int, height: Int, isFlipped: Boolean) {
        check(width > 0) { "image width must be positive" }
        check(height > 0) { "image height must be positive" }
        synchronized(lock) {
            imageWidth = width
            imageHeight = height
            isImageFlipped = isFlipped
            needUpdateTransformation = true
        }
        postInvalidate()
    }

    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return
        }
        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio = imageWidth.toFloat() / imageHeight
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            scaleFactor = width.toFloat() / imageWidth
            postScaleHeightOffset = (width / imageAspectRatio - height) / 2
        } else {
            scaleFactor = height.toFloat() / imageHeight
            postScaleWidthOffset = (height / imageAspectRatio - width) / 2
        }

        transformationMatrix.apply {
            reset()
            setScale(scaleFactor, scaleFactor)
            postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)

            if (isImageFlipped) {
                postScale(-1f, 1f, width / 2f, height / 2f)
            }
        }

        needUpdateTransformation = false
    }

    abstract class Graphic(protected val overlay: GraphicOverlay) {

        val applicationContext get() = overlay.context.applicationContext
        val transformationMatrix get() = overlay.transformationMatrix
        val isImageFlipped get() = overlay.isImageFlipped

        abstract fun draw(canvas: Canvas)

        fun scale(imagePixel: Float) = imagePixel * overlay.scaleFactor

        fun translateX(x: Float) = with(overlay) {
            if (isImageFlipped) {
                width - (scale(x) - postScaleWidthOffset)
            } else {
                scale(x) - postScaleWidthOffset
            }
        }

        fun translateY(y: Float) = scale(y) - overlay.postScaleHeightOffset

        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }
}