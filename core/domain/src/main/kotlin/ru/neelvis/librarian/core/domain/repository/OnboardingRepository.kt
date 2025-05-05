package ru.neelvis.librarian.core.domain.repository

import ru.neelvis.librarian.core.model.UserGoal

interface OnboardingRepository {
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun setOnboardingCompleted(value: Boolean)
    suspend fun getUserGoals(): Set<UserGoal>?
    suspend fun setUserGoals(value: Set<UserGoal>)
    suspend fun isCollectingStatsAllowed(): Boolean
    suspend fun setCollectingStatsAllowed(value: Boolean)
}
