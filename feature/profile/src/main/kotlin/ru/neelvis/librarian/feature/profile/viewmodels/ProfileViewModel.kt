package ru.neelvis.librarian.feature.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.neelvis.librarian.core.domain.repository.AuthRepository
import ru.neelvis.librarian.core.model.User
import javax.inject.Inject

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Loading : AuthState()
}

sealed class AuthUiEvent {
    data object PasswordResetEmailSent : AuthUiEvent()
    data class PasswordResetError(val message: String) : AuthUiEvent()
    data object AuthSuccessful: AuthUiEvent()
    data class AuthFailed(val message: String): AuthUiEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.userFlow.map {
        if (it != null) AuthState.Authenticated(it) else AuthState.Unauthenticated
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = AuthState.Loading
    )

    private val _uiEvents = MutableSharedFlow<AuthUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun emitAuthResult(result: Result<User?>) {
        viewModelScope.launch {
            _uiEvents.emit(
                result.fold(
                    onSuccess = { user -> if (user != null) AuthUiEvent.AuthSuccessful else AuthUiEvent.AuthFailed("No user") },
                    onFailure = { AuthUiEvent.AuthFailed(it.message ?: "Unknown authentication error") })
            )
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            val result = authRepository.signUpWithEmail(email, password, name)
            emitAuthResult(result)
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(email, password)
            emitAuthResult(result)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            emitAuthResult(result)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun updateProfile(name: String, photoUrl: String?) {
        viewModelScope.launch {
            val result = authRepository.updateProfile(name, photoUrl)
            emitAuthResult(result)
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            result.fold(
                onSuccess = { _uiEvents.emit(AuthUiEvent.PasswordResetEmailSent) },
                onFailure = { _uiEvents.emit(AuthUiEvent.PasswordResetError(it.message ?: "Unknown error")) }
            )
        }
    }
} 