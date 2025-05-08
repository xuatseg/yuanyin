/**
 * LLM 仓库接口定义
 * 
 * 该接口定义了 LLM 功能的核心业务逻辑：
 * 1. 文本生成（同步和流式）
 * 2. 图像分析
 * 3. 响应历史管理
 * 
 * 作为领域层的一部分，该接口：
 * - 定义了业务操作的契约
 * - 隐藏了数据源的具体实现
 * - 提供了统一的数据访问接口
 */
package com.looirobot.domain.repository

import com.looirobot.domain.model.LLMRequest
import com.looirobot.domain.model.LLMResponse
import com.looirobot.domain.model.ImageAnalysisResponse
import kotlinx.coroutines.flow.Flow

interface LLMRepository {
    suspend fun generateCompletion(request: LLMRequest): LLMResponse
    suspend fun generateStreamingCompletion(request: LLMRequest): Flow<LLMResponse>
    suspend fun analyzeImage(imagePath: String): ImageAnalysisResponse
    suspend fun saveResponse(response: LLMResponse)
    fun getResponseHistory(): Flow<List<LLMResponse>>
    suspend fun clearHistory()
} 