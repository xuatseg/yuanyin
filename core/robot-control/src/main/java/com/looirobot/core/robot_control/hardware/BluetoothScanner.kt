package com.looirobot.core.robot_control.hardware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothScanner @Inject constructor(
    private val context: Context
) {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        private const val SCAN_PERIOD = 10000L // 10秒
        private const val DEVICE_NAME_PREFIX = "LooiRobot"
        
        // ScanCallback 错误码
        private const val SCAN_FAILED_ALREADY_STARTED = 1
        private const val SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2
        private const val SCAN_FAILED_FEATURE_UNSUPPORTED = 4
        private const val SCAN_FAILED_INTERNAL_ERROR = 3
    }
    
    fun startScan(): Flow<BluetoothDevice> = callbackFlow {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            throw HardwareError.ConnectionError("Bluetooth not supported")
        }
        
        if (!bluetoothAdapter!!.isEnabled) {
            throw HardwareError.ConnectionError("Bluetooth not enabled")
        }
        
        // 获取BLE扫描器
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                if (device.name?.startsWith(DEVICE_NAME_PREFIX) == true) {
                    trySend(device)
                }
            }
            
            override fun onScanFailed(errorCode: Int) {
                when (errorCode) {
                    SCAN_FAILED_ALREADY_STARTED -> {
                        // 忽略，因为可能已经在扫描
                    }
                    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> {
                        throw HardwareError.ConnectionError("Failed to register scan callback")
                    }
                    SCAN_FAILED_FEATURE_UNSUPPORTED -> {
                        throw HardwareError.ConnectionError("BLE scanning not supported")
                    }
                    SCAN_FAILED_INTERNAL_ERROR -> {
                        throw HardwareError.ConnectionError("Internal error during scanning")
                    }
                }
            }
        }
        
        // 开始扫描
        bluetoothLeScanner?.startScan(scanCallback)
        
        // 设置超时
        handler.postDelayed({
            bluetoothLeScanner?.stopScan(scanCallback)
        }, SCAN_PERIOD)
        
        awaitClose {
            bluetoothLeScanner?.stopScan(scanCallback)
        }
    }
    
    fun getBondedDevices(): List<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices?.filter { 
            it.name?.startsWith(DEVICE_NAME_PREFIX) == true 
        } ?: emptyList()
    }
    
    fun isDeviceBonded(device: BluetoothDevice): Boolean {
        return bluetoothAdapter?.bondedDevices?.contains(device) == true
    }
} 