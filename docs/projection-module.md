# 投影功能模块设计文档

## 1. 模块概述
投影功能模块负责管理投影设备连接、内容播放控制以及状态同步。该模块采用六边形架构设计，支持多种投影方式（Miracast、HDMI、AirPlay）和多种内容格式（PPT、PDF、视频）。

## 2. 核心功能
- 投影设备管理
- 内容播放控制
- 状态同步
- 多格式支持
- 远程控制

## 3. 接口设计

### 3.1 领域接口
```kotlin
// 投影管理接口
interface ProjectionManager {
    suspend fun startProjection(content: ProjectionContent): Result<Unit>
    suspend fun stopProjection()
    suspend fun pauseProjection()
    suspend fun resumeProjection()
    fun observeProjectionState(): Flow<ProjectionState>
}

// 投影内容
sealed class ProjectionContent {
    data class Document(
        val id: String,
        val type: DocumentType,
        val path: String,
        val title: String
    ) : ProjectionContent()
    
    data class Video(
        val id: String,
        val url: String,
        val title: String
    ) : ProjectionContent()
}

// 投影状态
sealed class ProjectionState {
    object Idle : ProjectionState()
    object Connecting : ProjectionState()
    object Playing : ProjectionState()
    object Paused : ProjectionState()
    data class Error(val cause: Throwable) : ProjectionState()
}
```

### 3.2 适配器接口
```kotlin
// 投影设备适配器
interface ProjectionDevice {
    suspend fun connect(deviceId: String): Result<Unit>
    suspend fun disconnect()
    fun observeDeviceState(): Flow<DeviceState>
}

// 内容播放适配器
interface ContentPlayer {
    suspend fun loadContent(content: ProjectionContent): Result<Unit>
    suspend fun play()
    suspend fun pause()
    suspend fun stop()
    fun observePlaybackState(): Flow<PlaybackState>
}

// 远程控制适配器
interface RemoteController {
    suspend fun sendCommand(command: RemoteCommand): Result<Unit>
    fun observeRemoteState(): Flow<RemoteState>
}
```

## 4. 实现细节

### 4.1 数据模型
```kotlin
data class DeviceState(
    val isConnected: Boolean,
    val deviceId: String?,
    val deviceName: String?,
    val error: Throwable?
)

data class PlaybackState(
    val isPlaying: Boolean,
    val currentPosition: Long,
    val duration: Long,
    val content: ProjectionContent?
)

data class RemoteState(
    val isConnected: Boolean,
    val lastCommand: RemoteCommand?,
    val error: Throwable?
)
```

### 4.2 用例实现
```kotlin
class ProjectionUseCase @Inject constructor(
    private val projectionDevice: ProjectionDevice,
    private val contentPlayer: ContentPlayer,
    private val remoteController: RemoteController
) {
    private val _projectionState = MutableStateFlow<ProjectionState>(ProjectionState.Idle)
    val projectionState: StateFlow<ProjectionState> = _projectionState.asStateFlow()
    
    suspend fun startProjection(content: ProjectionContent) {
        _projectionState.value = ProjectionState.Connecting
        try {
            // 连接设备
            projectionDevice.connect("default")
            
            // 加载内容
            contentPlayer.loadContent(content)
            
            // 开始播放
            contentPlayer.play()
            
            _projectionState.value = ProjectionState.Playing
        } catch (e: Exception) {
            _projectionState.value = ProjectionState.Error(e)
        }
    }
    
    suspend fun stopProjection() {
        try {
            contentPlayer.stop()
            projectionDevice.disconnect()
            _projectionState.value = ProjectionState.Idle
        } catch (e: Exception) {
            _projectionState.value = ProjectionState.Error(e)
        }
    }
    
    suspend fun pauseProjection() {
        try {
            contentPlayer.pause()
            _projectionState.value = ProjectionState.Paused
        } catch (e: Exception) {
            _projectionState.value = ProjectionState.Error(e)
        }
    }
    
    suspend fun resumeProjection() {
        try {
            contentPlayer.play()
            _projectionState.value = ProjectionState.Playing
        } catch (e: Exception) {
            _projectionState.value = ProjectionState.Error(e)
        }
    }
}
```

## 5. 错误处理
- 设备连接失败处理
- 内容加载失败处理
- 播放错误恢复
- 网络异常处理

## 6. 测试策略
- 单元测试：模拟设备和播放器
- 集成测试：实际设备测试
- UI 测试：控制界面交互
- 性能测试：播放流畅度

## 7. 依赖注入
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ProjectionModule {
    @Provides
    @Singleton
    fun provideProjectionDevice(
        @ApplicationContext context: Context
    ): ProjectionDevice {
        return ProjectionDeviceImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideContentPlayer(
        @ApplicationContext context: Context
    ): ContentPlayer {
        return ContentPlayerImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideRemoteController(
        @ApplicationContext context: Context
    ): RemoteController {
        return RemoteControllerImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideProjectionUseCase(
        projectionDevice: ProjectionDevice,
        contentPlayer: ContentPlayer,
        remoteController: RemoteController
    ): ProjectionUseCase {
        return ProjectionUseCase(
            projectionDevice,
            contentPlayer,
            remoteController
        )
    }
}
```

## 8. 使用示例
```kotlin
class ProjectionViewModel @Inject constructor(
    private val projectionUseCase: ProjectionUseCase
) : ViewModel() {
    val projectionState: StateFlow<ProjectionState> = projectionUseCase.projectionState
    
    fun startProjection(content: ProjectionContent) {
        viewModelScope.launch {
            projectionUseCase.startProjection(content)
        }
    }
    
    fun stopProjection() {
        viewModelScope.launch {
            projectionUseCase.stopProjection()
        }
    }
    
    fun pauseProjection() {
        viewModelScope.launch {
            projectionUseCase.pauseProjection()
        }
    }
    
    fun resumeProjection() {
        viewModelScope.launch {
            projectionUseCase.resumeProjection()
        }
    }
} 