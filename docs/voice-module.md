# 语音交互模块设计文档

## 1. 模块概述
语音交互模块负责处理语音唤醒、识别、合成以及指令解析等功能。该模块采用六边形架构设计，支持本地和云端语音识别，并提供流畅的语音交互体验。

## 2. 核心功能
- 语音唤醒检测
- 语音识别（本地/云端）
- 语音合成（TTS）
- 指令解析与执行
- 语音反馈动画

## 3. 接口设计

### 3.1 领域接口
```kotlin
// 语音交互接口
interface VoiceInteraction {
    suspend fun startListening(): Result<Unit>
    suspend fun stopListening()
    suspend fun speak(text: String): Result<Unit>
    fun observeVoiceState(): Flow<VoiceState>
}

// 语音状态
sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    object Processing : VoiceState()
    object Speaking : VoiceState()
    data class Error(val cause: Throwable) : VoiceState()
}

// 语音命令
sealed class VoiceCommand {
    data class Move(
        val direction: Direction,
        val distance: Float? = null
    ) : VoiceCommand()
    
    data class Project(
        val contentId: String,
        val action: ProjectAction
    ) : VoiceCommand()
    
    object Stop : VoiceCommand()
}
```

### 3.2 适配器接口
```kotlin
// 语音识别适配器
interface SpeechRecognizer {
    suspend fun startListening(): Flow<RecognitionResult>
    suspend fun stopListening()
    fun observeState(): Flow<RecognizerState>
}

// 语音合成适配器
interface TextToSpeech {
    suspend fun speak(text: String): Result<Unit>
    suspend fun stop()
    fun observeState(): Flow<TTSState>
}

// 唤醒词检测适配器
interface WakeWordDetector {
    fun startDetection(): Flow<WakeWordEvent>
    fun stopDetection()
}
```

## 4. 实现细节

### 4.1 数据模型
```kotlin
data class RecognitionResult(
    val text: String,
    val confidence: Float,
    val isFinal: Boolean
)

data class VoiceInteractionState(
    val isListening: Boolean,
    val isSpeaking: Boolean,
    val lastCommand: VoiceCommand?,
    val error: Throwable?
)
```

### 4.2 用例实现
```kotlin
class VoiceUseCase @Inject constructor(
    private val speechRecognizer: SpeechRecognizer,
    private val textToSpeech: TextToSpeech,
    private val wakeWordDetector: WakeWordDetector,
    private val commandParser: CommandParser
) {
    private val _voiceState = MutableStateFlow<VoiceInteractionState>(VoiceInteractionState())
    val voiceState: StateFlow<VoiceInteractionState> = _voiceState.asStateFlow()
    
    init {
        startWakeWordDetection()
    }
    
    private fun startWakeWordDetection() {
        viewModelScope.launch {
            wakeWordDetector.startDetection()
                .collect { event ->
                    when (event) {
                        is WakeWordEvent.Detected -> startListening()
                        is WakeWordEvent.Error -> handleError(event.cause)
                    }
                }
        }
    }
    
    suspend fun startListening() {
        speechRecognizer.startListening()
            .collect { result ->
                if (result.isFinal) {
                    val command = commandParser.parse(result.text)
                    executeCommand(command)
                }
            }
    }
    
    private suspend fun executeCommand(command: VoiceCommand) {
        when (command) {
            is VoiceCommand.Move -> {
                // 执行移动命令
                speak("正在执行移动命令")
            }
            is VoiceCommand.Project -> {
                // 执行投影命令
                speak("正在执行投影命令")
            }
            is VoiceCommand.Stop -> {
                // 执行停止命令
                speak("正在停止")
            }
        }
    }
    
    suspend fun speak(text: String) {
        textToSpeech.speak(text)
    }
}
```

## 5. 错误处理
- 识别失败重试
- 网络异常降级
- 合成失败处理
- 唤醒词误触发处理

## 6. 测试策略
- 单元测试：模拟语音识别和合成
- 集成测试：实际语音交互
- UI 测试：语音界面交互
- 性能测试：响应时间

## 7. 依赖注入
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object VoiceModule {
    @Provides
    @Singleton
    fun provideSpeechRecognizer(
        @ApplicationContext context: Context
    ): SpeechRecognizer {
        return SpeechRecognizerImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideTextToSpeech(
        @ApplicationContext context: Context
    ): TextToSpeech {
        return TextToSpeechImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideWakeWordDetector(
        @ApplicationContext context: Context
    ): WakeWordDetector {
        return WakeWordDetectorImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideVoiceUseCase(
        speechRecognizer: SpeechRecognizer,
        textToSpeech: TextToSpeech,
        wakeWordDetector: WakeWordDetector,
        commandParser: CommandParser
    ): VoiceUseCase {
        return VoiceUseCase(
            speechRecognizer,
            textToSpeech,
            wakeWordDetector,
            commandParser
        )
    }
}
```

## 8. 使用示例
```kotlin
class VoiceViewModel @Inject constructor(
    private val voiceUseCase: VoiceUseCase
) : ViewModel() {
    val voiceState: StateFlow<VoiceInteractionState> = voiceUseCase.voiceState
    
    fun startVoiceInteraction() {
        viewModelScope.launch {
            voiceUseCase.startListening()
        }
    }
    
    fun stopVoiceInteraction() {
        viewModelScope.launch {
            voiceUseCase.stopListening()
        }
    }
    
    fun speak(text: String) {
        viewModelScope.launch {
            voiceUseCase.speak(text)
        }
    }
}
``` 