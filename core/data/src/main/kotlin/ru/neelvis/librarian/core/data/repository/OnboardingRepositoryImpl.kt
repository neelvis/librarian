package ru.neelvis.librarian.core.data.repository

import ru.neelvis.librarian.core.data.storage.OnboardingStorage
import ru.neelvis.librarian.core.domain.repository.OnboardingRepository
import ru.neelvis.librarian.core.model.UserGoal
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val storage: OnboardingStorage
): OnboardingRepository {
    override suspend fun isOnboardingCompleted(): Boolean =
        storage.isOnboardingCompleted()

    override suspend fun setOnboardingCompleted(value: Boolean) =
        storage.setOnboardingCompleted(value)

    override suspend fun getUserGoals(): Set<UserGoal>? =
        storage.getUserGoals()

    override suspend fun setUserGoals(value: Set<UserGoal>) =
        storage.setUserGoals(value)

    override suspend fun isCollectingStatsAllowed(): Boolean =
        storage.isCollectingStatsAllowed()

    override suspend fun setCollectingStatsAllowed(value: Boolean) =
        storage.setCollectingStatsAllowed(value)
}