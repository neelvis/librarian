package ru.neelvis.librarian.feature.profile

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import ru.neelvis.librarian.core.model.User
import ru.neelvis.librarian.feature.profile.viewmodels.AuthState
import ru.neelvis.librarian.feature.profile.viewmodels.AuthUiEvent
import ru.neelvis.librarian.feature.profile.viewmodels.ProfileViewModel
import java.io.File
import java.util.UUID

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {

    val authState by viewModel.authState.collectAsState()
    when (authState) {
        is AuthState.Unauthenticated -> AuthAuthScreen(viewModel)
        is AuthState.Authenticated -> ProfileEditScreen(
            user = (authState as AuthState.Authenticated).user,
            onSignOut = { viewModel.signOut() },
            onUpdateProfile = { name, photoUrl -> viewModel.updateProfile(name, photoUrl) }
        )
        is AuthState.Loading -> LoadingScreen()
    }
}

@Composable
fun AuthAuthScreen(viewModel: ProfileViewModel, error: String? = null) {
    var isSignIn by remember { mutableStateOf(true) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }

    val localContext = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is AuthUiEvent.PasswordResetEmailSent -> {
                    showPasswordResetDialog = false
                    Toast.makeText(localContext, "Please check your email", Toast.LENGTH_SHORT).show()
                }
                is AuthUiEvent.PasswordResetError -> {
                    showPasswordResetDialog = false
                    Toast.makeText(localContext, "Failed to send an email. ${event.message}", Toast.LENGTH_SHORT).show()
                }
                is AuthUiEvent.AuthFailed -> {
                    Toast.makeText(localContext, "Authentication failed. ${event.message}", Toast.LENGTH_SHORT).show()
                }
                AuthUiEvent.AuthSuccessful -> {
                    Toast.makeText(localContext, "Authentication OK.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    if (isSignIn) {
        SignInScreen(
            onSignIn = { email, password -> viewModel.signInWithEmail(email, password) },
            onGoogleSignIn = { idToken -> viewModel.signInWithGoogle(idToken) },
            onSwitchToSignUp = { isSignIn = false },
            onSwitchToResetPassword = { showPasswordResetDialog = true },
            error = error
        )
    } else {
        SignUpScreen(
            onSignUp = { name, email, password -> viewModel.signUpWithEmail(email, password, name) },
            onGoogleSignIn = { idToken -> viewModel.signInWithGoogle(idToken) },
            onSwitchToSignIn = { isSignIn = true },
            error = error
        )
    }
    if (showPasswordResetDialog) {
        PasswordResetDialog(
            onDismiss = { showPasswordResetDialog = false },
            onSubmit = { email -> viewModel.sendPasswordReset(email) }
        )
    }
}

@Composable
fun GoogleSignInButtonWithCredentialManager(
    onGoogleToken: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            isLoading = true
            coroutineScope.launch {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId("803605023369-qkim0s5ur30lfhob2tc58ie8sidm1c98.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context as Activity
                    )
                    handleSignIn(result, onGoogleToken)
                } catch (e: GetCredentialException) {
                    Log.e("GoogleSignIn", "Error: $e")
                } finally {
                    isLoading = false
                }
            }
        },
        enabled = !isLoading,
        modifier = modifier
    ) {
        Text(if (isLoading) "Signing in..." else "Sign in with Google")
    }
}

private fun handleSignIn(
    result: GetCredentialResponse,
    onGoogleToken: (String) -> Unit,
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            onGoogleToken(idToken)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("GoogleSignIn", "Invalid Google ID token response", e)
        }
    } else {
        Log.e("GoogleSignIn", "Unexpected credential type: ${credential::class.java.name}")
    }
}

@Composable
fun SignInScreen(
    onSignIn: (String, String) -> Unit,
    onGoogleSignIn: (String) -> Unit,
    onSwitchToSignUp: () -> Unit,
    onSwitchToResetPassword: () -> Unit,
    error: String? = null,
) {
    var email by remember { mutableStateOf("9202953@mail.ru") }
    var password by remember { mutableStateOf("Detropan0))") }
    var hidePassword by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text("Sign In", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation =
                    if (hidePassword)
                        PasswordVisualTransformation()
                    else
                        VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton({ hidePassword = !hidePassword }) {
                        Icon(
                            imageVector =
                                if (hidePassword)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                            contentDescription = "Hide password"
                        )
                    }
                }
            )


        }
        Button(
            onClick = { onSignIn(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Sign In")
        }
        GoogleSignInButtonWithCredentialManager(onGoogleToken = onGoogleSignIn)
        TextButton(onClick = onSwitchToSignUp) {
            Text("Don't have an account? Sign Up")
        }
        TextButton(onClick = onSwitchToResetPassword) {
            Text("Forgot your password?")
        }
        if (!error.isNullOrBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SignUpScreen(
    onSignUp: (String, String, String) -> Unit,
    onGoogleSignIn: (String) -> Unit,
    onSwitchToSignIn: () -> Unit,
    error: String? = null,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSignUp(name, email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        GoogleSignInButtonWithCredentialManager(onGoogleToken = onGoogleSignIn)
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onSwitchToSignIn) {
            Text("Already have an account? Sign In")
        }
        if (!error.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ProfileEditScreen(
    user: User,
    onSignOut: () -> Unit,
    onUpdateProfile: (String, String?) -> Unit,
) {
    var name by remember { mutableStateOf(user.name) }
    var photoUrl by remember { mutableStateOf(user.photoUrl) }
    var isPhotoPickerOpen by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoUrl = it.toString() }
    }
    // Camera capture
    var cameraImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraImageUri?.let { photoUrl = it.toString() }
        }
    }

    fun launchCamera() {
        val file = File(context.cacheDir, "profile_${UUID.randomUUID()}.jpg")
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        cameraImageUri = uri
        cameraLauncher.launch(uri)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { showPhotoDialog = true }
        ) {
            if (photoUrl.isNullOrBlank()) {
                // Placeholder image
                Image(
                    painter = painterResource(ru.neelvis.librarian.common.R.drawable.profile_mock),
                    contentDescription = "Profile photo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = "Profile photo",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap photo to change", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = user.email,
            onValueChange = {},
            label = { Text("Email") },
            singleLine = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onUpdateProfile(name, photoUrl) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        ) {
            Text("Save Changes")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text("Sign Out", color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Change Profile Photo") },
            text = { Text("Choose a method:") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Pick from Gallery") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    launchCamera()
                }) { Text("Take a Photo") }
            }
        )
    }
}

@Composable
fun PasswordResetDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("9202953@mail.ru") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column {
                Text("Enter your email address and we'll send you a link to reset your password.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(email)
                },
                enabled = email.isNotBlank()
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LoadingScreen() {
    // TODO: Implement loading indicator
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
