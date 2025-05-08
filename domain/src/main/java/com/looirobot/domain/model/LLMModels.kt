package com.looirobot.domain.model

/**
 * LLM 请求模型
 */
data class LLMRequest(
    val prompt: String,
    val maxTokens: Int = 1000,
    val temperature: Float = 0.7f,
    val topP: Float = 1.0f,
    val frequencyPenalty: Float = 0.0f,
    val presencePenalty: Float = 0.0f,
    val stop: List<String> = emptyList()
)

/**
 * LLM 响应模型
 */
data class LLMResponse(
    val id: String,
    val text: String,
    val finishReason: String? = null,
    val usage: TokenUsage? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Token 使用统计
 */
data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

/**
 * 图像分析响应模型
 */
data class ImageAnalysisResponse(
    val description: String,
    val tags: List<String>,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
) 