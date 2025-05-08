package com.looirobot.core.llm

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
    val text: String,
    val finishReason: String? = null
) 