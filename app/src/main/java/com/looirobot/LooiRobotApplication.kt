package com.looirobot

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LooiRobotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化应用程序
    }
} 