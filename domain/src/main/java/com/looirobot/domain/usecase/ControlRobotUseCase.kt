package com.looirobot.domain.usecase

import com.looirobot.domain.model.MovementDirection
import com.looirobot.domain.model.RobotStatus
import com.looirobot.domain.repository.RobotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ControlRobotUseCase @Inject constructor(
    private val robotRepository: RobotRepository
) {
    fun getRobotState(): Flow<RobotStatus> {
        return robotRepository.getRobotState()
    }

    suspend fun move(direction: MovementDirection, speed: Float) {
        robotRepository.move(direction, speed)
    }
    
    suspend fun rotate(angle: Float) {
        robotRepository.rotate(angle)
    }
    
    suspend fun stop() {
        robotRepository.stop()
    }
    
    suspend fun setSpeed(speed: Float) {
        robotRepository.setSpeed(speed)
    }
    
    suspend fun initialize() {
        robotRepository.initialize()
    }
    
    suspend fun release() {
        robotRepository.release()
    }
} 