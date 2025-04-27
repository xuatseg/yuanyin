package com.xuatseg.yuanyin.bluetooth

import kotlinx.coroutines.flow.Flow

/**
 * 蓝牙设备接口
 */
interface IBluetooth {
    /**
     * 扫描设备
     * @return 扫描结果流
     */
    fun scanDevices(): Flow<BluetoothEvent>

    /**
     * 停止扫描
     */
    fun stopScan()

    /**
     * 连接设备
     * @param deviceId 设备ID
     */
    suspend fun connect(deviceId: String): BluetoothResult

    /**
     * 断开连接
     */
    suspend fun disconnect(): BluetoothResult

    /**
     * 发送数据
     * @param data 要发送的数据
     */
    suspend fun sendData(data: ByteArray): BluetoothResult

    /**
     * 接收数据流
     * @return 接收到的数据流
     */
    fun receiveData(): Flow<ByteArray>

    /**
     * 获取连接状态
     * @return 连接状态流
     */
    fun getConnectionState(): Flow<BluetoothConnectionState>

    /**
     * 释放资源
     */
    fun release()
}

/**
 * 蓝牙事件
 */
sealed class BluetoothEvent {
    data class DeviceFound(val device: BluetoothDeviceInfo) : BluetoothEvent()
    data class ScanFinished(val devices: List<BluetoothDeviceInfo>) : BluetoothEvent()
    data class Error(val message: String) : BluetoothEvent()
}

/**
 * 蓝牙设备信息
 */
data class BluetoothDeviceInfo(
    val id: String,
    val name: String,
    val address: String,
    val rssi: Int,
    val deviceType: BluetoothDeviceType
)

/**
 * 蓝牙设备类型
 */
enum class BluetoothDeviceType {
    ROBOT,
    UNKNOWN
}

/**
 * 蓝牙连接状态
 */
enum class BluetoothConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

/**
 * 蓝牙操作结果
 */
sealed class BluetoothResult {
    object Success : BluetoothResult()
    data class Failure(val error: BluetoothError) : BluetoothResult()
}

/**
 * 蓝牙错误类型
 */
sealed class BluetoothError {
    object NotEnabled : BluetoothError()
    object NotSupported : BluetoothError()
    object PermissionDenied : BluetoothError()
    object ConnectionFailed : BluetoothError()
    object Disconnected : BluetoothError()
    object Timeout : BluetoothError()
    data class Unknown(val message: String) : BluetoothError()
}

/**
 * 蓝牙配置接口
 */
interface IBluetoothConfig {
    /**
     * 获取蓝牙服务UUID
     */
    fun getServiceUUID(): String

    /**
     * 获取特征值UUID
     */
    fun getCharacteristicUUID(): String

    /**
     * 获取扫描超时时间（毫秒）
     */
    fun getScanTimeout(): Long

    /**
     * 获取连接超时时间（毫秒）
     */
    fun getConnectionTimeout(): Long
}

/**
 * 蓝牙数据解析接口
 */
interface IBluetoothDataParser {
    /**
     * 解析接收到的数据
     * @param data 原始数据
     * @return 解析后的数据
     */
    fun parseReceivedData(data: ByteArray): Any

    /**
     * 打包要发送的数据
     * @param command 命令
     * @param params 参数
     * @return 打包后的数据
     */
    fun packData(command: String, params: Map<String, Any>): ByteArray
}

/**
 * 蓝牙权限接口
 */
interface IBluetoothPermission {
    /**
     * 检查是否有所需权限
     */
    fun hasRequiredPermissions(): Boolean

    /**
     * 请求权限
     */
    fun requestPermissions()

    /**
     * 检查蓝牙是否启用
     */
    fun isBluetoothEnabled(): Boolean

    /**
     * 请求启用蓝牙
     */
    fun requestEnableBluetooth()
}
