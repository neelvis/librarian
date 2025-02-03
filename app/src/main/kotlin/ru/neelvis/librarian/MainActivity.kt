package ru.neelvis.librarian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ru.neelvis.librarian.ui.LibrarianApp
import ru.neelvis.librarian.ui.rememberLibrarianAppState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val appState = rememberLibrarianAppState()
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LibrarianApp(appState)
                }
            }
        }
    }
}
