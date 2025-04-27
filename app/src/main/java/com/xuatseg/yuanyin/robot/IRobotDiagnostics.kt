package com.xuatseg.yuanyin.robot

import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * 机器人诊断接口
 */
interface IRobotDiagnostics {
    /**
     * 执行系统诊断
     * @return 诊断结果
     */
    suspend fun performDiagnostics(): DiagnosticResult

    /**
     * 获取系统健康状态
     * @return 健康状态
     */
    fun getSystemHealth(): SystemHealth

    /**
     * 观察系统状态
     * @return 状态流
     */
    fun observeSystemStatus(): Flow<SystemStatus>

    /**
     * 获取错误日志
     * @return 错误日志列表
     */
    fun getErrorLogs(): List<ErrorLog>

    /**
     * 执行自检
     * @return 自检结果
     */
    suspend fun performSelfTest(): SelfTestResult
}

/**
 * 诊断结果
 */
data class DiagnosticResult(
    val timestamp: Instant,
    val overallStatus: HealthStatus,
    val componentResults: Map<ComponentType, ComponentDiagnostic>,
    val recommendations: List<Recommendation>
)

/**
 * 组件诊断
 */
data class ComponentDiagnostic(
    val status: HealthStatus,
    val issues: List<Issue>,
    val metrics: Map<String, Float>,
    val lastChecked: Instant
)

/**
 * 系统健康状态
 */
data class SystemHealth(
    val overallHealth: HealthStatus,
    val componentHealth: Map<ComponentType, HealthStatus>,
    val alerts: List<HealthAlert>,
    val metrics: SystemMetrics
)

/**
 * 系统状态
 */
data class SystemStatus(
    val timestamp: Instant,
    val operationalState: OperationalState,
    val errors: List<SystemError>,
    val warnings: List<SystemWarning>,
    val metrics: SystemMetrics
)

/**
 * 系统指标
 */
data class SystemMetrics(
    val cpuUsage: Float,
    val memoryUsage: Float,
    val batteryLevel: Float,
    val temperature: Float,
    val uptime: Long,
    val networkLatency: Long?
)

/**
 * 错误日志
 */
data class ErrorLog(
    val timestamp: Instant,
    val severity: ErrorSeverity,
    val componentType: ComponentType,
    val message: String,
    val stackTrace: String?,
    val context: Map<String, Any>
)

/**
 * 自检结果
 */
data class SelfTestResult(
    val timestamp: Instant,
    val success: Boolean,
    val testResults: Map<TestType, TestResult>,
    val duration: Long
)

/**
 * 测试结果
 */
data class TestResult(
    val status: TestStatus,
    val details: String?,
    val metrics: Map<String, Any>
)

/**
 * 组件类型
 */
enum class ComponentType {
    MOTOR,
    SENSOR,
    BATTERY,
    COMMUNICATION,
    PROCESSOR,
    STORAGE,
    NETWORK,
    SOFTWARE
}

/**
 * 操作状态
 */
enum class OperationalState {
    NORMAL,
    DEGRADED,
    CRITICAL,
    MAINTENANCE,
    OFFLINE
}

/**
 * 错误严重程度
 */
enum class ErrorSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

/**
 * 测试类型
 */
enum class TestType {
    HARDWARE,
    SOFTWARE,
    NETWORK,
    SENSOR,
    CALIBRATION,
    PERFORMANCE
}

/**
 * 测试状态
 */
enum class TestStatus {
    PASSED,
    FAILED,
    SKIPPED,
    ERROR
}

/**
 * 问题
 */
data class Issue(
    val code: String,
    val description: String,
    val severity: ErrorSeverity,
    val possibleCauses: List<String>,
    val suggestedActions: List<String>
)

/**
 * 建议
 */
data class Recommendation(
    val priority: Int,
    val action: String,
    val impact: String,
    val deadline: Instant?
)

/**
 * 健康告警
 */
data class HealthAlert(
    val timestamp: Instant,
    val componentType: ComponentType,
    val severity: ErrorSeverity,
    val message: String,
    val metrics: Map<String, Any>
)

/**
 * 系统错误
 */
data class SystemError(
    val code: String,
    val message: String,
    val componentType: ComponentType,
    val timestamp: Instant,
    val context: Map<String, Any>
)

/**
 * 系统警告
 */
data class SystemWarning(
    val code: String,
    val message: String,
    val componentType: ComponentType,
    val timestamp: Instant
)

/**
 * 诊断监听器
 */
interface IDiagnosticListener {
    /**
     * 诊断完成回调
     */
    fun onDiagnosticComplete(result: DiagnosticResult)

    /**
     * 健康状态变化回调
     */
    fun onHealthStatusChanged(health: SystemHealth)

    /**
     * 错误发生回调
     */
    fun onErrorOccurred(error: SystemError)

    /**
     * 警告发生回调
     */
    fun onWarningOccurred(warning: SystemWarning)
}

/**
 * 诊断配置接口
 */
interface IDiagnosticConfig {
    /**
     * 获取诊断间隔
     */
    fun getDiagnosticInterval(): Long

    /**
     * 获取告警阈值
     */
    fun getAlertThresholds(): Map<ComponentType, Map<String, Float>>

    /**
     * 获取自检配置
     */
    fun getSelfTestConfig(): SelfTestConfig
}

/**
 * 自检配置
 */
data class SelfTestConfig(
    val enabledTests: Set<TestType>,
    val timeout: Long,
    val retryCount: Int,
    val parallelExecution: Boolean
)

/**
 * 诊断数据收集器
 */
interface IDiagnosticDataCollector {
    /**
     * 收集组件数据
     */
    suspend fun collectComponentData(componentType: ComponentType): Map<String, Any>

    /**
     * 收集性能指标
     */
    fun collectPerformanceMetrics(): Flow<SystemMetrics>

    /**
     * 收集错误日志
     */
    fun collectErrorLogs(): Flow<ErrorLog>
}
