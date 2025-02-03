package ru.neelvis.librarian.common.usecase

import android.util.Log

abstract class UseCase<in P, R : Any>() {

    operator fun invoke(parameters: P): UseCaseResult<R> {
        return try {
            execute(parameters).let {
                UseCaseResult.Success(it)
            }
        } catch (e: Exception) {
            Log.d(null, "UseCase exception: $e")
            UseCaseResult.Error(e)
        }
    }

    @Throws(NotImplementedError::class)
    protected abstract fun execute(parameters: P): R
}