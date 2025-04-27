package com.xuatseg.yuanyin.llm

import kotlinx.coroutines.flow.Flow

/**
 * LLM服务接口
 */
interface ILLMService {
    /**
     * 发送对话请求
     * @param request 请求内容
     * @return 响应流
     */
    fun chat(request: LLMRequest): Flow<LLMResponse>

    /**
     * 发送补全请求
     * @param request 请求内容
     * @return 响应流
     */
    fun complete(request: LLMRequest): Flow<LLMResponse>

    /**
     * 发送嵌入请求
     * @param text 文本内容
     * @return 嵌入向量
     */
    suspend fun embed(text: String): List<Float>

    /**
     * 中断当前请求
     */
    fun cancelRequest()
}

/**
 * LLM请求
 */
data class LLMRequest(
    val messages: List<LLMMessage>,
    val parameters: LLMParameters,
    val options: LLMOptions? = null
)

/**
 * LLM消息
 */
data class LLMMessage(
    val role: MessageRole,
    val content: String,
    val metadata: Map<String, Any>? = null
)

/**
 * 消息角色
 */
enum class MessageRole {
    SYSTEM,
    USER,
    ASSISTANT
}

/**
 * LLM参数
 */
data class LLMParameters(
    val temperature: Float = 0.7f,
    val topP: Float = 1.0f,
    val maxTokens: Int? = null,
    val presencePenalty: Float = 0.0f,
    val frequencyPenalty: Float = 0.0f,
    val stop: List<String>? = null
)

/**
 * LLM选项
 */
data class LLMOptions(
    val stream: Boolean = false,
    val timeout: Long? = null,
    val retryCount: Int = 3,
    val modelVersion: String? = null
)

/**
 * LLM响应
 */
sealed class LLMResponse {
    data class Content(val text: String) : LLMResponse()
    data class Error(val error: LLMError) : LLMResponse()
    object Done : LLMResponse()
}

/**
 * LLM错误
 */
sealed class LLMError {
    data class ApiError(val code: String, val message: String) : LLMError()
    data class NetworkError(val message: String) : LLMError()
    data class RateLimitError(val retryAfter: Long) : LLMError()
    data class TokenLimitError(val limit: Int, val requested: Int) : LLMError()
    data class TimeoutError(val timeout: Long) : LLMError()
    data class Unknown(val message: String) : LLMError()
}

/**
 * LLM配置接口
 */
interface ILLMConfig {
    /**
     * 获取API密钥
     */
    fun getApiKey(): String

    /**
     * 获取模型配置
     */
    fun getModelConfig(): ModelConfig

    /**
     * 获取API配置
     */
    fun getApiConfig(): ApiConfig

    /**
     * 更新配置
     */
    fun updateConfig(config: Map<String, Any>)
}

/**
 * 模型配置
 */
data class ModelConfig(
    val modelName: String,
    val modelVersion: String,
    val contextLength: Int,
    val maxTokens: Int,
    val supportedFeatures: Set<ModelFeature>
)

/**
 * API配置
 */
data class ApiConfig(
    val baseUrl: String,
    val timeout: Long,
    val retryCount: Int,
    val rateLimitPerMinute: Int,
    val headers: Map<String, String>
)

/**
 * 模型特性
 */
enum class ModelFeature {
    CHAT,
    COMPLETION,
    EMBEDDING,
    CODE,
    FUNCTION_CALLING
}

/**
 * LLM监控接口
 */
interface ILLMMonitor {
    /**
     * 记录请求
     * @param request 请求内容
     * @param startTime 开始时间
     */
    fun recordRequest(request: LLMRequest, startTime: Long)

    /**
     * 记录响应
     * @param response 响应内容
     * @param endTime 结束时间
     */
    fun recordResponse(response: LLMResponse, endTime: Long)

    /**
     * 获取使用统计
     */
    fun getUsageStatistics(): LLMUsageStatistics
}

/**
 * LLM使用统计
 */
data class LLMUsageStatistics(
    val totalRequests: Int,
    val totalTokens: Int,
    val averageResponseTime: Long,
    val errorRate: Float,
    val costEstimate: Float
)

/**
 * LLM缓存接口
 */
interface ILLMCache {
    /**
     * 获取缓存响应
     * @param request 请求内容
     */
    suspend fun getCachedResponse(request: LLMRequest): LLMResponse?

    /**
     * 保存响应到缓存
     * @param request 请求内容
     * @param response 响应内容
     */
    suspend fun cacheResponse(request: LLMRequest, response: LLMResponse)

    /**
     * 清除缓存
     */
    suspend fun clearCache()
}
