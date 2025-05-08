/**
 * LLM 数据源接口定义
 * 
 * 该接口定义了与 LLM 服务交互的核心方法：
 * 1. 生成文本响应（同步和流式）
 * 2. 图像分析
 * 3. 响应历史记录管理
 * 
 * 实现类需要处理：
 * - 远程数据源：与 LLM API 的直接交互
 * - 本地数据源：响应数据的持久化存储
 */
package com.looirobot.data.source

import com.looirobot.data.model.LLMRequest
import com.looirobot.data.model.LLMResponse
import com.looirobot.data.model.ImageAnalysisResponse
import kotlinx.coroutines.flow.Flow

interface LLMDataSource {
    suspend fun generateCompletion(request: LLMRequest): LLMResponse
    suspend fun generateStreamingCompletion(request: LLMRequest): Flow<LLMResponse>
    suspend fun analyzeImage(imagePath: String): ImageAnalysisResponse
    suspend fun saveResponse(response: LLMResponse)
    suspend fun getResponseHistory(): Flow<List<LLMResponse>>
    suspend fun clearHistory()
} 