package com.xuatseg.yuanyin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xuatseg.yuanyin.mode.IModeManager
import com.xuatseg.yuanyin.mode.IModeMonitor
import com.xuatseg.yuanyin.mode.IModePersistence

class MainViewModelFactory(
    private val modeManager: IModeManager,
    private val modePersistence: IModePersistence,
    private val modeMonitor: IModeMonitor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(modeManager, modePersistence, modeMonitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
