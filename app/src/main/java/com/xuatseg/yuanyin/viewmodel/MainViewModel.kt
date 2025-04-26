package com.xuatseg.yuanyin.viewmodel

import androidx.lifecycle.ViewModel
import com.xuatseg.yuanyin.model.BotState
import com.xuatseg.yuanyin.model.WorkMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 主界面ViewModel，负责管理机器人状态
 */
class MainViewModel : ViewModel() {

    // 机器人状态数据流
    private val _botState = MutableStateFlow(BotState())
    val botState: StateFlow<BotState> = _botState.asStateFlow()

    /**
     * 更新连接状态
     */
    fun updateConnectionStatus(isConnected: Boolean) {
        _botState.value = _botState.value.copy(
            isConnected = isConnected,
            error = if (!isConnected) "连接断开" else null
        )
    }

    /**
     * 更新电池电量
     */
    fun updateBatteryLevel(level: Int) {
        _botState.value = _botState.value.copy(batteryLevel = level)
    }

    /**
     * 更新工作模式
     */
    fun updateWorkMode(mode: WorkMode) {
        _botState.value = _botState.value.copy(workMode = mode)
    }

    /**
     * 设置错误状态
     */
    fun setError(error: String?) {
        _botState.value = _botState.value.copy(error = error)
    }
}
