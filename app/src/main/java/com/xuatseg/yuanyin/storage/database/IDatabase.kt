package com.xuatseg.yuanyin.storage.database

import com.xuatseg.yuanyin.chat.ChatMessage
import com.xuatseg.yuanyin.robot.SensorData
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * 数据库接口
 */
interface IDatabase {
    /**
     * 初始化数据库
     */
    suspend fun initialize()

    /**
     * 关闭数据库
     */
    suspend fun close()

    /**
     * 清理数据库
     */
    suspend fun cleanup()

    /**
     * 获取传感器数据存储
     */
    fun getSensorStorage(): ISensorStorage

    /**
     * 获取对话历史存储
     */
    fun getChatStorage(): IChatStorage
}

/**
 * 传感器数据存储接口
 */
interface ISensorStorage {
    /**
     * 保存传感器数据
     * @param data 传感器数据
     */
    suspend fun saveSensorData(data: SensorData)

    /**
     * 批量保存传感器数据
     * @param dataList 传感器数据列表
     */
    suspend fun saveSensorDataBatch(dataList: List<SensorData>)

    /**
     * 查询传感器数据
     * @param query 查询条件
     * @return 传感器数据流
     */
    fun querySensorData(query: SensorDataQuery): Flow<List<SensorData>>

    /**
     * 删除传感器数据
     * @param criteria 删除条件
     * @return 删除的数据数量
     */
    suspend fun deleteSensorData(criteria: DeleteCriteria): Int
}

/**
 * 对话历史存储接口
 */
interface IChatStorage {
    /**
     * 保存对话消息
     * @param message 对话消息
     */
    suspend fun saveMessage(message: ChatMessage)

    /**
     * 批量保存对话消息
     * @param messages 消息列表
     */
    suspend fun saveMessageBatch(messages: List<ChatMessage>)

    /**
     * 查询对话历史
     * @param query 查询条件
     * @return 对话消息流
     */
    fun queryMessages(query: ChatMessageQuery): Flow<List<ChatMessage>>

    /**
     * 删除对话历史
     * @param criteria 删除条件
     * @return 删除的消息数量
     */
    suspend fun deleteMessages(criteria: DeleteCriteria): Int
}

/**
 * 传感器数据查询条件
 */
data class SensorDataQuery(
    val timeRange: ClosedRange<Instant>? = null,
    val sensorTypes: Set<String>? = null,
    val limit: Int? = null,
    val offset: Int = 0,
    val sortOrder: SortOrder = SortOrder.DESCENDING
)

/**
 * 对话消息查询条件
 */
data class ChatMessageQuery(
    val timeRange: ClosedRange<Instant>? = null,
    val messageTypes: Set<String>? = null,
    val senderIds: Set<String>? = null,
    val limit: Int? = null,
    val offset: Int = 0,
    val sortOrder: SortOrder = SortOrder.DESCENDING
)

/**
 * 删除条件
 */
data class DeleteCriteria(
    val timeRange: ClosedRange<Instant>? = null,
    val types: Set<String>? = null,
    val ids: Set<String>? = null
)

/**
 * 排序顺序
 */
enum class SortOrder {
    ASCENDING,
    DESCENDING
}

/**
 * 数据库配置接口
 */
interface IDatabaseConfig {
    /**
     * 获取数据库名称
     */
    fun getDatabaseName(): String

    /**
     * 获取数据库版本
     */
    fun getDatabaseVersion(): Int

    /**
     * 获取数据保留策略
     */
    fun getRetentionPolicy(): RetentionPolicy
}

/**
 * 数据保留策略
 */
data class RetentionPolicy(
    val maxStorageSize: Long,
    val maxRecordAge: Long,
    val cleanupInterval: Long
)

/**
 * 数据库监控接口
 */
interface IDatabaseMonitor {
    /**
     * 记录数据库操作
     * @param operation 操作信息
     */
    fun recordOperation(operation: DatabaseOperation)

    /**
     * 获取数据库统计信息
     */
    fun getDatabaseStats(): DatabaseStats

    /**
     * 获取性能指标
     */
    fun getPerformanceMetrics(): DatabaseMetrics
}

/**
 * 数据库操作
 */
data class DatabaseOperation(
    val type: OperationType,
    val table: String,
    val timestamp: Instant,
    val duration: Long,
    val recordCount: Int,
    val error: String? = null
)

/**
 * 操作类型
 */
enum class OperationType {
    INSERT,
    QUERY,
    UPDATE,
    DELETE,
    BATCH_INSERT,
    BATCH_UPDATE,
    BATCH_DELETE
}

/**
 * 数据库统计信息
 */
data class DatabaseStats(
    val totalRecords: Map<String, Long>,
    val storageSize: Long,
    val lastCleanup: Instant,
    val operationCounts: Map<OperationType, Long>
)

/**
 * 数据库性能指标
 */
data class DatabaseMetrics(
    val averageQueryTime: Long,
    val averageInsertTime: Long,
    val cacheHitRate: Float,
    val indexEfficiency: Float
)

/**
 * 数据库错误
 */
sealed class DatabaseError : Exception() {
    data class ConnectionError(override val message: String) : DatabaseError()
    data class QueryError(override val message: String) : DatabaseError()
    data class StorageError(override val message: String) : DatabaseError()
    data class ConfigurationError(override val message: String) : DatabaseError()
}
