package com.looirobot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.looirobot.domain.model.MovementDirection
import com.looirobot.domain.repository.RobotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val robotRepository: RobotRepository
) : ViewModel() {

    val robotState: StateFlow<RobotState> = robotRepository.getRobotState()
        .map { status ->
            RobotState(
                status = if (status.isMoving) "运动中" else "空闲",
                batteryLevel = (status.batteryLevel * 100).toInt(),
                isConnected = status.isConnected,
                currentSpeed = status.currentSpeed,
                direction = status.currentDirection ?: MovementDirection.STOP,
                error = status.error
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RobotState()
        )

    fun move(direction: MovementDirection, speed: Float) {
        viewModelScope.launch {
            robotRepository.move(direction, speed)
        }
    }

    fun stop() {
        viewModelScope.launch {
            robotRepository.stop()
        }
    }
}

data class RobotState(
    val status: String = "空闲",
    val batteryLevel: Int = 100,
    val isConnected: Boolean = false,
    val currentSpeed: Float = 0f,
    val direction: MovementDirection = MovementDirection.STOP,
    val error: String? = null
) 