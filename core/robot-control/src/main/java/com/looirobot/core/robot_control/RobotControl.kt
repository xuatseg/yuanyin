/**
 * 机器人控制核心接口定义
 * 
 * 该接口定义了机器人控制功能的核心操作：
 * 1. 运动控制（移动/转向）
 * 2. 状态监控
 * 3. 传感器数据获取
 * 4. 错误处理
 * 
 * 包含的数据类：
 * - RobotStatus：机器人状态
 * - MovementCommand：运动命令
 * - SensorData：传感器数据
 * - ControlError：错误类型定义
 */
package com.looirobot.core.robot_control

import com.looirobot.domain.model.MovementDirection
import kotlinx.coroutines.flow.Flow

interface RobotControl {
    suspend fun move(direction: MovementDirection, speed: Float)
    suspend fun rotate(angle: Float)
    suspend fun stop()
    suspend fun setSpeed(speed: Float)
    suspend fun getSensorData(): SensorData
    fun getRobotStatus(): Flow<RobotStatus>
}

data class RobotStatus(
    val isMoving: Boolean = false,
    val currentSpeed: Float = 0f,
    val currentDirection: MovementDirection? = null,
    val batteryLevel: Float = 1.0f,
    val error: String? = null,
    val isConnected: Boolean = false
)

data class SensorData(
    val distance: Float = 0f,
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val batteryVoltage: Float = 0f,
    val motorCurrent: Float = 0f
)

sealed class ControlError : Exception() {
    data class MovementError(override val message: String) : ControlError()
    data class SensorError(override val message: String) : ControlError()
    data class BatteryError(override val message: String) : ControlError()
    data class MotorError(override val message: String) : ControlError()
} 