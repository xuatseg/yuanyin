# UI 模块

该模块使用Jetpack Compose实现了机器人控制系统的用户界面，提供了直观的控制界面和实时状态显示。

## 模块结构

### 目录组织
```
ui/
├── control/         # 机器人控制组件
├── mode/           # 模式切换组件
├── screens/        # 主屏幕界面
└── theme/          # 主题定义
```

## 核心组件

### MainScreen
主屏幕组件，整合了所有控制和显示功能：
- 顶部应用栏（含模式切换）
- 状态显示区
- 机器人控制面板
- 传感器数据显示
- 控制按钮组
- 系统健康状态显示

```kotlin
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modeSwitchViewModel: IModeSwitchViewModel,
    robotControlViewModel: IRobotControlViewModel
)
```

### 控制组件 (control/)

#### RobotControlPanel
机器人控制面板，提供主要控制功能：
```kotlin
@Composable
fun RobotControlPanel(
    uiState: RobotControlUiState,
    onControlEvent: (RobotControlEvent) -> Unit,
    modifier: Modifier = Modifier
)
```

#### DirectionalControls
方向控制按钮组：
```kotlin
@Composable
fun DirectionalControls(
    onMove: (MovementCommand) -> Unit,
    isEnabled: Boolean
)
```

#### SpeedControl
速度控制滑块：
```kotlin
@Composable
fun SpeedControl(
    currentSpeed: SpeedCommand,
    onSpeedChange: (SpeedCommand) -> Unit,
    isEnabled: Boolean
)
```

### 模式组件 (mode/)

#### ModeSwitchButton
模式切换按钮：
```kotlin
@Composable
fun ModeSwitchButton(
    currentMode: ProcessingMode,
    onModeSwitch: (ProcessingMode) -> Unit
)
```

#### ModeStatusIndicator
模式状态指示器：
```kotlin
@Composable
fun ModeStatusIndicator(
    modeState: ModeState,
    modifier: Modifier = Modifier
)
```

### 状态组件

#### BatteryIndicator
电池状态指示器：
```kotlin
@Composable
fun BatteryIndicator(
    level: Float,
    isCharging: Boolean
)
```

#### SensorDataDisplay
传感器数据显示：
```kotlin
@Composable
fun SensorDataDisplay(
    sensorData: Map<SensorType, SensorReading>,
    selectedSensors: Set<SensorType>
)
```

#### HealthIndicator
系统健康状态指示器：
```kotlin
@Composable
fun HealthIndicator(
    health: SystemHealth,
    showDetails: Boolean
)
```

## 状态管理

### UI状态模型

#### RobotControlUiState
机器人控制界面状态：
```kotlin
data class RobotControlUiState(
    val robotState: RobotState,
    val sensorData: Map<SensorType, SensorReading>,
    val systemHealth: SystemHealth,
    val isConnected: Boolean,
    val error: String? = null
)
```

#### ModeSwitchUiState
模式切换界面状态：
```kotlin
data class ModeSwitchUiState(
    val currentMode: ProcessingMode,
    val availableModes: List<ProcessingMode>,
    val error: String? = null
)
```

## 主题系统

### Theme
应用主题定义：
```kotlin
@Composable
fun YuanyinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)
```

## 使用示例

### 基本界面设置
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YuanyinTheme {
                MainScreen(
                    viewModel = viewModel,
                    modeSwitchViewModel = modeSwitchViewModel,
                    robotControlViewModel = robotControlViewModel
                )
            }
        }
    }
}
```

### 自定义控制面板
```kotlin
@Composable
fun CustomControlPanel() {
    Column {
        // 状态显示
        RobotStatusBar(...)
        
        // 控制按钮
        DirectionalControls(...)
        
        // 速度控制
        SpeedControl(...)
        
        // 系统健康状态
        HealthIndicator(...)
    }
}
```

## 实现建议

1. **组件设计**:
   - 保持组件职责单一
   - 实现合适的重组范围
   - 使用合适的状态提升

2. **性能优化**:
   - 使用remember和derivedStateOf
   - 避免不必要的重组
   - 实现LaunchedEffect优化

3. **用户体验**:
   - 添加适当的动画效果
   - 实现错误状态展示
   - 提供加载状态反馈

4. **可访问性**:
   - 添加内容描述
   - 支持大字体
   - 实现深色主题

## 最佳实践

1. **状态管理**:
   - 使用单向数据流
   - 实现状态提升
   - 避免状态重复

2. **组件复用**:
   - 提取共用组件
   - 使用组合优于继承
   - 实现灵活的自定义

3. **错误处理**:
   - 优雅的错误展示
   - 提供重试机制
   - 保持用户信息同步

4. **主题支持**:
   - 使用主题颜色
   - 支持动态主题
   - 保持视觉一致性
