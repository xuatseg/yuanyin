# Model 模块

该模块包含了系统核心数据模型的定义，为整个系统提供统一的数据结构和类型定义。

## 核心数据模型

### BotState
机器人状态数据模型：
```kotlin
data class BotState(
    val isConnected: Boolean = false,  // 连接状态
    val batteryLevel: Int = 100,      // 电池电量(0-100)
    val workMode: WorkMode = WorkMode.STANDBY, // 工作模式
    val error: String? = null         // 错误信息
)
```

### WorkMode
机器人工作模式枚举：
```kotlin
enum class WorkMode {
    STANDBY,    // 待机模式
    WORKING,    // 工作模式
    CHARGING,   // 充电模式
    ERROR       // 错误模式
}
```

## 消息模型

### ChatMessage
聊天消息模型：
```kotlin
data class ChatMessage(
    val id: String,              // 消息ID
    val content: String,        // 消息内容
    val sender: MessageSender,  // 发送者
    val timestamp: Long,        // 时间戳
    val status: MessageStatus   // 消息状态
)
```

### MessageSender
消息发送者枚举：
```kotlin
enum class MessageSender {
    USER,    // 用户
    BOT,     // 机器人
    SYSTEM   // 系统
}
```

### MessageStatus
消息状态枚举：
```kotlin
enum class MessageStatus {
    SENDING,    // 发送中
    SENT,       // 已发送
    DELIVERED,  // 已送达
    READ,       // 已读
    FAILED      // 发送失败
}
```

## 配置模型

### SystemConfig
系统配置模型：
```kotlin
data class SystemConfig(
    val bluetooth: BluetoothConfig,  // 蓝牙配置
    val storage: StorageConfig,     // 存储配置
    val ai: AIConfig,               // AI配置
    val robot: RobotConfig          // 机器人配置
)
```

### BluetoothConfig
蓝牙配置：
```kotlin
data class BluetoothConfig(
    val serviceUUID: String,       // 服务UUID
    val characteristicUUID: String // 特征UUID
)
```

### StorageConfig
存储配置：
```kotlin
data class StorageConfig(
    val databasePath: String,  // 数据库路径
    val logDirectory: String   // 日志目录
)
```

## 请求/响应模型

### LLMRequest
LLM请求模型：
```kotlin
data class LLMRequest(
    val messages: List<LLMMessage>,  // 消息历史
    val parameters: LLMParameters,  // 生成参数
    val options: LLMOptions? = null  // 请求选项
)
```

### LLMResponse
LLM响应模型：
```kotlin
sealed class LLMResponse {
    data class Content(val text: String) : LLMResponse()
    data class Error(val error: String) : LLMResponse()
    object Done : LLMResponse()
}
```

## 枚举类型

### ProcessingMode
处理模式枚举：
```kotlin
enum class ProcessingMode {
    LOCAL,   // 本地处理
    LLM,     // LLM API处理
    HYBRID,  // 混合处理
    AUTO     // 自动选择
}
```

### DeviceType
设备类型枚举：
```kotlin
enum class DeviceType {
    CPU,  // CPU计算
    GPU,  // GPU加速
    NPU   // 神经网络处理器
}
```

## 最佳实践

1. **模型设计**:
   - 保持模型不可变性
   - 使用默认值简化初始化
   - 提供清晰的文档注释

2. **类型安全**:
   - 使用枚举代替字符串常量
   - 使用密封类处理不同类型响应
   - 定义合理的值范围

3. **扩展性**:
   - 使用可空字段处理可选数据
   - 添加扩展字段支持未来需求
   - 保持向后兼容性

4. **性能考虑**:
   - 使用基本数据类型
   - 避免深层嵌套
   - 考虑序列化性能
