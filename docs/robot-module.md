# 机器人控制模块设计文档

## 1. 模块概述
机器人控制模块负责管理与机器人的蓝牙连接，实现移动控制，并监控连接状态。该模块采用六边形架构设计，确保核心业务逻辑与外部依赖解耦。

## 2. 核心功能
- 蓝牙设备扫描与配对
- 连接管理与自动重连
- 移动指令发送
- 状态监控与反馈
- 错误处理与恢复

## 3. 接口设计

### 3.1 领域接口
```kotlin
// 机器人控制接口
interface RobotController {
    suspend fun connect(macAddress: String): Result<Unit>
    suspend fun disconnect()
    suspend fun sendCommand(command: RobotCommand): Result<Unit>
    fun observeConnectionState(): Flow<ConnectionState>
}

// 机器人命令
sealed class RobotCommand {
    data class Move(
        val direction: Direction,
        val speed: Float,
        val duration: Long? = null
    ) : RobotCommand()
    
    object Stop : RobotCommand()
}

// 连接状态
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val cause: Throwable) : ConnectionState()
}
```

### 3.2 适配器接口
```kotlin
// 蓝牙适配器接口
interface BluetoothAdapter {
    suspend fun scanDevices(): Flow<BluetoothDevice>
    suspend fun connect(device: BluetoothDevice): Result<Unit>
    suspend fun disconnect()
    suspend fun sendData(data: ByteArray): Result<Unit>
    fun observeConnectionState(): Flow<ConnectionState>
}
```

## 4. 实现细节

### 4.1 数据模型
```kotlin
data class BluetoothDevice(
    val macAddress: String,
    val name: String,
    val rssi: Int
)

data class RobotState(
    val isConnected: Boolean,
    val batteryLevel: Int,
    val lastCommand: RobotCommand?,
    val error: Throwable?
)
```

### 4.2 用例实现
```kotlin
class RobotUseCase @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val robotRepository: RobotRepository
) {
    suspend fun connectToRobot(macAddress: String): Result<Unit> {
        return bluetoothAdapter.connect(BluetoothDevice(macAddress))
    }
    
    suspend fun sendMoveCommand(direction: Direction, speed: Float): Result<Unit> {
        val command = RobotCommand.Move(direction, speed)
        return bluetoothAdapter.sendData(command.toByteArray())
    }
    
    fun observeRobotState(): Flow<RobotState> {
        return combine(
            bluetoothAdapter.observeConnectionState(),
            robotRepository.observeBatteryLevel()
        ) { connectionState, batteryLevel ->
            RobotState(
                isConnected = connectionState is ConnectionState.Connected,
                batteryLevel = batteryLevel,
                lastCommand = null,
                error = null
            )
        }
    }
}
```

## 5. 错误处理
- 连接超时处理
- 断开重连机制
- 命令发送失败重试
- 异常状态恢复

## 6. 测试策略
- 单元测试：模拟蓝牙适配器
- 集成测试：实际蓝牙设备
- UI 测试：控制界面交互
- 性能测试：连接稳定性

## 7. 依赖注入
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RobotModule {
    @Provides
    @Singleton
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter {
        return BluetoothAdapterImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideRobotUseCase(
        bluetoothAdapter: BluetoothAdapter,
        robotRepository: RobotRepository
    ): RobotUseCase {
        return RobotUseCase(bluetoothAdapter, robotRepository)
    }
}
```

## 8. 使用示例
```kotlin
class RobotViewModel @Inject constructor(
    private val robotUseCase: RobotUseCase
) : ViewModel() {
    private val _robotState = MutableStateFlow<RobotState>(RobotState())
    val robotState: StateFlow<RobotState> = _robotState.asStateFlow()
    
    init {
        viewModelScope.launch {
            robotUseCase.observeRobotState()
                .collect { state ->
                    _robotState.value = state
                }
        }
    }
    
    fun connectToRobot(macAddress: String) {
        viewModelScope.launch {
            robotUseCase.connectToRobot(macAddress)
        }
    }
    
    fun moveRobot(direction: Direction, speed: Float) {
        viewModelScope.launch {
            robotUseCase.sendMoveCommand(direction, speed)
        }
    }
}
``` 