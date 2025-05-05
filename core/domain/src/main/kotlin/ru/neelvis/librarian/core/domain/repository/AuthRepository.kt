package ru.neelvis.librarian.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.neelvis.librarian.core.model.User

interface AuthRepository {
    val userFlow: Flow<User?>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User?>
    suspend fun signInWithEmail(email: String, password: String): Result<User?>
    suspend fun signInWithPhone(phone: String, code: String): Result<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User?>
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(name: String, photoUrl: String?): Result<User?>
    suspend fun sendPasswordReset(email: String): Result<Unit>
}