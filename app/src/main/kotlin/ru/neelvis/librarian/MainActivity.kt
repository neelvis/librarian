package ru.neelvis.librarian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.neelvis.librarian.feature.onboarding.OnboardingScreen
import ru.neelvis.librarian.ui.LibrarianApp
import ru.neelvis.librarian.ui.rememberLibrarianAppState
import ru.neelvis.librarian.viewmodels.AppViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val appState = rememberLibrarianAppState()
            val appViewModel: AppViewModel = hiltViewModel()
            val isOnboardingCompleted = appViewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isOnboardingCompleted.value == true) {
                        LibrarianApp(appState)
                    } else if (isOnboardingCompleted.value == false) {
                        Scaffold(contentWindowInsets = WindowInsets.safeGestures) { padding ->
                            Box(modifier = Modifier.padding(padding)) {
                                OnboardingScreen(
                                    onOnboardingComplete = { appViewModel.onOnboardingComplete() },
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
