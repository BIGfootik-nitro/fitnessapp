package com.example.fitnessapp

import android.app.Application
import com.example.fitnessapp.di.ServiceLocator

class FitnessApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
