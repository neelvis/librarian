package ru.neelvis.librarian.core.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.os.CountDownTimer
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class CameraPreviewViewModel @Inject constructor() : ViewModel() {

    //Camera and use cases
    private var camera: Camera? = null

    private val cameraPreviewUseCase = Preview.Builder().build()
    private val _imageCaptureUseCase = ImageCapture.Builder().build()
    private val _isCameraBound = MutableStateFlow(false)
    val isCameraBound: StateFlow<Boolean> = _isCameraBound

    fun updateTargetRotation(targetRotation: Int) {
        _imageCaptureUseCase.targetRotation = targetRotation
    }


    //Torch handling
    private val _isTorchAvailable = MutableStateFlow<Boolean>(false)
    val isTorchAvailable: StateFlow<Boolean> = _isTorchAvailable

    private val _torchState = MutableStateFlow<Boolean>(false)
    val torchState: StateFlow<Boolean> = _torchState

    private val torchTimeoutMillis = 10_000L
    private val _torchTimeLeftRatio = MutableStateFlow<Float>(0f)
    val torchTimeLeftRatio: StateFlow<Float> = _torchTimeLeftRatio
    val torchTimeoutTimer = object : CountDownTimer(torchTimeoutMillis, 25L) {
        override fun onTick(millisUntilFinished: Long) {
            _torchTimeLeftRatio.update { millisUntilFinished * 1f / torchTimeoutMillis }
        }

        override fun onFinish() {
            if (_torchState.value) {
                toggleTorch()
            }
        }
    }

    fun toggleTorch() {
        if (_isTorchAvailable.value) {
            val newTorchState = !_torchState.value
            torchTimeoutTimer.cancel()
            camera?.cameraControl?.enableTorch(newTorchState)

            if (newTorchState) {
                _torchTimeLeftRatio.update { 1f }
                torchTimeoutTimer.start()
            } else {
                torchTimeoutTimer.cancel()
            }
        }
    }


    //Activate camera
    suspend fun bindToCamera(
        appContext: Context,
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
    ) {
        val processCameraProvider: ProcessCameraProvider = ProcessCameraProvider.Companion.awaitInstance(appContext)
        cameraPreviewUseCase.surfaceProvider = surfaceProvider
        camera = processCameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, cameraPreviewUseCase, _imageCaptureUseCase)
        _isCameraBound.update { true }

        _isTorchAvailable.update { camera?.cameraInfo?.hasFlashUnit() == true }
        camera?.cameraInfo?.torchState?.observe(lifecycleOwner) { newTorchState ->
            _torchState.update { newTorchState == TorchState.ON }
        }

        try {
            awaitCancellation()
        } finally {
            torchTimeoutTimer.cancel()
            _torchState.update { false }
            _torchTimeLeftRatio.update { 0f }
            _isTorchAvailable.update { false }
            _isCameraBound.update { false }
            processCameraProvider.unbindAll()
            camera?.cameraInfo?.torchState?.removeObservers(lifecycleOwner)
            camera = null
        }
    }


    //Shoot handling
    // For DB
    private val _capturedBytes = MutableStateFlow<ByteArray?>(null)
    val capturedBytes: StateFlow<ByteArray?> = _capturedBytes

    // For preview
    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> = _capturedImage

    fun takePicture() {
        _imageCaptureUseCase.takePicture(Executors.newSingleThreadExecutor(), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureStarted() {
                super.onCaptureStarted()
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                Log.d(
                    null,
                    "Captured successfully, format ${image.width}x${image.height}, ${image.imageInfo.rotationDegrees}, image size = ${image.toBitmap().allocationByteCount}"
                )
                viewModelScope.launch {
                    val bitmap = image.toBitmap();
                    val rotated = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        Matrix().apply {
                            setRectToRect(
                                RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
                                RectF(0f, 0f, 300f, 400f),
                                Matrix.ScaleToFit.CENTER
                            )
                            postRotate(image.imageInfo.rotationDegrees.toFloat())
                        },
                        true
                    )
                    Log.d(null, "Rotated image size: ${rotated.allocationByteCount}")
                    _capturedImage.update { rotated }

                    val baos: ByteArrayOutputStream = ByteArrayOutputStream()
                    rotated.compress(CompressFormat.WEBP, 80, baos)
                    Log.d(null, "Compressed image size: ${baos.size()}")
                    _capturedBytes.update { baos.toByteArray() }
                }
            }
        })
    }
}

fun measureCompressionTime(viewModelScope: CoroutineScope, image: ImageProxy) {
    data class CompressionParams(val format: CompressFormat, val quality: Int)

    val toCompress = listOf(
        CompressionParams(CompressFormat.PNG, 100), //            3.372s, image size = 11133650
        CompressionParams(CompressFormat.PNG, 80), //             3.376s, image size = 11133650
        CompressionParams(CompressFormat.PNG, 50), //             3.376s, image size = 11133650
        CompressionParams(CompressFormat.PNG, 30), //             3.386s, image size = 11133650
        CompressionParams(CompressFormat.JPEG, 100), //           177ms,  image size = 4564392
        CompressionParams(CompressFormat.JPEG, 80), //            117ms,  image size = 1157168
        CompressionParams(CompressFormat.JPEG, 50), //            108ms,  image size = 569974
        CompressionParams(CompressFormat.JPEG, 30), //            101ms,  image size = 383581
        CompressionParams(CompressFormat.WEBP_LOSSLESS, 100), //  6.535s, image size = 8122766
        CompressionParams(CompressFormat.WEBP_LOSSLESS, 80), //   4.465s, image size = 8214626
        CompressionParams(CompressFormat.WEBP_LOSSLESS, 50), //   1.525s, image size = 8252544
        CompressionParams(CompressFormat.WEBP_LOSSLESS, 30), //   1.113s, image size = 8325792
        CompressionParams(CompressFormat.WEBP, 100), //           4.09s,  image size = 8225732
        CompressionParams(CompressFormat.WEBP, 80), //            1.314s, image size = 534760
        CompressionParams(CompressFormat.WEBP, 50), //            1.163s, image size = 286554
        CompressionParams(CompressFormat.WEBP, 30), //            1.098s, image size = 203038
        CompressionParams(CompressFormat.WEBP_LOSSY, 100), //     1.824s, image size = 2777930
        CompressionParams(CompressFormat.WEBP_LOSSY, 80), //      1.289s, image size = 534760
        CompressionParams(CompressFormat.WEBP_LOSSY, 50), //      1.151s, image size = 286554
        CompressionParams(CompressFormat.WEBP_LOSSY, 30), //      1.084s, image size = 203038
    )
    val compressJobs: List<Deferred<String>> = toCompress.map { it ->
        viewModelScope.async {
            Log.d(null, "Launched compression: ${it.format.name} - ${it.quality}")
            var currentTime = System.currentTimeMillis()
            val baos: ByteArrayOutputStream = ByteArrayOutputStream()
            image.toBitmap().compress(it.format, it.quality, baos)
            "Compression to ${it.format.name} - ${it.quality} took ${(System.currentTimeMillis() - currentTime).toDuration(DurationUnit.MILLISECONDS)}, image size = ${baos.size()}"
        }
    }
    viewModelScope.launch {
        val results: List<String> = compressJobs.awaitAll()
        results.forEach {
            Log.d(null, it)
        }
    }
}
