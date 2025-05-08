package com.looirobot.data.model

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

data class LLMParameters(
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1000,
    val topP: Float = 1.0f,
    val frequencyPenalty: Float = 0.0f,
    val presencePenalty: Float = 0.0f
) 