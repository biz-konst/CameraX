package bk.github.camerax

import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface

class TextRecognitionProcessor(options: TextRecognizerOptionsInterface): BaseImageProcessor<Text>() {

    private val textRecognizer = TextRecognition.getClient(options)

    override fun detectInImage(image: InputImage): Task<Text> = textRecognizer.process(image)

    override fun onFailure(e: Exception) {
    }

    override fun onSuccess(result: Text, graphicOverlay: GraphicOverlay) {
    }
}