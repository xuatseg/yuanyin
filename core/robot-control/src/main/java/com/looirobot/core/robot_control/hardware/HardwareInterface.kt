package com.looirobot.core.robot_control.hardware

import java.io.InputStream
import java.io.OutputStream

interface HardwareInterface {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun sendCommand(command: ByteArray)
    suspend fun readResponse(): ByteArray
    fun getInputStream(): InputStream
    fun getOutputStream(): OutputStream
}

sealed class HardwareError : Exception() {
    data class ConnectionError(override val message: String) : HardwareError()
    data class CommunicationError(override val message: String) : HardwareError()
    data class TimeoutError(override val message: String) : HardwareError()
    data class ProtocolError(override val message: String) : HardwareError()
}

object HardwareProtocol {
    // 命令类型
    const val CMD_MOTOR_CONTROL: Byte = 0x01.toByte()
    const val CMD_SENSOR_READ: Byte = 0x02.toByte()
    const val CMD_BATTERY_READ: Byte = 0x03.toByte()
    const val CMD_SYSTEM_STATUS: Byte = 0x04.toByte()
    
    // 响应类型
    const val RESP_SUCCESS: Byte = 0x00.toByte()
    const val RESP_ERROR: Byte = 0xFF.toByte()
    
    // 数据长度
    const val HEADER_LENGTH = 4
    const val CHECKSUM_LENGTH = 1
    
    // 超时设置
    const val DEFAULT_TIMEOUT = 1000L // 毫秒
    
    // 构建命令包
    fun buildCommand(type: Byte, data: ByteArray): ByteArray {
        val length = data.size
        val packet = ByteArray(HEADER_LENGTH + length + CHECKSUM_LENGTH)
        
        // 包头
        packet[0] = 0xAA.toByte() // 起始标记
        packet[1] = type // 命令类型
        packet[2] = length.toByte() // 数据长度
        
        // 数据
        System.arraycopy(data, 0, packet, HEADER_LENGTH, length)
        
        // 校验和
        packet[packet.size - 1] = calculateChecksum(packet, 0, packet.size - 1)
        
        return packet
    }
    
    // 解析响应包
    fun parseResponse(response: ByteArray): ByteArray {
        if (response.size < HEADER_LENGTH + CHECKSUM_LENGTH) {
            throw HardwareError.ProtocolError("Response too short")
        }
        
        // 验证起始标记
        if (response[0] != 0xAA.toByte()) {
            throw HardwareError.ProtocolError("Invalid start marker")
        }
        
        // 验证校验和
        val checksum = calculateChecksum(response, 0, response.size - 1)
        if (checksum != response[response.size - 1]) {
            throw HardwareError.ProtocolError("Checksum error")
        }
        
        // 提取数据
        val length = response[2].toInt() and 0xFF
        return response.copyOfRange(HEADER_LENGTH, HEADER_LENGTH + length)
    }
    
    // 计算校验和
    private fun calculateChecksum(data: ByteArray, offset: Int, length: Int): Byte {
        var sum = 0
        for (i in offset until offset + length) {
            sum += data[i].toInt() and 0xFF
        }
        return (sum and 0xFF).toByte()
    }
} 