package com.looirobot.domain.repository

import com.looirobot.domain.model.MovementDirection
import com.looirobot.domain.model.RobotStatus
import kotlinx.coroutines.flow.Flow

interface RobotRepository {
    fun getRobotState(): Flow<RobotStatus>
    suspend fun move(direction: MovementDirection, speed: Float)
    suspend fun stop()
    suspend fun rotate(angle: Float)
    suspend fun setSpeed(speed: Float)
    suspend fun initialize()
    suspend fun release()
} 