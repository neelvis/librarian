package ru.neelvis.librarian.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.neelvis.librarian.core.model.UserGoal
import ru.neelvis.librarian.feature.onboarding.viewmodels.OnboardingStep
import ru.neelvis.librarian.feature.onboarding.viewmodels.OnboardingViewModel

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.step) {
        OnboardingStep.Welcome -> WelcomeScreen { viewModel.goNext() }
        OnboardingStep.SetGoals -> GoalSelectionScreen(
            onGoalSelected = { goal, selected ->
                if (selected)
                    viewModel.addGoal(goal)
                else
                    viewModel.removeGoal(goal)

            },
            onNext = { viewModel.goNext() },
            onSkip = {
                viewModel.finishOnboarding(false)
                onOnboardingComplete()
            },
            goals = viewModel.getAvailableGoals(),
            selectedGoals = uiState.selectedGoals,
        )

        OnboardingStep.AllowStats -> AllowStatsScreen(
            onBack = { viewModel.goBack() },
            onNext = { viewModel.goNext() },
            onSkip = {
                viewModel.finishOnboarding(false)
                onOnboardingComplete()
            },
            allowStats = uiState.allowCollectingStats,
            onAllowStats = { allowStats -> viewModel.setCollectingStats(allowStats) },
        )

        OnboardingStep.Finished -> FinishScreen(onFinish = {
            viewModel.finishOnboarding(true)
            onOnboardingComplete()
        })
    }
}

@Composable
fun NavigationButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(all = 12.dp)) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun NavigationButtons(
    isNextVisible: Boolean = false,
    isBackVisible: Boolean = false,
    isSkipVisible: Boolean = false,
    isFinishVisible: Boolean = false,
    onNext: () -> Unit = {},
    onBack: () -> Unit = {},
    onSkip: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isBackVisible) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                NavigationButton("< Back") { onBack() }
            }
        }
        when {
            isSkipVisible -> {
                NavigationButton("Skip") { onSkip() }
            }

            isFinishVisible -> {
                NavigationButton("Let's go") { onFinish() }
            }
        }
        if (isNextVisible) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                NavigationButton("Next >") { onNext() }
            }
        }
    }
}

@Composable
private fun WelcomeScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Welcome to the Librarian",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Text(
            "Let's get started.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        NavigationButtons(
            isNextVisible = true,
            onNext = onNext
        )
    }
}

@Composable
private fun GoalSelectionScreen(
    onGoalSelected: (goal: UserGoal, selected: Boolean) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    goals: Set<UserGoal>,
    selectedGoals: Set<UserGoal>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "What's your goal?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Text(
            text = "Choose how you'd like to use the app",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        goals.forEach { goal ->
            GoalCard(
                goal = goal,
                selectedGoals.contains(goal),
                onClick = onGoalSelected
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        NavigationButtons(
            isNextVisible = true,
            onNext = onNext,
            isSkipVisible = true,
            onSkip = onSkip,
        )
    }
}

@Composable
private fun GoalCard(
    goal: UserGoal,
    isSelected: Boolean,
    onClick: (goal: UserGoal, selected: Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable {
                onClick(goal, !isSelected)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = goal.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun AllowStatsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    allowStats: Boolean,
    onAllowStats: (allowStats: Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Collect technical data",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 16.dp)
                .background(color = Color(0x55FFFFFF)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = allowStats,
                onCheckedChange = { checked ->
                    onAllowStats(checked)
                },
            )
            Text(
                "Allow us to collect anonymous technical data to improve our services",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable {
                        onAllowStats(!allowStats)
                    }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        NavigationButtons(
            isNextVisible = true,
            onNext = onNext,
            isSkipVisible = true,
            onSkip = onSkip,
            isBackVisible = true,
            onBack = onBack,
        )
    }
}

@Composable
private fun FinishScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "You are all set!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(
            "Start using the app",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(bottom = 24.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        NavigationButtons(
            isFinishVisible = true,
            onFinish = onFinish
        )

    }
}

@Preview
@Composable
fun WelcomePreview() {
    WelcomeScreen(onNext = {})
}

@Preview
@Composable
fun GoalSelectionScreenPreview() {
    GoalSelectionScreen(
        onGoalSelected = { _, _ -> },
        onNext = {},
        onSkip = {},
        goals = UserGoal.entries.toSet(),
        selectedGoals = setOf(UserGoal.CHALLENGE)
    )
}

@Preview
@Composable
fun AllowStatsScreenPreview() {
    AllowStatsScreen(
        allowStats = true,
        onAllowStats = { _ -> },
        onNext = {},
        onSkip = {},
        onBack = {},
    )
}

@Preview
@Composable
fun FinishScreenPreview() {
    FinishScreen({})
}