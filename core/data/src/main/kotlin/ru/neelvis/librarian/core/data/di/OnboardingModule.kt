package ru.neelvis.librarian.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.neelvis.librarian.core.data.repository.OnboardingRepositoryImpl
import ru.neelvis.librarian.core.data.storage.OnboardingPrefsStorage
import ru.neelvis.librarian.core.data.storage.OnboardingStorage
import ru.neelvis.librarian.core.domain.repository.OnboardingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {

    @Singleton
    @Binds
    internal abstract fun bindOnboardingRepository(
        impl: OnboardingRepositoryImpl
    ): OnboardingRepository

    @Singleton
    @Binds
    internal abstract fun bindStorage(
        impl: OnboardingPrefsStorage
    ): OnboardingStorage
}
