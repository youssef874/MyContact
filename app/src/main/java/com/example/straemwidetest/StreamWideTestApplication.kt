package com.example.straemwidetest

import android.app.Application
import android.content.res.Resources
import com.example.straemwidetest.model.Repository
import com.example.straemwidetest.model.content_provider.PhoneContactProvider
import com.example.straemwidetest.model.room.AppDatabase


class StreamWideTestApplication: Application() {
    //Instantiate repository here to make it accessible in the all application while instantiate it
    // just once
    val repository: Repository by lazy {
        Repository(
            PhoneContactProvider(this),
            AppDatabase.getDatabase(this)
        )
    }
}