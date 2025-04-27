package com.xuatseg.yuanyin.bluetooth

/**
 * 蓝牙通信协议接口
 */
interface IBluetoothProtocol {
    /**
     * 创建数据包
     * @param command 命令类型
     * @param payload 数据负载
     * @return 打包后的数据
     */
    fun createPacket(command: CommandType, payload: ByteArray): ByteArray

    /**
     * 解析数据包
     * @param data 接收到的数据
     * @return 解析后的数据包
     */
    fun parsePacket(data: ByteArray): BluetoothPacket

    /**
     * 验证数据包
     * @param data 数据包
     * @return 是否有效
     */
    fun validatePacket(data: ByteArray): Boolean
}

/**
 * 命令类型
 */
enum class CommandType {
    // 系统命令
    HANDSHAKE,           // 握手
    HEARTBEAT,           // 心跳
    RESET,              // 重置
    GET_STATUS,         // 获取状态

    // 控制命令
    MOVE,              // 移动
    STOP,              // 停止
    TURN,              // 转向

    // 传感器命令
    GET_SENSOR_DATA,    // 获取传感器数据
    SET_SENSOR_CONFIG,  // 设置传感器配置

    // AI相关命令
    START_RECOGNITION,  // 开始识别
    STOP_RECOGNITION,   // 停止识别
    GET_AI_RESULT,      // 获取AI结果

    // 系统设置
    GET_CONFIG,         // 获取配置
    SET_CONFIG,         // 设置配置

    // 错误处理
    ERROR,              // 错误信息

    // 自定义命令
    CUSTOM             // 自定义命令
}

/**
 * 蓝牙数据包
 */
data class BluetoothPacket(
    val header: PacketHeader,
    val payload: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BluetoothPacket
        if (header != other.header) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

/**
 * 数据包头
 */
data class PacketHeader(
    val command: CommandType,     // 命令类型
    val sequenceNumber: Int,      // 序列号
    val payloadLength: Int,       // 负载长度
    val timestamp: Long           // 时间戳
)

/**
 * 协议常量
 */
object ProtocolConstants {
    const val HEADER_SIZE = 16            // 包头大小（字节）
    const val MAX_PAYLOAD_SIZE = 512      // 最大负载大小（字节）
    const val PROTOCOL_VERSION = 1        // 协议版本

    // 包头标识
    const val PACKET_START_FLAG = 0xAA    // 包起始标识
    const val PACKET_END_FLAG = 0x55      // 包结束标识

    // 超时设置
    const val COMMAND_TIMEOUT = 5000L     // 命令超时时间（毫秒）
    const val HEARTBEAT_INTERVAL = 1000L  // 心跳间隔（毫秒）
}

/**
 * 协议错误
 */
sealed class ProtocolError {
    object InvalidPacket : ProtocolError()
    object PayloadTooLarge : ProtocolError()
    object ChecksumError : ProtocolError()
    object UnsupportedCommand : ProtocolError()
    data class Custom(val code: Int, val message: String) : ProtocolError()
}

/**
 * 协议回调接口
 */
interface IProtocolCallback {
    /**
     * 收到数据包回调
     * @param packet 数据包
     */
    fun onPacketReceived(packet: BluetoothPacket)

    /**
     * 发送数据包回调
     * @param packet 数据包
     * @param success 是否成功
     */
    fun onPacketSent(packet: BluetoothPacket, success: Boolean)

    /**
     * 错误回调
     * @param error 错误信息
     */
    fun onError(error: ProtocolError)
}
