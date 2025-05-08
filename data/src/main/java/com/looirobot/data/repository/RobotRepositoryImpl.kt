package com.looirobot.data.repository

import com.looirobot.core.robot_control.RobotControl
import com.looirobot.domain.model.MovementDirection
import com.looirobot.domain.model.RobotStatus
import com.looirobot.domain.repository.RobotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RobotRepositoryImpl @Inject constructor(
    private val robotControl: RobotControl
) : RobotRepository {

    override fun getRobotState(): Flow<RobotStatus> {
        return robotControl.getRobotStatus().map { status ->
            RobotStatus(
                isMoving = status.isMoving,
                currentSpeed = status.currentSpeed,
                currentDirection = status.currentDirection,
                batteryLevel = status.batteryLevel,
                error = status.error,
                isConnected = status.isConnected
            )
        }
    }

    override suspend fun move(direction: MovementDirection, speed: Float) {
        robotControl.move(direction, speed)
    }

    override suspend fun stop() {
        robotControl.move(MovementDirection.STOP, 0f)
    }

    override suspend fun rotate(angle: Float) {
        robotControl.rotate(angle)
    }

    override suspend fun setSpeed(speed: Float) {
        robotControl.setSpeed(speed)
    }

    override suspend fun initialize() {
        // 暂时不实现
    }

    override suspend fun release() {
        // 暂时不实现
    }
} 