package ru.neelvis.librarian.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import ru.neelvis.librarian.core.domain.repository.AuthRepository
import ru.neelvis.librarian.core.model.User
import javax.inject.Inject
import androidx.core.net.toUri

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {
    private val _userFlow = MutableStateFlow<User?>(auth.currentUser?.toUser())
    override val userFlow: StateFlow<User?> = _userFlow.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        Log.e("TEST", "FirebaseAuth state changed : ${firebaseAuth.currentUser?.toUser()}")
        _userFlow.value = firebaseAuth.currentUser?.toUser()
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User?> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
        val user = result.user?.toUser()
        _userFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }.also {
        Log.e("TEST", "signUpWithEmail result : ${it.getOrNull()}")
    }


    override suspend fun signInWithEmail(email: String, password: String): Result<User?> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user?.toUser()
        _userFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }.also {
        Log.e("TEST", "signInWithEmail result : ${it.getOrNull()}")
    }

    override suspend fun signInWithPhone(phone: String, code: String): Result<User?> {
        return Result.failure(Exception("Phone auth is not yet implemented"))
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User?> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user?.toUser()
        _userFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }.also {
        Log.e("TEST", "signInWithGoogle result : ${it.getOrNull()}")
    }

    override suspend fun signOut() {
        auth.signOut()
        _userFlow.value = null
    }

    override suspend fun getCurrentUser(): User? = _userFlow.value

    override suspend fun updateProfile(name: String, photoUrl: String?): Result<User?> = try {
        val user = auth.currentUser ?: return Result.failure(Exception("No user"))
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(photoUrl?.toUri())
            .build()
        user.updateProfile(request).await()
        val updatedUser = user.toUser()
        _userFlow.value = updatedUser
        Result.success(updatedUser)
    } catch (e: Exception) {
        Result.failure(e)
    }.also {
        Log.e("TEST", "getCurrentUser result : ${it.getOrNull()}")
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        val result = auth.sendPasswordResetEmail(email).await()
        Log.d("AuthRepositoryImpl", result.toString())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun com.google.firebase.auth.FirebaseUser.toUser(): User = User(
    id = uid,
    name = displayName ?: "",
    email = email ?: "",
    phone = phoneNumber ?: "",
    photoUrl = photoUrl?.toString()
) 