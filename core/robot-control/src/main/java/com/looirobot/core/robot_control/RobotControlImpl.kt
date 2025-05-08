/**
 * 机器人控制核心实现类
 * 
 * 该实现类负责：
 * 1. 实现机器人控制接口定义的所有功能
 * 2. 管理机器人状态
 * 3. 处理运动控制
 * 4. 错误处理
 * 
 * 技术特点：
 * - 使用 StateFlow 管理状态
 * - 支持实时运动控制
 * - 支持传感器数据监控
 * - 完整的错误处理机制
 */
package com.looirobot.core.robot_control

import android.content.Context
import com.looirobot.core.robot_control.hardware.MotorController
import com.looirobot.core.robot_control.hardware.SensorManager
import com.looirobot.domain.model.MovementDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RobotControlImpl @Inject constructor(
    private val context: Context,
    private val motorController: MotorController,
    private val sensorManager: SensorManager
) : RobotControl {
    private val _robotStatus = MutableStateFlow(
        RobotStatus(
            isMoving = false,
            currentSpeed = 0f,
            currentDirection = null,
            batteryLevel = 1.0f,
            error = null,
            isConnected = true
        )
    )
    
    private var isInitialized = false
    
    suspend fun initialize() {
        if (!isInitialized) {
            try {
                motorController.initialize()
                sensorManager.initialize()
                isInitialized = true
            } catch (e: Exception) {
                handleError(e)
                throw ControlError.MotorError("Failed to initialize hardware: ${e.message}")
            }
        }
    }
    
    override fun getRobotStatus(): Flow<RobotStatus> = _robotStatus.asStateFlow()
    
    override suspend fun move(direction: MovementDirection, speed: Float) {
        try {
            checkInitialized()
            validateSpeed(speed)
            validateBatteryLevel()
            
            when (direction) {
                MovementDirection.FORWARD -> motorController.moveForward(speed)
                MovementDirection.BACKWARD -> motorController.moveBackward(speed)
                MovementDirection.LEFT -> motorController.turnLeft(speed)
                MovementDirection.RIGHT -> motorController.turnRight(speed)
                MovementDirection.STOP -> stop()
            }
            
            _robotStatus.value = _robotStatus.value.copy(
                isMoving = true,
                currentSpeed = speed,
                currentDirection = direction
            )
        } catch (e: Exception) {
            handleError(e)
            throw ControlError.MovementError("Failed to move: ${e.message}")
        }
    }
    
    override suspend fun rotate(angle: Float) {
        try {
            checkInitialized()
            validateBatteryLevel()
            
            motorController.rotate(angle)
            
            _robotStatus.value = _robotStatus.value.copy(
                isMoving = true,
                currentDirection = if (angle > 0) MovementDirection.RIGHT else MovementDirection.LEFT
            )
        } catch (e: Exception) {
            handleError(e)
            throw ControlError.MovementError("Failed to rotate: ${e.message}")
        }
    }
    
    override suspend fun stop() {
        try {
            checkInitialized()
            motorController.stop()
            
            _robotStatus.value = _robotStatus.value.copy(
                isMoving = false,
                currentSpeed = 0f,
                currentDirection = MovementDirection.STOP
            )
        } catch (e: Exception) {
            handleError(e)
            throw ControlError.MovementError("Failed to stop: ${e.message}")
        }
    }
    
    override suspend fun setSpeed(speed: Float) {
        try {
            checkInitialized()
            validateSpeed(speed)
            
            motorController.setSpeed(speed)
            
            _robotStatus.value = _robotStatus.value.copy(
                currentSpeed = speed
            )
        } catch (e: Exception) {
            handleError(e)
            throw ControlError.MovementError("Failed to set speed: ${e.message}")
        }
    }
    
    override suspend fun getSensorData(): SensorData {
        try {
            checkInitialized()
            validateBatteryLevel()
            
            return sensorManager.getSensorData()
        } catch (e: Exception) {
            handleError(e)
            throw ControlError.SensorError("Failed to get sensor data: ${e.message}")
        }
    }
    
    private fun checkInitialized() {
        if (!isInitialized) {
            throw ControlError.MotorError("Hardware not initialized")
        }
    }
    
    private fun validateSpeed(speed: Float) {
        if (speed !in 0f..1f) {
            throw ControlError.MovementError("Speed must be between 0.0 and 1.0")
        }
    }
    
    private fun validateBatteryLevel() {
        if (_robotStatus.value.batteryLevel < 0.1f) {
            throw ControlError.BatteryError("Battery level too low")
        }
    }
    
    private fun handleError(error: Exception) {
        _robotStatus.value = _robotStatus.value.copy(
            error = error.message
        )
    }
    
    suspend fun release() {
        if (isInitialized) {
            try {
                stop()
                motorController.release()
                sensorManager.release()
                isInitialized = false
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
} 