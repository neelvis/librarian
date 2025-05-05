package ru.neelvis.librarian

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LibrarianApplication : Application() {
    override fun onCreate() {
        super.onCreate() // To init Firebase before the first use
        FirebaseApp.initializeApp(this)
    }
}