package bk.github.camerax

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

abstract class BaseImageProcessor<T> {

    @ExperimentalGetImage
    fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {
        with(imageProxy) {
            val image = InputImage.fromMediaImage(image!!, imageInfo.rotationDegrees)

            val bitmap = BitmapUtils.getBitmap(imageProxy)

                    graphicOverlay.clear()

                    bitmap?.let { graphicOverlay.add(ImageGraphic(graphicOverlay, it)) }
                    graphicOverlay.add(InferenceInfoGraphic(graphicOverlay))
                    graphicOverlay.add(ViewPortGraphic(graphicOverlay))
                    graphicOverlay.postInvalidate()

            imageProxy.close()
//            detectInImage(image)
//                .addOnSuccessListener { result ->
//                    Log.d("test", "${Thread.currentThread().name} $bitmap")
//                    onSuccess(result, graphicOverlay)
//                    graphicOverlay.clear()
//
//                    bitmap?.let { graphicOverlay.add(ImageGraphic(graphicOverlay, it)) }
//                    graphicOverlay.add(InferenceInfoGraphic(graphicOverlay))
//                    graphicOverlay.add(ViewPortGraphic(graphicOverlay))
//                    graphicOverlay.postInvalidate()
//                }
//                .addOnFailureListener { onFailure(it) }
//                .addOnCompleteListener { imageProxy.close() }
        }
    }

    abstract fun detectInImage(image: InputImage): Task<T>

    abstract fun onSuccess(result: T, graphicOverlay: GraphicOverlay)

    abstract fun onFailure(e: Exception)
}