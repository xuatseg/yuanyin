package com.xuatseg.yuanyin.storage.file

import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.Instant

/**
 * 文件存储接口
 */
interface IFileStorage {
    /**
     * 写入文件
     * @param path 文件路径
     * @param content 文件内容
     * @param options 写入选项
     */
    suspend fun writeFile(path: String, content: ByteArray, options: WriteOptions? = null)

    /**
     * 读取文件
     * @param path 文件路径
     * @return 文件内容
     */
    suspend fun readFile(path: String): ByteArray

    /**
     * 删除文件
     * @param path 文件路径
     * @return 是否成功
     */
    suspend fun deleteFile(path: String): Boolean

    /**
     * 检查文件是否存在
     * @param path 文件路径
     * @return 是否存在
     */
    fun fileExists(path: String): Boolean

    /**
     * 获取文件信息
     * @param path 文件路径
     * @return 文件信息
     */
    fun getFileInfo(path: String): FileInfo

    /**
     * 列出目录内容
     * @param path 目录路径
     * @param filter 过滤器
     * @return 文件列表
     */
    fun listDirectory(path: String, filter: FileFilter? = null): List<FileInfo>
}

/**
 * 日志存储接口
 */
interface ILogStorage {
    /**
     * 写入日志
     * @param entry 日志条目
     */
    suspend fun writeLog(entry: LogEntry)

    /**
     * 批量写入日志
     * @param entries 日志条目列表
     */
    suspend fun writeLogBatch(entries: List<LogEntry>)

    /**
     * 查询日志
     * @param query 查询条件
     * @return 日志条目流
     */
    fun queryLogs(query: LogQuery): Flow<List<LogEntry>>

    /**
     * 清理日志
     * @param criteria 清理条件
     * @return 清理的日志数量
     */
    suspend fun cleanupLogs(criteria: LogCleanupCriteria): Int

    /**
     * 获取日志文件
     * @param date 日期
     * @return 日志文件
     */
    fun getLogFile(date: Instant): File
}

/**
 * 文件系统监控接口
 */
interface IFileSystemMonitor {
    /**
     * 开始监控
     * @param path 监控路径
     * @param filter 过滤器
     * @return 文件系统事件流
     */
    fun startMonitoring(path: String, filter: FileFilter? = null): Flow<FileSystemEvent>

    /**
     * 停止监控
     */
    fun stopMonitoring()

    /**
     * 获取存储统计信息
     */
    fun getStorageStats(): StorageStats
}

/**
 * 写入选项
 */
data class WriteOptions(
    val append: Boolean = false,
    val createDirectories: Boolean = true,
    val overwrite: Boolean = true,
    val encryption: EncryptionOptions? = null
)

/**
 * 加密选项
 */
data class EncryptionOptions(
    val algorithm: String,
    val key: ByteArray,
    val iv: ByteArray? = null
)

/**
 * 文件信息
 */
data class FileInfo(
    val path: String,
    val name: String,
    val size: Long,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val isDirectory: Boolean,
    val attributes: Map<String, Any>
)

/**
 * 文件过滤器
 */
data class FileFilter(
    val extensions: Set<String>? = null,
    val minSize: Long? = null,
    val maxSize: Long? = null,
    val modifiedAfter: Instant? = null,
    val modifiedBefore: Instant? = null,
    val namePattern: Regex? = null
)

/**
 * 日志条目
 */
data class LogEntry(
    val timestamp: Instant,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val metadata: Map<String, Any>? = null,
    val throwable: Throwable? = null
)

/**
 * 日志级别
 */
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

/**
 * 日志查询条件
 */
data class LogQuery(
    val timeRange: ClosedRange<Instant>? = null,
    val levels: Set<LogLevel>? = null,
    val tags: Set<String>? = null,
    val messagePattern: Regex? = null,
    val limit: Int? = null,
    val offset: Int = 0
)

/**
 * 日志清理条件
 */
data class LogCleanupCriteria(
    val olderThan: Instant? = null,
    val levels: Set<LogLevel>? = null,
    val tags: Set<String>? = null,
    val maxSize: Long? = null
)

/**
 * 文件系统事件
 */
sealed class FileSystemEvent {
    data class Created(val file: FileInfo) : FileSystemEvent()
    data class Modified(val file: FileInfo) : FileSystemEvent()
    data class Deleted(val path: String) : FileSystemEvent()
    data class Moved(val from: String, val to: String) : FileSystemEvent()
}

/**
 * 存储统计信息
 */
data class StorageStats(
    val totalSpace: Long,
    val usedSpace: Long,
    val freeSpace: Long,
    val fileCount: Long,
    val directoryCount: Long
)

/**
 * 文件存储配置接口
 */
interface IFileStorageConfig {
    /**
     * 获取根目录
     */
    fun getRootDirectory(): String

    /**
     * 获取日志目录
     */
    fun getLogDirectory(): String

    /**
     * 获取临时目录
     */
    fun getTempDirectory(): String

    /**
     * 获取存储策略
     */
    fun getStoragePolicy(): StoragePolicy
}

/**
 * 存储策略
 */
data class StoragePolicy(
    val maxFileSize: Long,
    val maxTotalSize: Long,
    val retentionPeriod: Long,
    val compressionEnabled: Boolean,
    val encryptionEnabled: Boolean
)

/**
 * 文件存储错误
 */
sealed class FileStorageError : Exception() {
    data class IOError(override val message: String) : FileStorageError()
    data class SecurityError(override val message: String) : FileStorageError()
    data class QuotaExceededError(override val message: String) : FileStorageError()
    data class InvalidPathError(override val message: String) : FileStorageError()
}
