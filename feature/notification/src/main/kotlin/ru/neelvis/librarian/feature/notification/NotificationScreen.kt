package ru.neelvis.librarian.feature.notification

import android.content.res.Resources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import ru.neelvis.librarian.core.model.User
import ru.neelvis.librarian.core.ui.BlurredOverlay

@Composable
fun NotificationScreen(
    userFlow: StateFlow<User?>,
    onSignInClick: () -> Unit,
) {
    val user: User? by userFlow.collectAsStateWithLifecycle(initialValue = null)

    if (user == null) {
        BlurredOverlay(onSignInClick = onSignInClick)
    } else {
        // Normal notification content goes here
        // For now, just a placeholder
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Notifications",
                style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text("Hello, ${user!!.name}",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}