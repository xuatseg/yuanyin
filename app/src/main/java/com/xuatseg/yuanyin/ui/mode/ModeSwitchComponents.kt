package com.xuatseg.yuanyin.ui.mode

import androidx.compose.runtime.Composable
import com.xuatseg.yuanyin.mode.ModeConfig
import com.xuatseg.yuanyin.mode.ModeState
import com.xuatseg.yuanyin.mode.ProcessingMode
import kotlinx.coroutines.flow.Flow

/**
 * 模式切换界面状态
 */
data class ModeSwitchUiState(
    val currentMode: ProcessingMode,
    val availableModes: List<ProcessingMode>,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 模式切换事件
 */
sealed class ModeSwitchEvent {
    data class SwitchMode(val mode: ProcessingMode) : ModeSwitchEvent()
    object DismissError : ModeSwitchEvent()
    data class UpdateConfig(val config: ModeConfig) : ModeSwitchEvent()
}

/**
 * 模式切换视图模型接口
 */
interface IModeSwitchViewModel {
    /**
     * 获取UI状态流
     */
    fun getUiState(): Flow<ModeSwitchUiState>

    /**
     * 处理事件
     * @param event 事件
     */
    fun handleEvent(event: ModeSwitchEvent)

    /**
     * 获取当前模式状态
     */
    fun getCurrentModeState(): ModeState

    /**
     * 获取可用模式列表
     */
    fun getAvailableModes(): List<ProcessingMode>
}

/**
 * 模式切换按钮组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeSwitchButton(
    currentMode: ProcessingMode,
    onModeSwitch: (ProcessingMode) -> Unit,
    isEnabled: Boolean = true,
    showLabel: Boolean = true
)

/**
 * 模式状态指示器组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeStatusIndicator(
    modeState: ModeState,
    showDetails: Boolean = false
)

/**
 * 模式设置对话框组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeSettingsDialog(
    currentConfig: ModeConfig,
    onConfigChange: (ModeConfig) -> Unit,
    onDismiss: () -> Unit
)

/**
 * 模式选择器组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeSelector(
    availableModes: List<ProcessingMode>,
    selectedMode: ProcessingMode,
    onModeSelect: (ProcessingMode) -> Unit
)

/**
 * 模式配置表单组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeConfigForm(
    config: ModeConfig,
    onConfigUpdate: (ModeConfig) -> Unit
)

/**
 * 模式状态栏组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeStatusBar(
    modeState: ModeState,
    onActionClick: () -> Unit
)

/**
 * 模式切换确认对话框组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeSwitchConfirmDialog(
    targetMode: ProcessingMode,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)

/**
 * 模式错误提示组件
 */
@Composable
fun ModeErrorSnackbar(
    error: String,
    onDismiss: () -> Unit
)

/**
 * 模式特性标签组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeFeatureChip(
    feature: String,
    isEnabled: Boolean
)

/**
 * 模式性能指标组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModePerformanceMetrics(
    metrics: Map<String, Float>,
    showChart: Boolean = false
)

/**
 * 模式切换动画组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeSwitchAnimation(
    isLoading: Boolean,
    progress: Float
)

/**
 * 模式比较表格组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeComparisonTable(
    modes: List<ProcessingMode>,
    features: Map<ProcessingMode, List<String>>
)

/**
 * 模式帮助提示组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeHelpTooltip(
    mode: ProcessingMode,
    description: String
)

/**
 * 模式设置项组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeSettingItem(
    title: String,
    description: String,
    value: Any,
    onValueChange: (Any) -> Unit
)

/**
 * 模式统计图表组件
 * todo: 实现函数体，否则编译不通过
 */
@Composable
fun ModeStatisticsChart(
    statistics: Map<ProcessingMode, Map<String, Float>>,
    chartType: ChartType
)

/**
 * 图表类型
 */
enum class ChartType {
    BAR,
    LINE,
    PIE,
    RADAR
}
