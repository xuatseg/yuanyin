# Core 模块

Core模块是整个机器人系统的核心，负责协调和管理各个子系统之间的交互。

## 核心组件

### RobotCoreService
主要的核心服务类，负责：
- 系统初始化和生命周期管理
- 子系统协调和通信
- 错误处理和恢复
- 状态管理

主要功能：
1. 系统初始化
   - 存储系统初始化
   - 蓝牙服务初始化
   - 机器人控制初始化
   - AI模型初始化
   - 规则引擎初始化

2. 用户输入处理
   - 本地嵌入处理
   - LLM API处理
   - 混合处理模式

3. 机器人控制
   - 命令验证
   - 命令执行
   - 状态监控

### RobotServiceFactory
服务工厂类，负责：
- 创建和配置各个服务实例
- 管理服务依赖关系
- 提供服务访问接口

### RobotStateManager
状态管理器，负责：
- 维护系统状态
- 状态转换管理
- 状态监听和通知

## 配置模型

### SystemConfig
系统总配置，包含：
- 存储配置 (StorageConfig)
- 蓝牙配置 (BluetoothConfig)
- AI配置 (AIConfig)
- 机器人配置 (RobotConfig)

### 各子系统配置
1. StorageConfig
   - 数据库路径
   - 日志目录

2. BluetoothConfig
   - 服务UUID
   - 特征值UUID

3. AIConfig
   - 嵌入模型路径
   - LLM API密钥

4. RobotConfig
   - 最大速度限制
   - 安全限制参数

## 系统状态

### SystemState
系统状态枚举：
- Initializing: 初始化中
- Ready: 就绪
- Error: 错误状态（包含错误信息）

## 错误处理

### InitializationError
初始化错误类，处理：
- 系统初始化失败
- 组件启动异常
- 配置错误

## 依赖服务

Core模块依赖以下关键服务：
- IChatInterface: 聊天接口
- ILLMService: 大语言模型服务
- IEmbeddingService: 嵌入服务
- IRuleEngine: 规则引擎
- IRobotControl: 机器人控制
- IBluetoothService: 蓝牙服务
- IModeManager: 模式管理
- IStorageManager: 存储管理
