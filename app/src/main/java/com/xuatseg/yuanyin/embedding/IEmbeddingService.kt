package com.xuatseg.yuanyin.embedding

import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * 嵌入服务接口
 */
interface IEmbeddingService {
    /**
     * 生成文本嵌入向量
     * @param text 输入文本
     * @return 嵌入向量
     */
    suspend fun generateEmbedding(text: String): EmbeddingVector

    /**
     * 批量生成嵌入向量
     * @param texts 文本列表
     * @return 嵌入向量列表
     */
    suspend fun generateEmbeddings(texts: List<String>): List<EmbeddingVector>

    /**
     * 计算相似度
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度分数
     */
    fun calculateSimilarity(vector1: EmbeddingVector, vector2: EmbeddingVector): Float

    /**
     * 搜索相似向量
     * @param query 查询向量
     * @param limit 返回数量限制
     * @return 相似度结果列表
     */
    suspend fun searchSimilar(query: EmbeddingVector, limit: Int): List<SimilarityResult>
    fun loadModel(embeddingModelConfig: Any)
}

/**
 * 向量存储接口
 */
interface IVectorStore {
    /**
     * 存储向量
     * @param id 向量ID
     * @param vector 向量数据
     * @param metadata 元数据
     */
    suspend fun store(id: String, vector: EmbeddingVector, metadata: Map<String, Any>? = null)

    /**
     * 批量存储向量
     * @param vectors 向量数据列表
     */
    suspend fun storeBatch(vectors: List<VectorEntry>)

    /**
     * 获取向量
     * @param id 向量ID
     * @return 向量数据
     */
    suspend fun get(id: String): VectorEntry?

    /**
     * 删除向量
     * @param id 向量ID
     */
    suspend fun delete(id: String)

    /**
     * 搜索相似向量
     * @param query 查询向量
     * @param limit 返回数量限制
     * @param filter 过滤条件
     * @return 搜索结果
     */
    suspend fun search(
        query: EmbeddingVector,
        limit: Int,
        filter: VectorFilter? = null
    ): List<SearchResult>
}

/**
 * 嵌入模型管理接口
 */
interface IEmbeddingModelManager {
    /**
     * 加载模型
     * @param modelConfig 模型配置
     */
    suspend fun loadModel(modelConfig: EmbeddingModelConfig)

    /**
     * 卸载模型
     */
    suspend fun unloadModel()

    /**
     * 更新模型
     * @param modelFile 模型文件
     */
    suspend fun updateModel(modelFile: File)

    /**
     * 获取模型信息
     */
    fun getModelInfo(): EmbeddingModelInfo

    /**
     * 验证模型
     * @param modelFile 模型文件
     * @return 验证结果
     */
    suspend fun validateModel(modelFile: File): ModelValidationResult
}

/**
 * 嵌入向量
 */
data class EmbeddingVector(
    val values: FloatArray,
    val dimension: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmbeddingVector
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}

/**
 * 向量条目
 */
data class VectorEntry(
    val id: String,
    val vector: EmbeddingVector,
    val metadata: Map<String, Any>? = null
)

/**
 * 相似度结果
 */
data class SimilarityResult(
    val entry: VectorEntry,
    val score: Float
)

/**
 * 搜索结果
 */
data class SearchResult(
    val id: String,
    val score: Float,
    val vector: EmbeddingVector,
    val metadata: Map<String, Any>? = null
)

/**
 * 向量过滤器
 */
data class VectorFilter(
    val metadata: Map<String, Any>,
    val minScore: Float? = null,
    val maxResults: Int? = null
)

/**
 * 嵌入模型配置
 */
data class EmbeddingModelConfig(
    val modelPath: String,
    val dimension: Int,
    val quantization: QuantizationType,
    val deviceType: DeviceType,
    val batchSize: Int,
    val threads: Int
)

/**
 * 量化类型
 */
enum class QuantizationType {
    NONE,
    INT8,
    INT4,
    FLOAT16
}

/**
 * 设备类型
 */
enum class DeviceType {
    CPU,
    GPU,
    NPU
}

/**
 * 嵌入模型信息
 */
data class EmbeddingModelInfo(
    val name: String,
    val version: String,
    val dimension: Int,
    val quantization: QuantizationType,
    val size: Long,
    val lastUpdated: Long
)

/**
 * 模型验证结果
 */
data class ModelValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)

/**
 * 嵌入性能监控接口
 */
interface IEmbeddingMonitor {
    /**
     * 记录嵌入生成
     * @param textLength 文本长度
     * @param duration 处理时长
     */
    fun recordEmbeddingGeneration(textLength: Int, duration: Long)

    /**
     * 记录向量搜索
     * @param queryVector 查询向量
     * @param resultCount 结果数量
     * @param duration 搜索时长
     */
    fun recordVectorSearch(
        queryVector: EmbeddingVector,
        resultCount: Int,
        duration: Long
    )

    /**
     * 获取性能统计
     */
    fun getPerformanceStats(): EmbeddingPerformanceStats

    /**
     * 观察性能指标
     */
    fun observePerformanceMetrics(): Flow<PerformanceMetric>
}

/**
 * 嵌入性能统计
 */
data class EmbeddingPerformanceStats(
    val averageEmbeddingTime: Long,
    val averageSearchTime: Long,
    val totalEmbeddings: Int,
    val totalSearches: Int,
    val memoryUsage: Long,
    val cacheHitRate: Float
)

/**
 * 性能指标
 */
data class PerformanceMetric(
    val type: MetricType,
    val value: Float,
    val timestamp: Long
)

/**
 * 指标类型
 */
enum class MetricType {
    EMBEDDING_TIME,
    SEARCH_TIME,
    MEMORY_USAGE,
    CACHE_HIT_RATE,
    CPU_USAGE,
    GPU_USAGE
}
