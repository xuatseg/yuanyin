package com.xuatseg.yuanyin.bluetooth

import kotlinx.coroutines.flow.Flow

/**
 * 蓝牙服务接口
 */
interface IBluetoothService {
    /**
     * 初始化服务
     * @param config 服务配置
     */
    fun initialize(config: BluetoothServiceConfig)

    /**
     * 启动服务
     */
    fun start()

    /**
     * 停止服务
     */
    fun stop()

    /**
     * 重启服务
     */
    fun restart()

    /**
     * 获取服务状态
     * @return 服务状态流
     */
    fun getServiceState(): Flow<ServiceState>

    /**
     * 注册状态监听器
     * @param listener 状态监听器
     */
    fun registerStateListener(listener: IServiceStateListener)

    /**
     * 注销状态监听器
     * @param listener 状态监听器
     */
    fun unregisterStateListener(listener: IServiceStateListener)

    /**
     * 获取服务统计信息
     * @return 服务统计信息
     */
    fun getServiceStatistics(): ServiceStatistics
}

/**
 * 蓝牙服务配置
 */
data class BluetoothServiceConfig(
    val serviceUUID: String,                  // 服务UUID
    val characteristicUUID: String,           // 特征值UUID
    val scanTimeout: Long = 10000,            // 扫描超时时间（毫秒）
    val connectionTimeout: Long = 30000,      // 连接超时时间（毫秒）
    val autoReconnect: Boolean = true,        // 是否自动重连
    val maxReconnectAttempts: Int = 3,        // 最大重连次数
    val reconnectInterval: Long = 5000,       // 重连间隔（毫秒）
    val enableLogging: Boolean = false        // 是否启用日志
)

/**
 * 服务状态
 */
sealed class ServiceState {
    object Initializing : ServiceState()      // 初始化中
    object Running : ServiceState()           // 运行中
    object Stopped : ServiceState()           // 已停止
    data class Error(val error: ServiceError) : ServiceState()  // 错误状态
}

/**
 * 服务错误
 */
sealed class ServiceError {
    object InitializationFailed : ServiceError()   // 初始化失败
    object BluetoothNotAvailable : ServiceError()  // 蓝牙不可用
    object ServiceStartFailed : ServiceError()     // 服务启动失败
    data class Unknown(val message: String) : ServiceError()  // 未知错误
}

/**
 * 服务统计信息
 */
data class ServiceStatistics(
    val startTime: Long,                  // 服务启动时间
    val uptime: Long,                     // 运行时长
    val totalConnections: Int,            // 总连接次数
    val totalPacketsSent: Long,          // 发送的数据包总数
    val totalPacketsReceived: Long,       // 接收的数据包总数
    val totalErrors: Int,                 // 错误总数
    val lastErrorTime: Long?,             // 最后一次错误时间
    val lastErrorMessage: String?         // 最后一次错误信息
)

/**
 * 服务状态监听器
 */
interface IServiceStateListener {
    /**
     * 服务状态变化回调
     * @param state 新状态
     */
    fun onStateChanged(state: ServiceState)

    /**
     * 错误回调
     * @param error 错误信息
     */
    fun onError(error: ServiceError)

    /**
     * 统计信息更新回调
     * @param statistics 统计信息
     */
    fun onStatisticsUpdated(statistics: ServiceStatistics)
}

/**
 * 服务生命周期监听器
 */
interface IServiceLifecycleListener {
    /**
     * 服务创建回调
     */
    fun onCreate()

    /**
     * 服务启动回调
     */
    fun onStart()

    /**
     * 服务停止回调
     */
    fun onStop()

    /**
     * 服务销毁回调
     */
    fun onDestroy()
}

/**
 * 服务配置提供者接口
 */
interface IServiceConfigProvider {
    /**
     * 获取服务配置
     * @return 服务配置
     */
    fun getServiceConfig(): BluetoothServiceConfig

    /**
     * 更新服务配置
     * @param config 新的服务配置
     */
    fun updateServiceConfig(config: BluetoothServiceConfig)
}
