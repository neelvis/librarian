package ru.neelvis.librarian.core.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.view.OrientationEventListener
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ru.neelvis.librarian.common.utils.toSurfaceRotation
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.core.ui.viewmodels.CameraPreviewViewModel


@Composable
fun BookInfoEditable(
    book: Book? = null,
    onAcceptClick: (Book) -> Unit,
) {
    var titleLabel = "Book title"
    var authorsLabel = "Authors"
    var isbnLabel = "ISBN"
    var sourceLabel = "Source"
    var descriptionLabel = "Description"

    var title by remember { mutableStateOf(book?.title ?: "") }
    var authors by remember { mutableStateOf(book?.authors?.joinToString(", ") ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var source by remember { mutableStateOf(book?.source ?: "") }
    var description by remember { mutableStateOf(book?.description ?: "") }
    var cover: ByteArray? = book?.cover

    val textFieldModifier = Modifier.padding(all = 10.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //TODO: add camera view to take a picture of the book
        OutlinedTextField(modifier = textFieldModifier,
            value = title,
            onValueChange = { title = it },
            label = { Text(titleLabel) })
        OutlinedTextField(modifier = textFieldModifier,
            value = authors,
            onValueChange = { authors = it },
            label = { Text(authorsLabel) })
        OutlinedTextField(modifier = textFieldModifier,
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text(isbnLabel) })
        OutlinedTextField(modifier = textFieldModifier,
            value = source,
            onValueChange = { source = it },
            label = { Text(sourceLabel) })
        OutlinedTextField(modifier = textFieldModifier,
            value = description,
            onValueChange = { description = it },
            label = { Text(descriptionLabel) })

        CameraMiniPreview(onPictureReceived = { picture ->
            cover = picture
        })

        Row {
            Button(
                onClick = {
                    onAcceptClick.invoke(
                        Book(
                            id = book?.id ?: "",
                            title = title,
                            authors = authors.split(","),
                            source = source,
                            cover = cover,
                            isbn = isbn,
                            description = description
                        )
                    )
                },
            ) { Text("Accept") }
            Button(
                onClick = {
                    title = ""
                    authors = ""
                    isbn = ""
                    source = ""
                    description = ""
                },
            ) { Text("Clear") }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraMiniPreview(
    onPictureReceived: (picture: ByteArray) -> Unit,
    viewModel: CameraPreviewViewModel = hiltViewModel<CameraPreviewViewModel>(),
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val appContext: Context = LocalContext.current.applicationContext

    //CAMERA
    //captured image
    val capturedImage: Bitmap? by viewModel.capturedImage.collectAsStateWithLifecycle()
    val capturedBytes: ByteArray? by viewModel.capturedBytes.collectAsStateWithLifecycle()
    //torch
    val isCameraBound by viewModel.isCameraBound.collectAsStateWithLifecycle()
    val isTorchAvailable by viewModel.isTorchAvailable.collectAsStateWithLifecycle()
    val isTorchOn by viewModel.torchState.collectAsStateWithLifecycle()
    val torchTimeLeft: Float by viewModel.torchTimeLeftRatio.collectAsStateWithLifecycle()
    val previewView = remember(appContext) {
        PreviewView(appContext).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    capturedBytes?.let {
        onPictureReceived(capturedBytes!!)
    }

    //Catch screen rotation to match photo rotation
    DisposableEffect(Unit) {
        val orientationEventListener =
            object : OrientationEventListener(appContext) {
                private var lastRotation = -1
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == ORIENTATION_UNKNOWN) {
                        return
                    }

                    val rotation = orientation.toSurfaceRotation()
                    if (lastRotation != rotation) {
                        lastRotation = rotation
                        viewModel.updateTargetRotation(targetRotation = rotation)
                    }
                }
            }
        orientationEventListener.enable()

        onDispose {
            orientationEventListener.disable()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        LaunchedEffect(lifecycleOwner, previewView) {
            viewModel.bindToCamera(
                appContext = appContext,
                lifecycleOwner = lifecycleOwner,
                surfaceProvider = previewView.surfaceProvider,
            )
        }
    }
    Row {
        CameraMiniPreview(
            previewView = previewView,
            isPermissionGranted = cameraPermissionState.status.isGranted,
            isCameraBound = isCameraBound,
            isTorchAvailable = isTorchAvailable,
            isTorchOn = isTorchOn,
            torchTimeLeftRatio = torchTimeLeft,
            onRequestPermissionClick = { cameraPermissionState.launchPermissionRequest() },
            onTorchClick = { viewModel.toggleTorch() },
            onShootClick = { viewModel.takePicture() },
        )
        capturedImage?.let { image ->
            Image(bitmap = image.asImageBitmap(), contentDescription = "ssssssssssssss")
        }
    }
}


@Composable
internal fun CameraMiniPreview(
    previewView: PreviewView,
    isPermissionGranted: Boolean,
    isCameraBound: Boolean,
    isTorchAvailable: Boolean,
    isTorchOn: Boolean,
    torchTimeLeftRatio: Float,
    onRequestPermissionClick: () -> Unit = {},
    onTorchClick: () -> Unit = {},
    onShootClick: () -> Unit = {},
) {
    // Camera mini-preview or permission message
    Row(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(200.dp)
                .border(border = BorderStroke(width = 1.dp, color = Color.Black), shape = RoundedCornerShape(5.dp))
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            if (isPermissionGranted) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    "Camera permission is required to capture the book cover.\nClick here to grant.",
                    modifier = Modifier
                        .clickable(onClick = onRequestPermissionClick)
                        .padding(all = 5.dp)
                )
            }
        }

        // Camera buttons
        if (isPermissionGranted) {
            val buttonsModifier = Modifier
                .size(width = 40.dp, height = 40.dp)
                .border(border = BorderStroke(width = 1.dp, color = Color.Black), shape = RoundedCornerShape(5.dp))
            Column(
                modifier = Modifier
                    .width(50.dp)
                    .height(200.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.camera),
                    contentDescription = "Shoot",
                    modifier = buttonsModifier.clickable(enabled = isCameraBound, onClick = onShootClick)
                )
                Image(
                    painter = painterResource(R.drawable.arrows),
                    contentDescription = "Expand camera",
                    modifier = buttonsModifier
                )
                Image(
                    painter = painterResource(
                        if (!isTorchAvailable)
                            R.drawable.torch_disabled
                        else if (isTorchOn)
                            R.drawable.torch_on
                        else
                            R.drawable.torch_off
                    ),
                    contentDescription = "Torch button",
                    modifier = buttonsModifier
                        .clickable(enabled = isTorchAvailable && isCameraBound, onClick = onTorchClick)
                        .then(
                            if (isTorchOn) Modifier.background(
                                brush = Brush.sweepGradient(
                                    colorStops = arrayOf(
                                        0.0f to Color.DarkGray,
                                        1.0f - torchTimeLeftRatio to Color.Gray,
                                        1.0f to Color.Yellow
                                    )
                                ),
                                shape = RoundedCornerShape(5.dp)
                            ) else Modifier
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun CameraButtonPreviewTorchOff() {
    Column {
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = true,
            isCameraBound = true,
            isTorchAvailable = false,
            isTorchOn = false,
            torchTimeLeftRatio = 0f,
        )
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = true,
            isCameraBound = true,
            isTorchAvailable = true,
            isTorchOn = false,
            torchTimeLeftRatio = 0f,
        )
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = false,
            isCameraBound = false,
            isTorchAvailable = false,
            isTorchOn = false,
            torchTimeLeftRatio = 0f,
        )
    }
}

@Preview
@Composable
fun CameraButtonPreviewTorchOn() {
    Column {
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = true,
            isCameraBound = true,
            isTorchAvailable = true,
            isTorchOn = true,
            torchTimeLeftRatio = 1f,
        )
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = true,
            isCameraBound = true,
            isTorchAvailable = true,
            isTorchOn = true,
            torchTimeLeftRatio = 0.75f,
        )
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = true,
            isCameraBound = true,
            isTorchAvailable = true,
            isTorchOn = true,
            torchTimeLeftRatio = 0.5f,
        )
        CameraMiniPreview(
            previewView = PreviewView(LocalContext.current),
            isPermissionGranted = true,
            isCameraBound = true,
            isTorchAvailable = true,
            isTorchOn = true,
            torchTimeLeftRatio = 0f,
        )
    }
}
