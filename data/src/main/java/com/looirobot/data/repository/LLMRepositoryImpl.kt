/**
 * LLM 仓库实现类
 * 
 * 该实现类负责：
 * 1. 协调远程和本地数据源
 * 2. 实现业务逻辑
 * 3. 管理数据流向
 * 
 * 主要功能：
 * - 从远程数据源获取响应
 * - 将响应保存到本地数据源
 * - 提供历史记录查询
 * - 处理数据清理
 * 
 * 技术特点：
 * - 使用依赖注入管理数据源
 * - 支持响应数据的自动持久化
 * - 实现了完整的仓库模式
 */
package com.looirobot.data.repository

import com.looirobot.data.model.LLMRequest as DataLLMRequest
import com.looirobot.data.model.LLMResponse as DataLLMResponse
import com.looirobot.data.model.ImageAnalysisResponse as DataImageAnalysisResponse
import com.looirobot.data.model.TokenUsage as DataTokenUsage
import com.looirobot.data.source.LLMDataSource
import com.looirobot.domain.model.LLMRequest
import com.looirobot.domain.model.LLMResponse
import com.looirobot.domain.model.ImageAnalysisResponse
import com.looirobot.domain.model.TokenUsage
import com.looirobot.domain.repository.LLMRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LLMRepositoryImpl @Inject constructor(
    private val remoteDataSource: LLMDataSource,
    private val localDataSource: LLMDataSource
) : LLMRepository {
    
    override suspend fun generateCompletion(request: LLMRequest): LLMResponse {
        val dataRequest = DataLLMRequest(
            prompt = request.prompt,
            model = "gpt-3.5-turbo", // 默认模型
            temperature = request.temperature,
            maxTokens = request.maxTokens,
            topP = request.topP,
            frequencyPenalty = request.frequencyPenalty,
            presencePenalty = request.presencePenalty
        )
        
        val dataResponse = remoteDataSource.generateCompletion(dataRequest)
        localDataSource.saveResponse(dataResponse)
        
        return LLMResponse(
            id = dataResponse.id,
            text = dataResponse.text,
            finishReason = dataResponse.finishReason,
            usage = dataResponse.usage.toDomainModel(),
            timestamp = dataResponse.timestamp
        )
    }
    
    override suspend fun generateStreamingCompletion(request: LLMRequest): Flow<LLMResponse> {
        val dataRequest = DataLLMRequest(
            prompt = request.prompt,
            model = "gpt-3.5-turbo", // 默认模型
            temperature = request.temperature,
            maxTokens = request.maxTokens,
            topP = request.topP,
            frequencyPenalty = request.frequencyPenalty,
            presencePenalty = request.presencePenalty
        )
        
        return remoteDataSource.generateStreamingCompletion(dataRequest)
            .map { dataResponse ->
                localDataSource.saveResponse(dataResponse)
                LLMResponse(
                    id = dataResponse.id,
                    text = dataResponse.text,
                    finishReason = dataResponse.finishReason,
                    usage = dataResponse.usage.toDomainModel(),
                    timestamp = dataResponse.timestamp
                )
            }
    }
    
    override suspend fun analyzeImage(imagePath: String): ImageAnalysisResponse {
        val dataResponse = remoteDataSource.analyzeImage(imagePath)
        return ImageAnalysisResponse(
            description = dataResponse.description,
            tags = dataResponse.tags,
            confidence = dataResponse.confidence,
            timestamp = dataResponse.timestamp
        )
    }
    
    override suspend fun saveResponse(response: LLMResponse) {
        val dataResponse = DataLLMResponse(
            id = response.id,
            text = response.text,
            model = "gpt-3.5-turbo", // 默认模型
            finishReason = response.finishReason,
            usage = response.usage?.toDataModel() ?: DataTokenUsage(0, 0, 0),
            timestamp = response.timestamp
        )
        localDataSource.saveResponse(dataResponse)
    }
    
    override fun getResponseHistory(): Flow<List<LLMResponse>> = flow {
        localDataSource.getResponseHistory().collect { dataResponses ->
            emit(dataResponses.map { dataResponse ->
                LLMResponse(
                    id = dataResponse.id,
                    text = dataResponse.text,
                    finishReason = dataResponse.finishReason,
                    usage = dataResponse.usage.toDomainModel(),
                    timestamp = dataResponse.timestamp
                )
            })
        }
    }
    
    override suspend fun clearHistory() {
        localDataSource.clearHistory()
    }

    private fun DataTokenUsage.toDomainModel(): TokenUsage {
        return TokenUsage(
            promptTokens = this.promptTokens,
            completionTokens = this.completionTokens,
            totalTokens = this.totalTokens
        )
    }

    private fun TokenUsage.toDataModel(): DataTokenUsage {
        return DataTokenUsage(
            promptTokens = this.promptTokens,
            completionTokens = this.completionTokens,
            totalTokens = this.totalTokens
        )
    }
} 