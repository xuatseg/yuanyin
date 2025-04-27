package com.xuatseg.yuanyin.robot

import kotlinx.coroutines.flow.Flow

/**
 * 机器人控制接口
 */
interface IRobotControl {
    /**
     * 移动控制
     * @param movement 移动命令
     */
    suspend fun move(movement: MovementCommand)

    /**
     * 停止移动
     */
    suspend fun stop()

    /**
     * 转向控制
     * @param rotation 转向命令
     */
    suspend fun rotate(rotation: RotationCommand)

    /**
     * 速度控制
     * @param speed 速度命令
     */
    suspend fun setSpeed(speed: SpeedCommand)

    /**
     * 获取当前状态
     * @return 机器人状态
     */
    fun getRobotState(): RobotState

    /**
     * 观察状态变化
     * @return 状态流
     */
    fun observeState(): Flow<RobotState>

    /**
     * 执行自定义命令
     * @param command 自定义命令
     */
    suspend fun executeCommand(command: RobotCommand)
}

/**
 * 移动命令
 */
sealed class MovementCommand {
    data class Forward(val distance: Float? = null) : MovementCommand()
    data class Backward(val distance: Float? = null) : MovementCommand()
    data class Left(val distance: Float? = null) : MovementCommand()
    data class Right(val distance: Float? = null) : MovementCommand()
    data class Custom(val x: Float, val y: Float) : MovementCommand()
}

/**
 * 转向命令
 */
sealed class RotationCommand {
    data class Angle(val degrees: Float) : RotationCommand()
    data class Direction(val direction: RotationDirection) : RotationCommand()
    data class Custom(val x: Float, val y: Float, val z: Float) : RotationCommand()
}

/**
 * 转向方向
 */
enum class RotationDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN
}

/**
 * 速度命令
 */
data class SpeedCommand(
    val linear: Float,    // 线速度 (-1.0 到 1.0)
    val angular: Float    // 角速度 (-1.0 到 1.0)
)

/**
 * 机器人状态
 */
data class RobotState(
    val position: Position,
    val orientation: Orientation,
    val speed: Speed,
    val batteryLevel: Float,
    val status: RobotStatus,
    val error: RobotError?
)

/**
 * 位置信息
 */
data class Position(
    val x: Float,
    val y: Float,
    val z: Float
)

/**
 * 方向信息
 */
data class Orientation(
    val roll: Float,
    val pitch: Float,
    val yaw: Float
)

/**
 * 速度信息
 */
data class Speed(
    val linear: Float,
    val angular: Float
)

/**
 * 机器人状态枚举
 */
enum class RobotStatus {
    IDLE,           // 空闲
    MOVING,         // 移动中
    ROTATING,       // 转向中
    CHARGING,       // 充电中
    ERROR           // 错误
}

/**
 * 机器人错误
 */
sealed class RobotError {
    object MotorError : RobotError()
    object SensorError : RobotError()
    object BatteryError : RobotError()
    object CommunicationError : RobotError()
    data class Custom(val code: Int, val message: String) : RobotError()
}

/**
 * 机器人命令
 */
interface RobotCommand {
    val commandType: CommandType
    val parameters: Map<String, Any>
}

/**
 * 命令类型
 */
enum class CommandType {
    MOVEMENT,
    ROTATION,
    SPEED,
    SENSOR,
    SYSTEM,
    CUSTOM
}

/**
 * 机器人配置接口
 */
interface IRobotConfig {
    /**
     * 获取最大速度
     */
    fun getMaxSpeed(): Speed

    /**
     * 获取加速度限制
     */
    fun getAccelerationLimits(): AccelerationLimits

    /**
     * 获取安全参数
     */
    fun getSafetyParameters(): SafetyParameters
}

/**
 * 加速度限制
 */
data class AccelerationLimits(
    val maxLinearAcceleration: Float,
    val maxAngularAcceleration: Float
)

/**
 * 安全参数
 */
data class SafetyParameters(
    val minObstacleDistance: Float,
    val emergencyStopDistance: Float,
    val maxOperatingTime: Long,
    val safetyChecks: Set<SafetyCheck>
)

/**
 * 安全检查
 */
enum class SafetyCheck {
    OBSTACLE_DETECTION,
    BATTERY_LEVEL,
    MOTOR_TEMPERATURE,
    TILT_PROTECTION,
    SPEED_LIMIT
}

/**
 * 机器人控制监听器
 */
interface IRobotControlListener {
    /**
     * 状态变化回调
     */
    fun onStateChanged(newState: RobotState)

    /**
     * 命令执行完成回调
     */
    fun onCommandCompleted(command: RobotCommand)

    /**
     * 错误回调
     */
    fun onError(error: RobotError)
}
