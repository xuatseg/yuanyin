# UI Mode 模块

该模块包含了处理模式切换的UI组件，提供了直观的模式选择和状态显示界面。

## 主要组件

### ModeSwitchComponents
模式切换组件集合，包含：

1. **ModeSwitchPanel**
```kotlin
@Composable
fun ModeSwitchPanel(
    state: ModeSwitchState,
    onModeSelect: (ProcessingMode) -> Unit,
    modifier: Modifier = Modifier
)
```
模式切换面板，提供：
- 当前模式显示
- 可用模式列表
- 模式切换动画
- 状态指示器

2. **ModeSelector**
```kotlin
@Composable
fun ModeSelector(
    currentMode: ProcessingMode,
    availableModes: List<ProcessingMode>,
    onModeSelect: (ProcessingMode) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
)
```
模式选择器，特性：
- 下拉选择
- 图标显示
- 禁用状态处理
- 动画过渡

3. **ModeStatusBar**
```kotlin
@Composable
fun ModeStatusBar(
    modeState: ModeState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
)
```
模式状态栏，显示：
- 当前状态
- 错误信息
- 重试选项
- 性能指标

4. **ModeInfoCard**
```kotlin
@Composable
fun ModeInfoCard(
    mode: ProcessingMode,
    stats: ModeStatistics,
    modifier: Modifier = Modifier
)
```
模式信息卡片，展示：
- 模式描述
- 使用统计
- 性能数据
- 系统建议

## 状态模型

### ModeSwitchState
```kotlin
data class ModeSwitchState(
    val currentMode: ProcessingMode,
    val availableModes: List<ProcessingMode>,
    val modeState: ModeState,
    val statistics: Map<ProcessingMode, ModeStatistics>,
    val error: String? = null
)
```

### ModeState
```kotlin
data class ModeState(
    val status: ModeStatus,
    val isAvailable: Boolean,
    val performance: PerformanceMetrics,
    val lastUpdated: Instant
)
```

### ModeStatistics
```kotlin
data class ModeStatistics(
    val usageCount: Int,
    val averageResponseTime: Duration,
    val successRate: Float,
    val lastUsed: Instant
)
```

## 事件处理

### ModeSwitchEvent
```kotlin
sealed class ModeSwitchEvent {
    data class SelectMode(val mode: ProcessingMode) : ModeSwitchEvent()
    object RetrySwitch : ModeSwitchEvent()
    object RefreshStatus : ModeSwitchEvent()
    data class UpdateStatistics(val mode: ProcessingMode) : ModeSwitchEvent()
}
```

## 使用示例

### 基本模式切换
```kotlin
@Composable
fun ModeSwitchScreen(
    viewModel: ModeSwitchViewModel
) {
    val state by viewModel.state.collectAsState()
    
    ModeSwitchPanel(
        state = state,
        onModeSelect = { mode ->
            viewModel.handleEvent(ModeSwitchEvent.SelectMode(mode))
        }
    )
}
```

### 自定义模式选择器
```kotlin
@Composable
fun CustomModeSelector() {
    var selectedMode by remember { mutableStateOf(ProcessingMode.LOCAL) }
    
    Column {
        ModeSelector(
            currentMode = selectedMode,
            availableModes = ProcessingMode.values().toList(),
            onModeSelect = { selectedMode = it },
            isEnabled = true
        )
        
        ModeStatusBar(
            modeState = getModeState(selectedMode),
            onRetry = { /* 处理重试 */ }
        )
        
        ModeInfoCard(
            mode = selectedMode,
            stats = getModeStatistics(selectedMode)
        )
    }
}
```

## 最佳实践

1. **状态管理**
   - 集中管理模式状态
   - 实现状态持久化
   - 处理状态转换

2. **性能优化**
   - 缓存模式信息
   - 延迟加载统计数据
   - 优化切换动画

3. **用户体验**
   - 提供模式预览
   - 显示切换进度
   - 保存用户偏好

4. **错误处理**
   - 显示详细错误信息
   - 提供故障排除建议
   - 实现自动恢复

5. **可访问性**
   - 支持键盘导航
   - 添加语音提示
   - 高对比度模式

## 自定义主题

```kotlin
@Composable
fun ModeTheme(
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) {
        DarkModeColors
    } else {
        LightModeColors
    }
    
    CompositionLocalProvider(
        LocalModeColors provides colors
    ) {
        content()
    }
}
```

## 动画效果

```kotlin
@Composable
fun AnimatedModeTransition(
    targetMode: ProcessingMode,
    content: @Composable (ProcessingMode) -> Unit
) {
    var currentMode by remember { mutableStateOf(targetMode) }
    
    AnimatedContent(
        targetState = targetMode,
        transitionSpec = {
            fadeIn() + slideIn() with fadeOut() + slideOut()
        }
    ) { mode ->
        content(mode)
    }
}
```

## 性能监控

```kotlin
@Composable
fun ModePerformanceMonitor(
    mode: ProcessingMode,
    metrics: PerformanceMetrics
) {
    LaunchedEffect(mode) {
        // 监控模式性能
        snapshotFlow { metrics }
            .collect { newMetrics ->
                // 更新性能数据
                updatePerformanceData(mode, newMetrics)
            }
    }
}
```
