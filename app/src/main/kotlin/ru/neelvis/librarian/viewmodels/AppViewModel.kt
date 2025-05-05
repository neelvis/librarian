package ru.neelvis.librarian.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.neelvis.librarian.core.domain.repository.OnboardingRepository
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _isOnboardingCompleted = MutableStateFlow<Boolean?>(null)
    val isOnboardingCompleted: StateFlow<Boolean?> = _isOnboardingCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            _isOnboardingCompleted.update{ onboardingRepository.isOnboardingCompleted() }
        }
    }

    fun onOnboardingComplete() {
        _isOnboardingCompleted.update { true }
    }
}