/**
 * LLM 本地数据源实现
 * 
 * 该实现类负责：
 * 1. 响应数据的本地持久化存储
 * 2. 历史记录管理
 * 3. 数据查询和清理
 * 
 * 技术特点：
 * - 使用 Room 数据库进行本地存储
 * - 支持响应历史记录的增删改查
 * - 使用 Flow 进行数据流处理
 * 
 * 包含的数据库相关类：
 * - LLMResponseEntity：数据库实体类
 * - LLMResponseDao：数据访问对象
 * - LLMDatabase：Room 数据库类
 */
package com.looirobot.data.source

import android.content.Context
import androidx.room.*
import com.looirobot.data.model.LLMRequest
import com.looirobot.data.model.LLMResponse
import com.looirobot.data.model.ImageAnalysisResponse
import com.looirobot.data.model.TokenUsage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLLMDataSource @Inject constructor(
    private val context: Context
) : LLMDataSource {
    private val db = Room.databaseBuilder(
        context,
        LLMDatabase::class.java,
        "llm_database"
    ).build()
    
    override suspend fun generateCompletion(request: LLMRequest): LLMResponse {
        throw UnsupportedOperationException("Local data source does not support generation")
    }
    
    override suspend fun generateStreamingCompletion(request: LLMRequest): Flow<LLMResponse> {
        throw UnsupportedOperationException("Local data source does not support streaming")
    }
    
    override suspend fun analyzeImage(imagePath: String): ImageAnalysisResponse {
        throw UnsupportedOperationException("Local data source does not support image analysis")
    }
    
    override suspend fun saveResponse(response: LLMResponse) {
        db.llmResponseDao().insert(response.toEntity())
    }
    
    override suspend fun getResponseHistory(): Flow<List<LLMResponse>> {
        return db.llmResponseDao().getAllResponses().map { entities ->
            entities.map { it.toModel() }
        }
    }
    
    override suspend fun clearHistory() {
        db.llmResponseDao().deleteAll()
    }
}

@Entity(tableName = "llm_responses")
data class LLMResponseEntity(
    @PrimaryKey val id: String,
    val text: String,
    val model: String,
    val finishReason: String?,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val timestamp: Long
)

@Dao
interface LLMResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: LLMResponseEntity)
    
    @Query("SELECT * FROM llm_responses ORDER BY timestamp DESC")
    fun getAllResponses(): Flow<List<LLMResponseEntity>>
    
    @Query("DELETE FROM llm_responses")
    suspend fun deleteAll()
}

@Database(entities = [LLMResponseEntity::class], version = 1)
abstract class LLMDatabase : RoomDatabase() {
    abstract fun llmResponseDao(): LLMResponseDao
}

private fun LLMResponse.toEntity() = LLMResponseEntity(
    id = id,
    text = text,
    model = model,
    finishReason = finishReason,
    promptTokens = usage.promptTokens,
    completionTokens = usage.completionTokens,
    totalTokens = usage.totalTokens,
    timestamp = timestamp
)

private fun LLMResponseEntity.toModel() = LLMResponse(
    id = id,
    text = text,
    model = model,
    finishReason = finishReason,
    usage = TokenUsage(
        promptTokens = promptTokens,
        completionTokens = completionTokens,
        totalTokens = totalTokens
    ),
    timestamp = timestamp
) 