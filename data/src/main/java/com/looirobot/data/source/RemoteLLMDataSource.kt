/**
 * LLM 远程数据源实现
 * 
 * 该实现类负责：
 * 1. 与 LLM API 的直接通信
 * 2. 处理文本生成请求
 * 3. 处理流式响应
 * 4. 处理图像分析请求
 * 
 * 技术特点：
 * - 使用 Retrofit 进行网络请求
 * - 支持流式响应处理
 * - 错误处理和状态管理
 */
package com.looirobot.data.source

import com.looirobot.data.model.LLMRequest
import com.looirobot.data.model.LLMResponse
import com.looirobot.data.model.ImageAnalysisResponse
import com.looirobot.data.model.TokenUsage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteLLMDataSource @Inject constructor() : LLMDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService: LLMApiService = retrofit.create(LLMApiService::class.java)
    
    override suspend fun generateCompletion(request: LLMRequest): LLMResponse {
        val response = apiService.generateCompletion(
            CompletionRequest(
                model = request.model,
                prompt = request.prompt,
                parameters = request.toParameters()
            )
        )
        
        return LLMResponse(
            id = response.id,
            text = response.text,
            model = request.model,
            finishReason = response.finishReason,
            usage = TokenUsage(
                promptTokens = response.usage.promptTokens,
                completionTokens = response.usage.completionTokens,
                totalTokens = response.usage.totalTokens
            )
        )
    }
    
    override suspend fun generateStreamingCompletion(request: LLMRequest): Flow<LLMResponse> = flow {
        apiService.generateStreamingCompletion(
            CompletionRequest(
                model = request.model,
                prompt = request.prompt,
                parameters = request.toParameters()
            )
        ).collect { response ->
            emit(
                LLMResponse(
                    id = response.id,
                    text = response.text,
                    model = request.model,
                    finishReason = response.finishReason,
                    usage = TokenUsage(
                        promptTokens = response.usage.promptTokens,
                        completionTokens = response.usage.completionTokens,
                        totalTokens = response.usage.totalTokens
                    )
                )
            )
        }
    }
    
    override suspend fun analyzeImage(imagePath: String): ImageAnalysisResponse {
        val response = apiService.analyzeImage(
            ImageAnalysisRequest(
                model = "gpt-4-vision-preview",
                imagePath = imagePath,
                parameters = LLMParameters(
                    temperature = 0.7f,
                    maxTokens = 1000,
                    topP = 1.0f,
                    frequencyPenalty = 0.0f,
                    presencePenalty = 0.0f
                )
            )
        )
        return ImageAnalysisResponse(
            id = response.id,
            description = response.text,
            tags = emptyList(), // TODO: 从响应中提取标签
            confidence = 1.0f // TODO: 从响应中提取置信度
        )
    }
    
    override suspend fun saveResponse(response: LLMResponse) {
        // TODO: 实现响应保存逻辑
    }
    
    override suspend fun getResponseHistory(): Flow<List<LLMResponse>> = flow {
        // TODO: 实现历史记录获取逻辑
        emit(emptyList())
    }
    
    override suspend fun clearHistory() {
        // TODO: 实现历史记录清除逻辑
    }
    
    private fun LLMRequest.toParameters() = LLMParameters(
        temperature = temperature,
        maxTokens = maxTokens,
        topP = topP,
        frequencyPenalty = frequencyPenalty,
        presencePenalty = presencePenalty
    )
} 