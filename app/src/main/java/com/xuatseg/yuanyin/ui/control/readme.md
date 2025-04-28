# UI Control 模块

该模块包含了机器人控制相关的UI组件，提供了直观的机器人操作界面。

## 主要组件

### RobotControlComponents
机器人控制组件集合，包含：

1. **ControlPanel**
```kotlin
@Composable
fun ControlPanel(
    state: RobotControlState,
    onControlAction: (ControlAction) -> Unit,
    modifier: Modifier = Modifier
)
```
主控制面板，整合了所有控制功能：
- 方向控制
- 速度调节
- 紧急停止
- 状态显示

2. **DirectionalPad**
```kotlin
@Composable
fun DirectionalPad(
    onDirectionPress: (Direction) -> Unit,
    onDirectionRelease: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
)
```
方向控制板，提供：
- 八方向控制
- 长按支持
- 触觉反馈
- 禁用状态处理

3. **SpeedController**
```kotlin
@Composable
fun SpeedController(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    maxSpeed: Float,
    modifier: Modifier = Modifier
)
```
速度控制器，特性：
- 滑动调节
- 精确控制
- 速度限制
- 视觉反馈

4. **EmergencyStop**
```kotlin
@Composable
fun EmergencyStop(
    onStop: () -> Unit,
    modifier: Modifier = Modifier
)
```
紧急停止按钮，提供：
- 突出显示
- 快速响应
- 触觉反馈
- 确认机制

## 状态模型

### RobotControlState
```kotlin
data class RobotControlState(
    val movement: MovementState,
    val speed: SpeedState,
    val batteryLevel: Float,
    val isConnected: Boolean,
    val error: String? = null
)
```

### MovementState
```kotlin
data class MovementState(
    val direction: Direction,
    val isMoving: Boolean,
    val position: Position
)
```

### SpeedState
```kotlin
data class SpeedState(
    val current: Float,
    val max: Float,
    val min: Float = 0f
)
```

## 事件处理

### ControlAction
```kotlin
sealed class ControlAction {
    data class Move(val direction: Direction) : ControlAction()
    data class SetSpeed(val speed: Float) : ControlAction()
    object Stop : ControlAction()
    object EmergencyStop : ControlAction()
}
```

## 使用示例

### 基本控制面板
```kotlin
@Composable
fun RobotControlScreen(
    viewModel: RobotControlViewModel
) {
    val state by viewModel.state.collectAsState()
    
    ControlPanel(
        state = state,
        onControlAction = viewModel::handleAction
    )
}
```

### 自定义控制器
```kotlin
@Composable
fun CustomController() {
    var speed by remember { mutableStateOf(0f) }
    
    Column {
        DirectionalPad(
            onDirectionPress = { /* 处理方向按压 */ },
            onDirectionRelease = { /* 处理释放 */ },
            isEnabled = true
        )
        
        SpeedController(
            currentSpeed = speed,
            onSpeedChange = { speed = it },
            maxSpeed = 1.0f
        )
        
        EmergencyStop(
            onStop = { /* 处理紧急停止 */ }
        )
    }
}
```

## 最佳实践

1. **状态管理**
   - 使用单向数据流
   - 状态提升到ViewModel
   - 避免本地状态泄漏

2. **性能优化**
   - 合理使用remember
   - 优化重组范围
   - 实现高效的事件处理

3. **用户体验**
   - 添加适当的动画
   - 实现触觉反馈
   - 提供清晰的视觉反馈

4. **错误处理**
   - 显示友好的错误信息
   - 提供重试机制
   - 保持界面响应性

5. **可访问性**
   - 添加内容描述
   - 支持键盘控制
   - 考虑色盲用户

## 自定义主题

```kotlin
@Composable
fun ControlTheme(
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) {
        DarkControlColors
    } else {
        LightControlColors
    }
    
    CompositionLocalProvider(
        LocalControlColors provides colors
    ) {
        content()
    }
}
```

## 动画效果

```kotlin
@Composable
fun AnimatedControlButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clickable(onClick = onClick)
    ) {
        content()
    }
}
```
