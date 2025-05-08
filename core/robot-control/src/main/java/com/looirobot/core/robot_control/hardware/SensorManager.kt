package com.looirobot.core.robot_control.hardware

import com.looirobot.core.robot_control.SensorData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorManager @Inject constructor(
    private val hardwareInterface: HardwareInterface
) {
    private var isInitialized = false
    
    suspend fun initialize() {
        if (!isInitialized) {
            hardwareInterface.connect()
            isInitialized = true
        }
    }
    
    suspend fun getSensorData(): SensorData {
        // 读取距离传感器数据
        val distance = readDistanceSensor()
        
        // 读取温度传感器数据
        val temperature = readTemperatureSensor()
        
        // 读取湿度传感器数据
        val humidity = readHumiditySensor()
        
        // 读取电池电压
        val batteryVoltage = readBatteryVoltage()
        
        // 读取电机电流
        val motorCurrent = readMotorCurrent()
        
        return SensorData(
            distance = distance,
            temperature = temperature,
            humidity = humidity,
            batteryVoltage = batteryVoltage,
            motorCurrent = motorCurrent
        )
    }
    
    private suspend fun readDistanceSensor(): Float {
        val data = ByteArray(1)
        data[0] = 0x01 // 距离传感器命令
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_SENSOR_READ,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to read distance sensor")
        }
        
        // 将原始数据转换为实际距离（厘米）
        return (result[1].toInt() and 0xFF) * 0.1f
    }
    
    private suspend fun readTemperatureSensor(): Float {
        val data = ByteArray(1)
        data[0] = 0x02 // 温度传感器命令
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_SENSOR_READ,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to read temperature sensor")
        }
        
        // 将原始数据转换为实际温度（摄氏度）
        return (result[1].toInt() and 0xFF) * 0.5f - 20f
    }
    
    private suspend fun readHumiditySensor(): Float {
        val data = ByteArray(1)
        data[0] = 0x03 // 湿度传感器命令
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_SENSOR_READ,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to read humidity sensor")
        }
        
        // 将原始数据转换为实际湿度（百分比）
        return (result[1].toInt() and 0xFF) * 0.5f
    }
    
    private suspend fun readBatteryVoltage(): Float {
        val data = ByteArray(1)
        data[0] = 0x01 // 电池电压命令
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_BATTERY_READ,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to read battery voltage")
        }
        
        // 将原始数据转换为实际电压（伏特）
        return (result[1].toInt() and 0xFF) * 0.1f
    }
    
    private suspend fun readMotorCurrent(): Float {
        val data = ByteArray(1)
        data[0] = 0x02 // 电机电流命令
        
        val command = HardwareProtocol.buildCommand(
            HardwareProtocol.CMD_BATTERY_READ,
            data
        )
        
        hardwareInterface.sendCommand(command)
        val response = hardwareInterface.readResponse()
        val result = HardwareProtocol.parseResponse(response)
        
        if (result[0] != HardwareProtocol.RESP_SUCCESS) {
            throw HardwareError.CommunicationError("Failed to read motor current")
        }
        
        // 将原始数据转换为实际电流（安培）
        return (result[1].toInt() and 0xFF) * 0.1f
    }
    
    suspend fun release() {
        if (isInitialized) {
            hardwareInterface.disconnect()
            isInitialized = false
        }
    }
} 