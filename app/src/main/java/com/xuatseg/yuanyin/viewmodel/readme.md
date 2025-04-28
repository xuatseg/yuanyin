# ViewModel 模块

该模块包含了应用的所有ViewModel实现，负责管理UI相关的数据和业务逻辑。

## 核心ViewModel

### MainViewModel
```kotlin
class MainViewModel(
    private val robotRepository: RobotRepository,
    private val modeManager: ModeManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    // 状态管理
    val uiState: StateFlow<MainUiState>
    
    // 事件处理
    fun handleEvent(event: MainEvent)
}
```
主要功能：
- 整合机器人状态
- 管理模式切换
- 处理用户设置
- 协调UI更新

### 状态定义
```kotlin
data class MainUiState(
    val robotState: RobotState,
    val modeState: ModeState,
    val settings: AppSettings,
    val isLoading: Boolean,
    val error: String? = null
)
```

### 事件定义
```kotlin
sealed class MainEvent {
    data class ModeChange(val mode: ProcessingMode) : MainEvent()
    data class RobotCommand(val command: RobotCommand) : MainEvent()
    data class SettingsUpdate(val settings: AppSettings) : MainEvent()
    object Refresh : MainEvent()
    object ClearError : MainEvent()
}
```

## ViewModel架构

### 依赖关系
```
+----------------+       +----------------+       +----------------+
|   UI Layer     | <---> |   ViewModel    | <---> |  Domain Layer  |
+----------------+       +----------------+       +----------------+
```

### 数据流
1. UI触发事件
2. ViewModel处理事件
3. 调用Repository获取数据
4. 更新状态
5. UI观察状态变化

## 实现模式

### 状态管理
```kotlin
private val _uiState = MutableStateFlow(MainUiState())
val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

private fun updateState(updater: (MainUiState) -> MainUiState) {
    _uiState.update(updater)
}
```

### 事件处理
```kotlin
fun handleEvent(event: MainEvent) {
    viewModelScope.launch {
        when (event) {
            is MainEvent.ModeChange -> handleModeChange(event.mode)
            is MainEvent.RobotCommand -> handleRobotCommand(event.command)
            // 其他事件处理...
        }
    }
}
```

### 协程使用
```kotlin
private fun loadInitialData() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        try {
            val robotState = robotRepository.getRobotState()
            val modeState = modeManager.getCurrentMode()
            val settings = settingsRepository.getSettings()
            
            _uiState.update {
                it.copy(
                    robotState = robotState,
                    modeState = modeState,
                    settings = settings,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
```

## 最佳实践

1. **职责分离**
   - 每个ViewModel专注于特定功能
   - 避免业务逻辑泄漏到UI层
   - 保持ViewModel轻量级

2. **状态管理**
   - 使用不可变状态
   - 集中状态更新
   - 提供状态转换函数

3. **事件处理**
   - 定义明确的事件类型
   - 处理事件副作用
   - 实现错误处理

4. **测试策略**
   - 测试状态转换
   - 验证事件处理
   - 模拟依赖项

## 测试示例

### ViewModel测试
```kotlin
@Test
fun `initial state should be loading`() = runTest {
    val viewModel = MainViewModel(
        robotRepository = mock(),
        modeManager = mock(),
        settingsRepository = mock()
    )
    
    assertEquals(true, viewModel.uiState.value.isLoading)
}

@Test
fun `handle mode change should update state`() = runTest {
    val modeManager = mockk<ModeManager>()
    coEvery { modeManager.switchMode(any()) } returns Unit
    
    val viewModel = MainViewModel(
        robotRepository = mock(),
        modeManager = modeManager,
        settingsRepository = mock()
    )
    
    viewModel.handleEvent(MainEvent.ModeChange(ProcessingMode.LLM))
    
    coVerify { modeManager.switchMode(ProcessingMode.LLM) }
}
```

## 扩展功能

### SavedStateHandle
```kotlin
class MainViewModel(
    private val savedStateHandle: SavedStateHandle,
    /* 其他依赖 */
) : ViewModel() {
    init {
        savedStateHandle.get<String>("key")?.let { 
            // 恢复状态
        }
    }
}
```

### Hilt注入
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val robotRepository: RobotRepository,
    private val modeManager: ModeManager
) : ViewModel()
```

### Flow操作
```kotlin
private fun observeRobotState() {
    viewModelScope.launch {
        robotRepository.observeRobotState()
            .distinctUntilChanged()
            .collect { state ->
                _uiState.update { it.copy(robotState = state) }
            }
    }
}
```

## 性能优化

1. **状态更新**
   - 使用StateFlow而非LiveData
   - 实现状态去重
   - 批量状态更新

2. **资源管理**
   - 取消无用协程
   - 释放观察者
   - 处理配置变更

3. **内存管理**
   - 避免大对象
   - 使用弱引用
   - 实现缓存策略
