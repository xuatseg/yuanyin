/**
 * LLM 响应相关的数据模型定义
 * 
 * 该文件包含以下数据类：
 * 1. LLMResponse - LLM 响应的主要数据模型，包含响应文本、模型信息、Token 使用情况等
 * 2. TokenUsage - Token 使用统计信息，包括提示词、完成词和总 Token 数量
 * 3. LLMRequest - LLM 请求参数模型，包含提示词、模型选择和生成参数等
 * 4. ImageAnalysisResponse - 图像分析响应模型，包含图像描述、标签和置信度等
 */
package com.looirobot.data.model

data class LLMResponse(
    val id: String,
    val text: String,
    val model: String,
    val finishReason: String?,
    val usage: TokenUsage,
    val timestamp: Long = System.currentTimeMillis()
)

data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

data class LLMRequest(
    val prompt: String,
    val model: String,
    val temperature: Float,
    val maxTokens: Int,
    val topP: Float,
    val frequencyPenalty: Float,
    val presencePenalty: Float
)

data class ImageAnalysisResponse(
    val id: String,
    val description: String,
    val tags: List<String>,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
) 