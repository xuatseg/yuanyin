package com.looirobot.data.source

import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface LLMApiService {
    @POST("chat/completions")
    suspend fun generateCompletion(
        @Body request: CompletionRequest
    ): CompletionResponse
    
    @Streaming
    @POST("chat/completions")
    suspend fun generateStreamingCompletion(
        @Body request: CompletionRequest
    ): Flow<CompletionResponse>
    
    @POST("chat/completions")
    suspend fun analyzeImage(
        @Body request: ImageAnalysisRequest
    ): CompletionResponse
}

data class CompletionRequest(
    val model: String,
    val prompt: String,
    val parameters: LLMParameters
)

data class ImageAnalysisRequest(
    val model: String,
    val imagePath: String,
    val parameters: LLMParameters
)

data class CompletionResponse(
    val id: String,
    val text: String,
    val finishReason: String?,
    val usage: TokenUsageResponse
)

data class TokenUsageResponse(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

data class LLMParameters(
    val temperature: Float,
    val maxTokens: Int,
    val topP: Float,
    val frequencyPenalty: Float,
    val presencePenalty: Float
) 