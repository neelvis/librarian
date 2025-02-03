package ru.neelvis.librarian.feature.add_books

import android.content.Context
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.AspectRatio.RATIO_4_3
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.feature.add_books.viewmodels.AddBooksViewModel
import ru.neelvis.librarian.feature.add_books.viewmodels.CameraPreviewViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun AddBookScreen(viewModel: AddBooksViewModel = hiltViewModel<AddBooksViewModel>()) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val context = LocalContext.current
    viewModel.isBookAdded.observe(LocalLifecycleOwner.current) { success ->
        success?.let {
            if (success) {
                Toast.makeText(context, "Book added successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Book was not added.", Toast.LENGTH_SHORT).show()
            }
            viewModel.finishAddingBook()
        }
    }

    AddBookScreen(
        cameraPermissionState.status.isGranted,
        onRequestPermissionClick = { cameraPermissionState.launchPermissionRequest() },
        onAddBookClick = { book -> viewModel.addBookToList(book) })
}

private suspend fun initCamera(context: Context, width: Int, height: Int) {
    val p = ProcessCameraProvider.getInstance(context = context)
    val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    val aspectRatio = RATIO_4_3

}

@Composable
fun AddBookScreen(isPermissionGranted: Boolean, onRequestPermissionClick: () -> Unit, onAddBookClick: (Book) -> Unit) {

    var titleDefaultValue = "Book title"
    var authorsDefaultValue = "Authors"
    var isbnDefaultValue = "ISBN"
    var sourceDefaultValue = "Source"
    var descriptionDefaultValue = "Description"

    var title by remember { mutableStateOf("") } //TODO: add orientation change
    var authors by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val textFieldModifier = Modifier.padding(vertical = 10.dp)

    Column(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxSize()
    ) {
        Text("Add a book")
        //TODO: add camera view to take a picture of the book
        OutlinedTextField(modifier = textFieldModifier,
            value = title,
            onValueChange = { title = it },
            label = { Text(titleDefaultValue) })
        OutlinedTextField(modifier = textFieldModifier,
            value = authors,
            onValueChange = { authors = it },
            label = { Text(authorsDefaultValue) })
        OutlinedTextField(modifier = textFieldModifier,
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text(isbnDefaultValue) })
        OutlinedTextField(modifier = textFieldModifier,
            value = source,
            onValueChange = { source = it },
            label = { Text(sourceDefaultValue) })
        OutlinedTextField(modifier = textFieldModifier,
            value = description,
            onValueChange = { description = it },
            label = { Text(descriptionDefaultValue) })
        CameraButton(isPermissionGranted, onRequestPermissionClick)
        Row {
            Button(
                onClick = {
                    onAddBookClick.invoke(
                        Book(
                            title = title,
                            authors = authors.split(","),
                            isbn = isbn,
                            source = source,
                            description = description
                        )
                    )
                },
            ) { Text("Add") }
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

@Preview
@Composable
fun AddBookScreenPreview() {
    AddBookScreen(isPermissionGranted = false, onRequestPermissionClick = {}, onAddBookClick = {})
}

@Composable
fun CameraButton(
    isPermissionGranted: Boolean,
    onRequestPermissionClick: () -> Unit,
    viewModel: CameraPreviewViewModel = hiltViewModel<CameraPreviewViewModel>(),
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val appContext: Context = LocalContext.current.applicationContext

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isPermissionGranted) {
            LaunchedEffect(lifecycleOwner) {
                viewModel.bindToCamera(appContext, lifecycleOwner)
            }
            surfaceRequest?.let { request ->
                CameraXViewfinder(request)
            }

        } else {
            Text(
                "Please grant camera permission to capture the book cover.\nClick here to grant.",
                modifier = Modifier.clickable(onClick = onRequestPermissionClick)
            )
        }

    }

}

@Preview
@Composable
fun CameraButtonPreview() {
    CameraButton(isPermissionGranted = false, onRequestPermissionClick = {})
}
