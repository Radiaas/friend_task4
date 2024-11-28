package com.colab.myfriend

import android.app.Application
import com.google.firebase.FirebaseApp

class MyFriendApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)
    }
}