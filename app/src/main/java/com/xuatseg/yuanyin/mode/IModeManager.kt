package com.xuatseg.yuanyin.mode

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 模式管理接口
 */
interface IModeManager {
    /**
     * 切换模式
     * @param mode 目标模式
     */
    suspend fun switchMode(mode: ProcessingMode)

    /**
     * 获取当前模式
     * @return 当前模式
     */
    fun getCurrentMode(): ProcessingMode

    /**
     * 观察模式变化
     * @return 模式状态流
     */
    fun observeMode(): Flow<ProcessingMode>

    /**
     * 检查模式是否可用
     * @param mode 要检查的模式
     * @return 是否可用
     */
    suspend fun isModeAvailable(mode: ProcessingMode): Boolean

    /**
     * 获取模式配置
     * @param mode 目标模式
     * @return 模式配置
     */
    fun getModeConfig(mode: ProcessingMode): ModeConfig
}

/**
 * 处理模式
 */
enum class ProcessingMode {
    LOCAL,      // 本地模式
    LLM,        // LLM API模式
    HYBRID,     // 混合模式
    AUTO        // 自动选择
}

/**
 * 模式配置
 */
data class ModeConfig(
    val mode: ProcessingMode,
    val parameters: Map<String, Any>,
    val restrictions: ModeRestrictions,
    val features: Set<ModeFeature>
)

/**
 * 模式限制
 */
data class ModeRestrictions(
    val maxInputLength: Int,
    val maxConcurrentRequests: Int,
    val requestsPerMinute: Int,
    val requiresNetwork: Boolean
)

/**
 * 模式特性
 */
enum class ModeFeature {
    TEXT_PROCESSING,
    VOICE_PROCESSING,
    IMAGE_PROCESSING,
    CODE_PROCESSING,
    FUNCTION_CALLING
}

/**
 * 模式状态
 */
data class ModeState(
    val currentMode: ProcessingMode,
    val isAvailable: Boolean,
    val status: ModeStatus,
    val error: String? = null
)

/**
 * 模式状态
 */
enum class ModeStatus {
    ACTIVE,
    INACTIVE,
    SWITCHING,
    ERROR
}

/**
 * 模式切换监听器
 */
interface IModeSwitchListener {
    /**
     * 模式切换开始
     * @param fromMode 源模式
     * @param toMode 目标模式
     */
    fun onModeSwitchStart(fromMode: ProcessingMode, toMode: ProcessingMode)

    /**
     * 模式切换完成
     * @param newMode 新模式
     */
    fun onModeSwitchComplete(newMode: ProcessingMode)

    /**
     * 模式切换失败
     * @param error 错误信息
     */
    fun onModeSwitchError(error: String)
}

/**
 * 模式持久化接口
 */
interface IModePersistence {
    /**
     * 保存模式设置
     * @param mode 模式
     * @param config 配置
     */
    suspend fun saveMode(mode: ProcessingMode, config: ModeConfig)

    /**
     * 加载模式设置
     * @return 模式和配置
     */
    suspend fun loadMode(): Pair<ProcessingMode, ModeConfig>?

    /**
     * 清除保存的模式设置
     */
    suspend fun clearMode()
}

/**
 * 模式监控接口
 */
interface IModeMonitor {
    /**
     * 记录模式切换
     * @param fromMode 源模式
     * @param toMode 目标模式
     * @param duration 切换耗时
     */
    fun recordModeSwitch(
        fromMode: ProcessingMode,
        toMode: ProcessingMode,
        duration: Long
    )

    /**
     * 记录模式使用
     * @param mode 模式
     * @param metrics 使用指标
     */
    fun recordModeUsage(mode: ProcessingMode, metrics: ModeMetrics)

    /**
     * 获取模式统计信息
     * @param mode 模式
     * @return 统计信息
     */
    fun getModeStatistics(mode: ProcessingMode): ModeStatistics
}

/**
 * 模式使用指标
 */
data class ModeMetrics(
    val requestCount: Int,
    val successRate: Float,
    val averageLatency: Long,
    val errorCount: Int,
    val resourceUsage: ResourceUsage
)

/**
 * 资源使用情况
 */
data class ResourceUsage(
    val cpuUsage: Float,
    val memoryUsage: Long,
    val networkUsage: Long,
    val batteryImpact: Float
)

/**
 * 模式统计信息
 */
data class ModeStatistics(
    val totalUsageTime: Long,
    val switchCount: Int,
    val averageSwitchDuration: Long,
    val successRate: Float,
    val metrics: ModeMetrics
)

/**
 * 最简实现，仅用于UI编译通过
 */
class ModeManagerStub : IModeManager {
    private val mode = ProcessingMode.LOCAL
    private val modeFlow = MutableStateFlow(mode)
    override suspend fun switchMode(mode: ProcessingMode) {}
    override fun getCurrentMode() = mode
    override fun observeMode(): Flow<ProcessingMode> = modeFlow.asStateFlow()
    override suspend fun isModeAvailable(mode: ProcessingMode) = true
    override fun getModeConfig(mode: ProcessingMode) = ModeConfig(
        mode = mode,
        parameters = emptyMap(),
        restrictions = ModeRestrictions(1024, 1, 60, false),
        features = emptySet()
    )
}

class ModeSwitchListenerStub : IModeSwitchListener {
    override fun onModeSwitchStart(fromMode: ProcessingMode, toMode: ProcessingMode) {}
    override fun onModeSwitchComplete(newMode: ProcessingMode) {}
    override fun onModeSwitchError(error: String) {}
}

class ModePersistenceStub : IModePersistence {
    override suspend fun saveMode(mode: ProcessingMode, config: ModeConfig) {}
    override suspend fun loadMode(): Pair<ProcessingMode, ModeConfig>? = null
    override suspend fun clearMode() {}
}

class ModeMonitorStub : IModeMonitor {
    override fun recordModeSwitch(fromMode: ProcessingMode, toMode: ProcessingMode, duration: Long) {}
    override fun recordModeUsage(mode: ProcessingMode, metrics: ModeMetrics) {}
    override fun getModeStatistics(mode: ProcessingMode) = ModeStatistics(
        totalUsageTime = 0,
        switchCount = 0,
        averageSwitchDuration = 0,
        successRate = 0f,
        metrics = ModeMetrics(
            requestCount = 0,
            successRate = 0f,
            averageLatency = 0,
            errorCount = 0,
            resourceUsage = ResourceUsage(
                cpuUsage = 0f,
                memoryUsage = 0,
                networkUsage = 0,
                batteryImpact = 0f
            )
        )
    )
}

// 在需要的地方直接 new 这些 Stub 类用于依赖注入或 UI 预览
