package com.looirobot.presentation.model

import com.looirobot.domain.model.MovementDirection

data class RobotState(
    val status: String = "空闲",
    val batteryLevel: Int = 100,
    val isConnected: Boolean = false,
    val currentSpeed: Float = 0f,
    val direction: MovementDirection = MovementDirection.STOP,
    val isMoving: Boolean = false,
    val error: String? = null
) 