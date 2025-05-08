package com.looirobot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.looirobot.domain.model.MovementDirection
import com.looirobot.domain.model.RobotState
import com.looirobot.domain.usecase.ControlRobotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val controlRobotUseCase: ControlRobotUseCase
) : ViewModel() {

    private val _robotState = MutableStateFlow(RobotState())
    val robotState: StateFlow<RobotState> = _robotState.asStateFlow()

    init {
        viewModelScope.launch {
            controlRobotUseCase.getRobotState().collect { status ->
                _robotState.value = RobotState(
                    status = if (status.isMoving) "运行中" else "空闲",
                    batteryLevel = (status.batteryLevel * 100).toInt(),
                    isConnected = status.isConnected,
                    currentSpeed = status.currentSpeed,
                    direction = status.currentDirection ?: MovementDirection.STOP,
                    isMoving = status.isMoving,
                    error = status.error
                )
            }
        }
    }

    fun initialize() {
        viewModelScope.launch {
            try {
                controlRobotUseCase.initialize()
            } catch (e: Exception) {
                _robotState.value = _robotState.value.copy(
                    error = "初始化失败: ${e.message}"
                )
            }
        }
    }

    fun release() {
        viewModelScope.launch {
            try {
                controlRobotUseCase.release()
            } catch (e: Exception) {
                _robotState.value = _robotState.value.copy(
                    error = "关闭失败: ${e.message}"
                )
            }
        }
    }
} 