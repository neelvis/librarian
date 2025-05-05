package ru.neelvis.librarian.common.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class SuspendUseCase<in P, R : Any>(val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P): UseCaseResult<R> {
        return try {
            val result = withContext(coroutineDispatcher) {
                execute(parameters)
            }
            UseCaseResult.Success(result)
        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }

    @Throws(NotImplementedError::class)
    protected suspend abstract fun execute(parameters: P): R
}