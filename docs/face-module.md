# 人脸识别模块设计文档

## 1. 模块概述
人脸识别模块负责摄像头管理、实时人脸检测与跟踪，以及位置信息处理。该模块采用六边形架构设计，支持实时人脸检测、跟踪和位置计算。

## 2. 核心功能
- 摄像头管理（打开/关闭/切换）
- 实时人脸检测
- 人脸跟踪
- 位置信息计算
- 多角度人脸识别

## 3. 接口设计

### 3.1 领域接口
```kotlin
// 人脸识别接口
interface FaceTracker {
    suspend fun startTracking(): Result<Unit>
    suspend fun stopTracking()
    fun observeFacePosition(): Flow<FacePosition>
    fun observeTrackingState(): Flow<TrackingState>
}

// 人脸位置
data class FacePosition(
    val boundingBox: Rect,
    val center: Point,
    val angle: Float,
    val confidence: Float
)

// 跟踪状态
sealed class TrackingState {
    object Idle : TrackingState()
    object Initializing : TrackingState()
    object Tracking : TrackingState()
    object Lost : TrackingState()
    data class Error(val cause: Throwable) : TrackingState()
}
```

### 3.2 适配器接口
```kotlin
// 摄像头适配器
interface CameraAdapter {
    suspend fun openCamera(cameraId: String): Result<Unit>
    suspend fun closeCamera()
    fun observeFrame(): Flow<CameraFrame>
    fun observeCameraState(): Flow<CameraState>
}

// 人脸检测适配器
interface FaceDetector {
    suspend fun detectFaces(frame: CameraFrame): List<FaceDetection>
    suspend fun trackFaces(frame: CameraFrame): List<FaceTracking>
}

// 位置计算适配器
interface PositionCalculator {
    fun calculatePosition(face: FaceDetection): FacePosition
    fun calculateAngle(face: FaceDetection): Float
}
```

## 4. 实现细节

### 4.1 数据模型
```kotlin
data class CameraFrame(
    val image: ImageProxy,
    val timestamp: Long,
    val rotation: Int
)

data class FaceDetection(
    val boundingBox: Rect,
    val landmarks: List<Point>,
    val confidence: Float
)

data class FaceTracking(
    val id: Int,
    val detection: FaceDetection,
    val velocity: Point
)
```

### 4.2 用例实现
```kotlin
class FaceUseCase @Inject constructor(
    private val cameraAdapter: CameraAdapter,
    private val faceDetector: FaceDetector,
    private val positionCalculator: PositionCalculator
) {
    private val _trackingState = MutableStateFlow<TrackingState>(TrackingState.Idle)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()
    
    private val _facePosition = MutableStateFlow<FacePosition?>(null)
    val facePosition: StateFlow<FacePosition?> = _facePosition.asStateFlow()
    
    suspend fun startTracking() {
        _trackingState.value = TrackingState.Initializing
        try {
            cameraAdapter.openCamera("front")
            startFrameProcessing()
            _trackingState.value = TrackingState.Tracking
        } catch (e: Exception) {
            _trackingState.value = TrackingState.Error(e)
        }
    }
    
    private fun startFrameProcessing() {
        viewModelScope.launch {
            cameraAdapter.observeFrame()
                .collect { frame ->
                    processFrame(frame)
                }
        }
    }
    
    private suspend fun processFrame(frame: CameraFrame) {
        val faces = faceDetector.detectFaces(frame)
        if (faces.isNotEmpty()) {
            val face = faces.first()
            val position = positionCalculator.calculatePosition(face)
            _facePosition.value = position
            _trackingState.value = TrackingState.Tracking
        } else {
            _trackingState.value = TrackingState.Lost
        }
    }
    
    suspend fun stopTracking() {
        cameraAdapter.closeCamera()
        _trackingState.value = TrackingState.Idle
        _facePosition.value = null
    }
}
```

## 5. 错误处理
- 摄像头打开失败处理
- 人脸检测失败重试
- 跟踪丢失恢复
- 性能优化（帧率控制）

## 6. 测试策略
- 单元测试：模拟摄像头和检测器
- 集成测试：实际设备测试
- UI 测试：预览界面交互
- 性能测试：检测速度

## 7. 依赖注入
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FaceModule {
    @Provides
    @Singleton
    fun provideCameraAdapter(
        @ApplicationContext context: Context
    ): CameraAdapter {
        return CameraAdapterImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideFaceDetector(
        @ApplicationContext context: Context
    ): FaceDetector {
        return MLKitFaceDetector(context)
    }
    
    @Provides
    @Singleton
    fun providePositionCalculator(): PositionCalculator {
        return PositionCalculatorImpl()
    }
    
    @Provides
    @Singleton
    fun provideFaceUseCase(
        cameraAdapter: CameraAdapter,
        faceDetector: FaceDetector,
        positionCalculator: PositionCalculator
    ): FaceUseCase {
        return FaceUseCase(
            cameraAdapter,
            faceDetector,
            positionCalculator
        )
    }
}
```

## 8. 使用示例
```kotlin
class FaceViewModel @Inject constructor(
    private val faceUseCase: FaceUseCase
) : ViewModel() {
    val trackingState: StateFlow<TrackingState> = faceUseCase.trackingState
    val facePosition: StateFlow<FacePosition?> = faceUseCase.facePosition
    
    fun startTracking() {
        viewModelScope.launch {
            faceUseCase.startTracking()
        }
    }
    
    fun stopTracking() {
        viewModelScope.launch {
            faceUseCase.stopTracking()
        }
    }
} 