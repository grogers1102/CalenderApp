package com.example.calenderapp

import android.app.Application
class CalendarAppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        EventRepository.initialize(this)
    }
}