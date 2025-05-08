package com.looirobot.core.robot_control.hardware

import kotlin.math.absoluteValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotorController @Inject constructor(
    private val hardwareInterface: HardwareInterface
) {
    private var isInitialized = false
    
    suspend fun initialize() {
        if (!isInitialized) {
            hardwareInterface.connect()
            isInitialized = true
        }
    }
    
    suspend fun moveForward(speed: Float) {
        val data = ByteArray(2)
        data[0] = 0x01 // 前进命令
        data[1] = (speed * 255).toInt().toByte() // 速度值
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to move forward")
        }
    }
    
    suspend fun moveBackward(speed: Float) {
        val data = ByteArray(2)
        data[0] = 0x02 // 后退命令
        data[1] = (speed * 255).toInt().toByte() // 速度值
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to move backward")
        }
    }
    
    suspend fun turnLeft(speed: Float) {
        val data = ByteArray(2)
        data[0] = 0x03 // 左转命令
        data[1] = (speed * 255).toInt().toByte() // 速度值
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to turn left")
        }
    }
    
    suspend fun turnRight(speed: Float) {
        val data = ByteArray(2)
        data[0] = 0x04 // 右转命令
        data[1] = (speed * 255).toInt().toByte() // 速度值
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to turn right")
        }
    }
    
    suspend fun rotate(angle: Float) {
        val data = ByteArray(3)
        data[0] = 0x05 // 旋转命令
        data[1] = if (angle > 0) 0x01 else 0x02 // 方向
        data[2] = (angle.absoluteValue * 255 / 360).toInt().toByte() // 角度值
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to rotate")
        }
    }
    
    suspend fun stop() {
        val data = ByteArray(1)
        data[0] = 0x00 // 停止命令
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to stop")
        }
    }
    
    suspend fun setSpeed(speed: Float) {
        val data = ByteArray(2)
        data[0] = 0x06 // 设置速度命令
        data[1] = (speed * 255).toInt().toByte() // 速度值
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_MOTOR_CONTROL,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to set speed")
        }
    }
    
    suspend fun release() {
        if (isInitialized) {
            stop()
            hardwareInterface.disconnect()
            isInitialized = false
        }
    }
} 