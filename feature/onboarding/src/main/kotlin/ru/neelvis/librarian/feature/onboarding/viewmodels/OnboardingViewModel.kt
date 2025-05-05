package ru.neelvis.librarian.feature.onboarding.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.neelvis.librarian.core.domain.repository.OnboardingRepository
import ru.neelvis.librarian.core.model.UserGoal
import javax.inject.Inject

sealed interface OnboardingStep{
    data object Welcome: OnboardingStep
    data object SetGoals: OnboardingStep
    data object AllowStats: OnboardingStep
    data object Finished: OnboardingStep
}

data class OnboardingUIState(
    val step: OnboardingStep,
    val selectedGoals: Set<UserGoal>,
    val allowCollectingStats: Boolean
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: OnboardingRepository
): ViewModel() {

    private val _uiState =
        MutableStateFlow(OnboardingUIState(OnboardingStep.Welcome, emptySet(), allowCollectingStats = false))
    val uiState = _uiState.asStateFlow()

    fun getAvailableGoals(): Set<UserGoal> = UserGoal.entries.toSet()

    fun addGoal(goal: UserGoal) {
        _uiState.update {
            it.copy(
                selectedGoals = _uiState.value.selectedGoals + goal
            ).also { goals -> Log.d("TEST", "new goals $goals") }
        }
    }

    fun removeGoal(goal: UserGoal) {
            _uiState.update {
                it.copy(
                    selectedGoals = _uiState.value.selectedGoals - goal
                ).also { goals -> Log.d("TEST", "new goals $goals") }
            }
    }

    fun setCollectingStats(allowStats: Boolean) {
        Log.d("TEST", "allowStats $allowStats")
        _uiState.update {  it.copy(allowCollectingStats = allowStats) }
    }

    fun goNext() {
        val nextStep = when (_uiState.value.step) {
            OnboardingStep.Welcome -> OnboardingStep.SetGoals
            OnboardingStep.SetGoals -> OnboardingStep.AllowStats
            OnboardingStep.AllowStats -> OnboardingStep.Finished
            OnboardingStep.Finished -> OnboardingStep.Finished
        }
        Log.d("TEST", "Go to the next step $nextStep")
        _uiState.update { it.copy(nextStep) }
    }
    fun goBack() {

        val previousStep = when (_uiState.value.step) {
            OnboardingStep.Welcome -> OnboardingStep.Welcome
            OnboardingStep.SetGoals -> OnboardingStep.Welcome
            OnboardingStep.AllowStats -> OnboardingStep.SetGoals
            OnboardingStep.Finished -> OnboardingStep.AllowStats
        }
        Log.d("TEST", "Go to the previous step $previousStep")
        _uiState.update { it.copy(previousStep) }
    }
    fun finishOnboarding(saveData: Boolean) {
        viewModelScope.launch {
            if (saveData) {
                repository.setUserGoals(_uiState.value.selectedGoals)
                repository.setCollectingStatsAllowed(_uiState.value.allowCollectingStats)
            }
            repository.setOnboardingCompleted(true)
//            Log.e("TEST", "onboarding is always required")
        }
    }

}