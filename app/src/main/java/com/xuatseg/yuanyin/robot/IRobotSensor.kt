package com.xuatseg.yuanyin.robot

import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * 机器人传感器接口
 */
interface IRobotSensor {
    /**
     * 获取所有传感器数据
     * @return 传感器数据流
     */
    fun getAllSensorData(): Flow<Map<SensorType, SensorData>>

    /**
     * 获取特定传感器数据
     * @param type 传感器类型
     * @return 传感器数据流
     */
    fun getSensorData(type: SensorType): Flow<SensorData>

    /**
     * 配置传感器
     * @param config 传感器配置
     */
    suspend fun configureSensor(config: SensorConfig)

    /**
     * 校准传感器
     * @param type 传感器类型
     */
    suspend fun calibrateSensor(type: SensorType)

    /**
     * 检查传感器状态
     * @param type 传感器类型
     * @return 传感器状态
     */
    fun checkSensorStatus(type: SensorType): SensorStatus
}

/**
 * 传感器类型
 */
enum class SensorType {
    IMU,                // 惯性测量单元
    LIDAR,             // 激光雷达
    CAMERA,            // 相机
    ULTRASONIC,        // 超声波
    INFRARED,          // 红外
    TEMPERATURE,       // 温度
    HUMIDITY,          // 湿度
    PRESSURE,          // 压力
    BATTERY,           // 电池
    ENCODER,           // 编码器
    TOUCH,             // 触摸
    GPS,               // GPS
    COMPASS,           // 罗盘
    ACCELEROMETER,     // 加速度计
    GYROSCOPE          // 陀螺仪
}

/**
 * 传感器数据
 */
sealed class SensorData {
    abstract val timestamp: Instant
    abstract val sensorType: SensorType
    abstract val reliability: Float  // 0.0 to 1.0

    data class IMUData(
        override val timestamp: Instant,
        val acceleration: Vector3D,
        val gyroscope: Vector3D,
        val magnetometer: Vector3D,
        override val reliability: Float
    ) : SensorData() {
        override val sensorType = SensorType.IMU
    }

    data class LidarData(
        override val timestamp: Instant,
        val pointCloud: List<Point3D>,
        val resolution: Float,
        override val reliability: Float
    ) : SensorData() {
        override val sensorType = SensorType.LIDAR
    }

    data class CameraData(
        override val timestamp: Instant,
        val imageData: ByteArray,
        val resolution: Resolution,
        val format: ImageFormat,
        override val reliability: Float
    ) : SensorData() {
        override val sensorType = SensorType.CAMERA
    }

    data class EnvironmentalData(
        override val timestamp: Instant,
        override val sensorType: SensorType,
        val value: Float,
        val unit: String,
        override val reliability: Float
    ) : SensorData()
}

/**
 * 传感器配置
 */
data class SensorConfig(
    val sensorType: SensorType,
    val samplingRate: Int,
    val resolution: Resolution? = null,
    val range: Range? = null,
    val filters: List<SensorFilter> = emptyList(),
    val calibrationData: Map<String, Any>? = null
)

/**
 * 传感器状态
 */
data class SensorStatus(
    val isActive: Boolean,
    val errorCode: Int?,
    val lastCalibration: Instant?,
    val healthStatus: HealthStatus,
    val diagnostics: Map<String, Any>
)

/**
 * 健康状态
 */
enum class HealthStatus {
    GOOD,
    FAIR,
    POOR,
    CRITICAL,
    UNKNOWN
}

/**
 * 传感器监控接口
 */
interface ISensorMonitor {
    /**
     * 开始监控
     */
    fun startMonitoring()

    /**
     * 停止监控
     */
    fun stopMonitoring()

    /**
     * 获取监控数据
     */
    fun getMonitoringData(): Flow<MonitoringData>

    /**
     * 设置告警规则
     */
    fun setAlertRules(rules: List<AlertRule>)
}

/**
 * 监控数据
 */
data class MonitoringData(
    val timestamp: Instant,
    val sensorReadings: Map<SensorType, SensorData>,
    val alerts: List<Alert>,
    val statistics: SensorStatistics
)

/**
 * 传感器统计
 */
data class SensorStatistics(
    val sampleCount: Long,
    val errorRate: Float,
    val latency: Long,
    val accuracy: Float,
    val uptime: Long
)

/**
 * 告警规则
 */
data class AlertRule(
    val sensorType: SensorType,
    val condition: AlertCondition,
    val threshold: Any,
    val priority: AlertPriority
)

/**
 * 告警条件
 */
sealed class AlertCondition {
    data class Threshold(val operator: ThresholdOperator, val value: Float) : AlertCondition()
    data class Range(val min: Float, val max: Float) : AlertCondition()
    data class Pattern(val pattern: String) : AlertCondition()
}

/**
 * 阈值操作符
 */
enum class ThresholdOperator {
    GREATER_THAN,
    LESS_THAN,
    EQUALS,
    NOT_EQUALS
}

/**
 * 告警优先级
 */
enum class AlertPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * 告警
 */
data class Alert(
    val sensorType: SensorType,
    val message: String,
    val priority: AlertPriority,
    val timestamp: Instant,
    val value: Any
)

/**
 * 辅助数据类
 */
data class Vector3D(
    val x: Float,
    val y: Float,
    val z: Float
)

data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float,
    val intensity: Float? = null
)

data class Resolution(
    val width: Int,
    val height: Int
)

data class Range(
    val min: Float,
    val max: Float
)

enum class ImageFormat {
    RGB,
    BGR,
    GRAY,
    YUV
}

/**
 * 传感器过滤器
 */
sealed class SensorFilter {
    data class MovingAverage(val windowSize: Int) : SensorFilter()
    data class Kalman(val processNoise: Float, val measurementNoise: Float) : SensorFilter()
    data class MedianFilter(val windowSize: Int) : SensorFilter()
    data class LowPass(val cutoffFrequency: Float) : SensorFilter()
    data class HighPass(val cutoffFrequency: Float) : SensorFilter()
}
