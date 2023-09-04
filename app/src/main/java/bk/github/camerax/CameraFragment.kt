package bk.github.camerax

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.navigation.fragment.findNavController
import bk.github.camerax.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    val binding: FragmentCameraBinding get() = _binding!!

    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.previewView.doOnLayout { createCameraXControl() }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun createCameraXControl() {
        with(binding.previewView) {
            val previewSize = Size(width, height)
            val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
            var needUpdateGraphicOverlayImageSourceInfo = true

            val imageProcessor = TextRecognitionProcessor(TextRecognizerOptions.DEFAULT_OPTIONS)

            val buf: ByteArray? = null
            BitmapFactory.decodeByteArray()

            controller =
                LifecycleCameraController(requireContext().applicationContext).apply {
                    previewTargetSize = CameraController.OutputSize(previewSize)
//                    imageAnalysisTargetSize = CameraController.OutputSize(previewSize)
                    cameraSelector = this@CameraFragment.cameraSelector
                    setEnabledUseCases(IMAGE_ANALYSIS)
                    imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
                        with(binding.graphicOverlay) {
                            if (needUpdateGraphicOverlayImageSourceInfo) {
                                val rotationDegrees = image.imageInfo.rotationDegrees
                                if (rotationDegrees == 0 || rotationDegrees == 180) {
                                    setImageSourceInfo(image.width, image.height, isImageFlipped)
                                } else {
                                    setImageSourceInfo(image.height, image.width, isImageFlipped)
                                }

                                needUpdateGraphicOverlayImageSourceInfo = false
                            }

                            imageProcessor.processImageProxy(image, this)
                        }
                    }
                    bindToLifecycle(viewLifecycleOwner)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.allPermissionGranted(requireContext().applicationContext)) {
            requirePermissions()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requirePermissions() {
        findNavController().navigate(R.id.action_cameraFragment_to_permissionsFragment)
    }

}