package ru.neelvis.librarian.core.data.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private const val ONBOARDING_PREFS = "ONBOARDING_PREFS"

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class OnboardingPrefs

@Module
@InstallIn(SingletonComponent::class)
class SharedPrefsModule {
    @Provides
    @Singleton
    @OnboardingPrefs
    fun provideOnboardingSharedPrefs(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(ONBOARDING_PREFS, Context.MODE_PRIVATE)
}