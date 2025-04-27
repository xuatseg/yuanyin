package com.xuatseg.yuanyin.ui.control

import androidx.compose.runtime.Composable
import com.xuatseg.yuanyin.robot.*
import kotlinx.coroutines.flow.Flow

/**
 * 机器人控制UI状态
 */
data class RobotControlUiState(
    val robotState: RobotState,
    val sensorData: Map<SensorType, SensorData>,
    val systemHealth: SystemHealth,
    val isConnected: Boolean,
    val error: String? = null
)

/**
 * 机器人控制事件
 */
sealed class RobotControlEvent {
    data class Move(val command: MovementCommand) : RobotControlEvent()
    data class Rotate(val command: RotationCommand) : RobotControlEvent()
    data class SetSpeed(val command: SpeedCommand) : RobotControlEvent()
    object Stop : RobotControlEvent()
    object ClearError : RobotControlEvent()
}

/**
 * 机器人控制视图模型接口
 */
interface IRobotControlViewModel {
    /**
     * 获取UI状态流
     */
    fun getUiState(): Flow<RobotControlUiState>

    /**
     * 处理控制事件
     */
    fun handleControlEvent(event: RobotControlEvent)

    /**
     * 获取系统健康状态
     */
    fun getSystemHealth(): SystemHealth

    /**
     * 获取传感器数据
     */
    fun getSensorData(): Map<SensorType, SensorData>
}

/**
 * 机器人控制面板
 */
@Composable
fun RobotControlPanel(
    uiState: RobotControlUiState,
    onControlEvent: (RobotControlEvent) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)

/**
 * 方向控制按钮组
 */
@Composable
fun DirectionalControls(
    onMove: (MovementCommand) -> Unit,
    isEnabled: Boolean = true
)

/**
 * 速度控制滑块
 */
@Composable
fun SpeedControl(
    currentSpeed: SpeedCommand,
    onSpeedChange: (SpeedCommand) -> Unit,
    isEnabled: Boolean = true
)

/**
 * 旋转控制器
 */
@Composable
fun RotationControl(
    onRotate: (RotationCommand) -> Unit,
    isEnabled: Boolean = true
)

/**
 * 紧急停止按钮
 */
@Composable
fun EmergencyStopButton(
    onStop: () -> Unit,
    isEnabled: Boolean = true
)

/**
 * 状态显示面板
 */
@Composable
fun StatusDisplay(
    robotState: RobotState,
    systemHealth: SystemHealth
)

/**
 * 传感器数据显示
 */
@Composable
fun SensorDataDisplay(
    sensorData: Map<SensorType, SensorData>,
    selectedSensors: Set<SensorType>
)

/**
 * 健康状态指示器
 */
@Composable
fun HealthIndicator(
    health: SystemHealth,
    showDetails: Boolean = false
)

/**
 * 错误提示条
 */
@Composable
fun ErrorSnackbar(
    error: String,
    onDismiss: () -> Unit
)

/**
 * 电池状态指示器
 */
@Composable
fun BatteryIndicator(
    level: Float,
    isCharging: Boolean
)

/**
 * 位置显示器
 */
@Composable
fun PositionDisplay(
    position: Position,
    orientation: Orientation
)

/**
 * 速度显示器
 */
@Composable
fun SpeedDisplay(
    speed: Speed,
    showGraph: Boolean = false
)

/**
 * 模式选择器
 */
@Composable
fun OperationModeSelector(
    currentMode: RobotStatus,
    availableModes: List<RobotStatus>,
    onModeSelect: (RobotStatus) -> Unit
)

/**
 * 传感器选择器
 */
@Composable
fun SensorSelector(
    availableSensors: List<SensorType>,
    selectedSensors: Set<SensorType>,
    onSelectionChange: (Set<SensorType>) -> Unit
)

/**
 * 诊断信息显示
 */
@Composable
fun DiagnosticsDisplay(
    diagnostics: DiagnosticResult,
    showFullReport: Boolean = false
)

/**
 * 控制模式切换按钮
 */
@Composable
fun ControlModeToggle(
    isManualMode: Boolean,
    onModeToggle: (Boolean) -> Unit
)

/**
 * 自定义控制面板
 */
@Composable
fun CustomControlPanel(
    commands: List<RobotCommand>,
    onCommandExecute: (RobotCommand) -> Unit
)

/**
 * 机器人状态图表
 */
@Composable
fun RobotStateChart(
    stateHistory: List<RobotState>,
    timeRange: Long
)

/**
 * 控制日志显示
 */
@Composable
fun ControlLogDisplay(
    logs: List<ControlLog>,
    filter: LogFilter? = null
)

/**
 * 日志过滤器
 */
data class LogFilter(
    val severity: Set<ErrorSeverity>,
    val timeRange: ClosedRange<Long>,
    val components: Set<ComponentType>
)

/**
 * 控制日志
 */
data class ControlLog(
    val timestamp: Long,
    val action: String,
    val result: String,
    val severity: ErrorSeverity
)
