package com.looirobot.core.llm

import kotlinx.coroutines.flow.Flow

interface LLM {
    suspend fun generateResponse(prompt: String): String
    suspend fun generateStreamingResponse(prompt: String): Flow<String>
    suspend fun analyzeImage(imagePath: String): String
    suspend fun setModel(model: LLMModel)
    suspend fun setParameters(parameters: LLMParameters)
    fun getStatus(): Flow<LLMStatus>
}

data class LLMStatus(
    val isProcessing: Boolean,
    val currentModel: LLMModel,
    val parameters: LLMParameters,
    val error: String? = null
)

data class LLMParameters(
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1000,
    val topP: Float = 1.0f,
    val frequencyPenalty: Float = 0.0f,
    val presencePenalty: Float = 0.0f
)

enum class LLMModel {
    GPT_4,
    GPT_3_5_TURBO,
    CLAUDE_3_OPUS,
    CLAUDE_3_SONNET,
    CLAUDE_3_HAIKU
}

sealed class LLMError : Exception() {
    data class NetworkError(override val message: String) : LLMError()
    data class ModelError(override val message: String) : LLMError()
    data class ParameterError(override val message: String) : LLMError()
    data class AuthenticationError(override val message: String) : LLMError()
} 