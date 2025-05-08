package com.looirobot.domain.model

data class RobotStatus(
    val isMoving: Boolean = false,
    val currentSpeed: Float = 0f,
    val currentDirection: MovementDirection? = null,
    val batteryLevel: Float = 1.0f,
    val error: String? = null,
    val isConnected: Boolean = false
)

enum class MovementDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    STOP
} 