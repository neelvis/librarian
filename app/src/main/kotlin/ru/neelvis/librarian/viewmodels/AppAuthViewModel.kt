package ru.neelvis.librarian.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.neelvis.librarian.core.domain.repository.AuthRepository
import ru.neelvis.librarian.core.model.User
import javax.inject.Inject

@HiltViewModel
class AppAuthViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null,
    )

    val isUserAuthenticated: StateFlow<Boolean> = authRepository.userFlow
        .map { it.also { Log.e("TEST", "Is authed: $it") } != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = false,
        )
}