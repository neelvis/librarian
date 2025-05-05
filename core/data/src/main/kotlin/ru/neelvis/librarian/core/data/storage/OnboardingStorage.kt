package ru.neelvis.librarian.core.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.neelvis.librarian.core.data.di.OnboardingPrefs
import ru.neelvis.librarian.core.model.UserGoal
import javax.inject.Inject

interface OnboardingStorage {
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun setOnboardingCompleted(value: Boolean)
    suspend fun getUserGoals(): Set<UserGoal>?
    suspend fun setUserGoals(value: Set<UserGoal>)
    suspend fun isCollectingStatsAllowed(): Boolean
    suspend fun setCollectingStatsAllowed(value: Boolean)
}

class OnboardingPrefsStorage @Inject constructor(
    @OnboardingPrefs private val prefs: SharedPreferences
): OnboardingStorage {

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_USER_GOALS = "user_goals"
        private const val KEY_ALLOW_STATS = "allow_collect_stats"
    }

    override suspend fun isOnboardingCompleted(): Boolean =
        prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override suspend fun setOnboardingCompleted(value: Boolean) {
        prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, value) }
    }

    override suspend fun getUserGoals(): Set<UserGoal>? =
        prefs.getStringSet(KEY_USER_GOALS, emptySet<String>())?.map {
            UserGoal.valueOf(it)
        }?.toSet()

    override suspend fun setUserGoals(value: Set<UserGoal>) {
        prefs.edit { putStringSet(KEY_USER_GOALS, value.map { it.name }.toSet()) }
    }

    override suspend fun isCollectingStatsAllowed(): Boolean =
        prefs.getBoolean(KEY_ALLOW_STATS, false)

    override suspend fun setCollectingStatsAllowed(value: Boolean) {
        prefs.edit { putBoolean(KEY_ALLOW_STATS, value) }
    }
}