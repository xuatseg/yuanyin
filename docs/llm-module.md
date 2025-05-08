# LLM 集成模块设计文档

## 1. 模块概述
LLM 集成模块负责与各种大语言模型（如 OpenAI、阿里、百度等）进行通信，提供智能对话、指令解析等功能。该模块采用六边形架构设计，支持多模型切换、结果缓存和降级机制。

## 2. 核心功能
- 多模型支持（OpenAI、阿里、百度等）
- 智能对话
- 指令解析
- 结果缓存
- 降级机制

## 3. 接口设计

### 3.1 领域接口
```kotlin
// LLM 服务接口
interface LLMService {
    suspend fun chat(prompt: String): Result<ChatResponse>
    suspend fun parseCommand(text: String): Result<Command>
    suspend fun generateResponse(context: ChatContext): Result<ChatResponse>
    fun observeModelState(): Flow<ModelState>
}

// 聊天响应
data class ChatResponse(
    val id: String,
    val content: String,
    val model: String,
    val usage: TokenUsage,
    val timestamp: Long
)

// 聊天上下文
data class ChatContext(
    val messages: List<Message>,
    val systemPrompt: String?,
    val temperature: Float,
    val maxTokens: Int?
)

// 模型状态
sealed class ModelState {
    object Idle : ModelState()
    object Loading : ModelState()
    object Ready : ModelState()
    data class Error(val cause: Throwable) : ModelState()
}
```

### 3.2 适配器接口
```kotlin
// LLM API 适配器
interface LLMApiAdapter {
    suspend fun chat(request: ChatRequest): Result<ChatResponse>
    suspend fun streamChat(request: ChatRequest): Flow<ChatResponse>
    fun observeApiState(): Flow<ApiState>
}

// 缓存适配器
interface LLMCache {
    suspend fun get(key: String): ChatResponse?
    suspend fun put(key: String, response: ChatResponse)
    suspend fun clear()
}

// 降级适配器
interface FallbackAdapter {
    suspend fun getFallbackResponse(prompt: String): ChatResponse
    fun isAvailable(): Boolean
}
```

## 4. 实现细节

### 4.1 数据模型
```kotlin
data class ChatRequest(
    val messages: List<Message>,
    val model: String,
    val temperature: Float,
    val maxTokens: Int?
)

data class Message(
    val role: Role,
    val content: String
)

data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

data class LLMState(
    val currentModel: String,
    val isStreaming: Boolean,
    val lastResponse: ChatResponse?,
    val error: Throwable?
)
```

### 4.2 用例实现
```kotlin
class LLMUseCase @Inject constructor(
    private val llmApiAdapter: LLMApiAdapter,
    private val llmCache: LLMCache,
    private val fallbackAdapter: FallbackAdapter
) {
    private val _llmState = MutableStateFlow<LLMState>(LLMState("", false, null, null))
    val llmState: StateFlow<LLMState> = _llmState.asStateFlow()
    
    suspend fun chat(prompt: String): Result<ChatResponse> {
        return try {
            // 检查缓存
            val cachedResponse = llmCache.get(prompt)
            if (cachedResponse != null) {
                return Result.success(cachedResponse)
            }
            
            // 构建请求
            val request = ChatRequest(
                messages = listOf(Message(Role.USER, prompt)),
                model = "gpt-3.5-turbo",
                temperature = 0.7f,
                maxTokens = 1000
            )
            
            // 发送请求
            val response = llmApiAdapter.chat(request).getOrThrow()
            
            // 缓存结果
            llmCache.put(prompt, response)
            
            // 更新状态
            _llmState.value = _llmState.value.copy(
                lastResponse = response,
                error = null
            )
            
            Result.success(response)
        } catch (e: Exception) {
            // 降级处理
            if (fallbackAdapter.isAvailable()) {
                val fallbackResponse = fallbackAdapter.getFallbackResponse(prompt)
                Result.success(fallbackResponse)
            } else {
                Result.failure(e)
            }
        }
    }
    
    suspend fun streamChat(prompt: String): Flow<ChatResponse> {
        val request = ChatRequest(
            messages = listOf(Message(Role.USER, prompt)),
            model = "gpt-3.5-turbo",
            temperature = 0.7f,
            maxTokens = 1000
        )
        
        return llmApiAdapter.streamChat(request)
            .onEach { response ->
                _llmState.value = _llmState.value.copy(
                    isStreaming = true,
                    lastResponse = response
                )
            }
            .onCompletion {
                _llmState.value = _llmState.value.copy(isStreaming = false)
            }
    }
    
    suspend fun parseCommand(text: String): Result<Command> {
        val prompt = "解析以下指令：$text"
        return chat(prompt).map { response ->
            CommandParser.parse(response.content)
        }
    }
}
```

## 5. 错误处理
- API 调用失败重试
- 网络超时处理
- 降级机制
- 缓存失效处理

## 6. 测试策略
- 单元测试：模拟 API 和缓存
- 集成测试：实际 API 调用
- 性能测试：响应时间
- 降级测试：网络异常场景

## 7. 依赖注入
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object LLMModule {
    @Provides
    @Singleton
    fun provideLLMApiAdapter(
        @ApplicationContext context: Context
    ): LLMApiAdapter {
        return OpenAIAdapter(context)
    }
    
    @Provides
    @Singleton
    fun provideLLMCache(
        @ApplicationContext context: Context
    ): LLMCache {
        return RoomLLMCache(context)
    }
    
    @Provides
    @Singleton
    fun provideFallbackAdapter(
        @ApplicationContext context: Context
    ): FallbackAdapter {
        return LocalFallbackAdapter(context)
    }
    
    @Provides
    @Singleton
    fun provideLLMUseCase(
        llmApiAdapter: LLMApiAdapter,
        llmCache: LLMCache,
        fallbackAdapter: FallbackAdapter
    ): LLMUseCase {
        return LLMUseCase(
            llmApiAdapter,
            llmCache,
            fallbackAdapter
        )
    }
}
```

## 8. 使用示例
```kotlin
class LLMViewModel @Inject constructor(
    private val llmUseCase: LLMUseCase
) : ViewModel() {
    val llmState: StateFlow<LLMState> = llmUseCase.llmState
    
    fun chat(prompt: String) {
        viewModelScope.launch {
            llmUseCase.chat(prompt)
        }
    }
    
    fun streamChat(prompt: String) {
        viewModelScope.launch {
            llmUseCase.streamChat(prompt)
                .collect { response ->
                    // 处理流式响应
                }
        }
    }
    
    fun parseCommand(text: String) {
        viewModelScope.launch {
            llmUseCase.parseCommand(text)
        }
    }
}
``` 