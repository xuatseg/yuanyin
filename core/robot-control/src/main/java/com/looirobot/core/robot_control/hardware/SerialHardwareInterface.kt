package com.looirobot.core.robot_control.hardware

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SerialHardwareInterface @Inject constructor(
    private val context: Context
) : HardwareInterface {
    private var usbManager: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var serialIoManager: SerialInputOutputManager? = null
    
    companion object {
        private const val BAUD_RATE = 115200
        private const val TIMEOUT = 1000
    }
    
    override suspend fun connect() {
        withContext(Dispatchers.IO) {
            try {
                usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
                
                if (availableDrivers.isEmpty()) {
                    throw HardwareError.ConnectionError("No USB serial devices found")
                }
                
                // 打开第一个可用的设备
                val driver = availableDrivers[0]
                usbDevice = driver.device
                
                // 请求权限
                if (!usbManager!!.hasPermission(usbDevice)) {
                    throw HardwareError.ConnectionError("No permission to access USB device")
                }
                
                // 打开连接
                usbSerialPort = driver.ports[0]
                usbSerialPort?.open(usbManager!!.openDevice(usbDevice))
                usbSerialPort?.setParameters(BAUD_RATE, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                
                // 创建串口管理器
                serialIoManager = SerialInputOutputManager(usbSerialPort, object : SerialInputOutputManager.Listener {
                    override fun onNewData(data: ByteArray) {
                        // 处理接收到的数据
                    }

                    override fun onRunError(e: Exception) {
                        // 处理错误
                    }
                })
                
                // 启动串口管理器
                serialIoManager?.start()
            } catch (e: IOException) {
                throw HardwareError.ConnectionError("Failed to open USB serial port: ${e.message}")
            }
        }
    }
    
    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                serialIoManager?.stop()
                usbSerialPort?.close()
                
                serialIoManager = null
                usbSerialPort = null
                usbDevice = null
            } catch (e: IOException) {
                throw HardwareError.ConnectionError("Failed to close USB serial port: ${e.message}")
            }
        }
    }
    
    override suspend fun sendCommand(command: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                usbSerialPort?.write(command, TIMEOUT)
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
                        val count = usbSerialPort?.read(header, TIMEOUT)
                            ?: throw HardwareError.CommunicationError("Port closed")
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
                        val count = usbSerialPort?.read(response, TIMEOUT)
                            ?: throw HardwareError.CommunicationError("Port closed")
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
        throw UnsupportedOperationException("Direct stream access is not supported. Use sendCommand and readResponse instead.")
    }
    
    override fun getOutputStream(): OutputStream {
        throw UnsupportedOperationException("Direct stream access is not supported. Use sendCommand and readResponse instead.")
    }
} 