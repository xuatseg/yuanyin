package com.looirobot.core.robot_control.hardware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothHardwareInterface @Inject constructor(
    private val context: Context,
    private val bluetoothScanner: BluetoothScanner
) : HardwareInterface {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val TIMEOUT = 10000
    }
    
    override suspend fun connect() {
        withContext(Dispatchers.IO) {
            try {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothAdapter = bluetoothManager.adapter
                
                if (bluetoothAdapter == null) {
                    throw HardwareError.ConnectionError("Bluetooth is not available")
                }
                
                if (!bluetoothAdapter!!.isEnabled) {
                    throw HardwareError.ConnectionError("Bluetooth is not enabled")
                }
                
                // 获取已配对的设备
                val pairedDevices = bluetoothAdapter!!.bondedDevices
                if (pairedDevices.isEmpty()) {
                    throw HardwareError.ConnectionError("No paired devices found")
                }
                
                // 连接到第一个已配对的设备
                val device = pairedDevices.first()
                bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                bluetoothSocket?.connect()
                
                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream
            } catch (e: IOException) {
                throw HardwareError.ConnectionError("Failed to connect to Bluetooth device: ${e.message}")
            }
        }
    }
    
    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                inputStream?.close()
                outputStream?.close()
                bluetoothSocket?.close()
                
                inputStream = null
                outputStream = null
                bluetoothSocket = null
            } catch (e: IOException) {
                throw HardwareError.ConnectionError("Failed to disconnect from Bluetooth device: ${e.message}")
            }
        }
    }
    
    override suspend fun sendCommand(command: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                outputStream?.write(command)
                outputStream?.flush()
            } catch (e: IOException) {
                throw HardwareError.CommunicationError("Failed to send command: ${e.message}")
            }
        }
    }
    
    override suspend fun readResponse(): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(HardwareProtocol.DEFAULT_TIMEOUT) {
                    val header = ByteArray(HardwareProtocol.HEADER_LENGTH)
                    var bytesRead = 0
                    
                    // 读取包头
                    while (bytesRead < header.size) {
                        val count = inputStream?.read(header, bytesRead, header.size - bytesRead)
                            ?: throw HardwareError.CommunicationError("Stream closed")
                        if (count == -1) {
                            throw HardwareError.CommunicationError("End of stream")
                        }
                        bytesRead += count
                    }
                    
                    // 获取数据长度
                    val dataLength = header[2].toInt() and 0xFF
                    val response = ByteArray(HardwareProtocol.HEADER_LENGTH + dataLength + HardwareProtocol.CHECKSUM_LENGTH)
                    
                    // 复制包头
                    System.arraycopy(header, 0, response, 0, header.size)
                    
                    // 读取数据和校验和
                    bytesRead = header.size
                    while (bytesRead < response.size) {
                        val count = inputStream?.read(response, bytesRead, response.size - bytesRead)
                            ?: throw HardwareError.CommunicationError("Stream closed")
                        if (count == -1) {
                            throw HardwareError.CommunicationError("End of stream")
                        }
                        bytesRead += count
                    }
                    
                    response
                }
            } catch (e: IOException) {
                throw HardwareError.CommunicationError("Failed to read response: ${e.message}")
            }
        }
    }
    
    override fun getInputStream(): InputStream {
        return inputStream ?: throw HardwareError.ConnectionError("Not connected")
    }
    
    override fun getOutputStream(): OutputStream {
        return outputStream ?: throw HardwareError.ConnectionError("Not connected")
    }
} 