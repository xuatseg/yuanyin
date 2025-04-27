# AI机器人控制系统

## 项目概述
本项目是一个基于Android的AI机器人控制系统，集成了本地嵌入模型和LLM API，实现了智能对话、机器人控制、状态监控等功能。

## 系统架构

### 整体架构
```
+----------------+     +----------------+     +----------------+
|     UI 层      |     |   业务逻辑层   |     |    数据层     |
|  (Jetpack      |     |   (Core       |     |  (Storage     |
|   Compose)     |<--->|    Service)   |<--->|   System)     |
+----------------+     +----------------+     +----------------+
                            ^      ^
                            |      |
                    +-------+      +-------+
                    |                      |
            +----------------+    +----------------+
            |   模型层        |    |   底盘控制层    |
            | (LLM/Embedding)|    |   (Robot      |
            |   Services)    |    |   Control)    |
            +----------------+    +----------------+
```

### 分层说明
1. **UI层** (app/src/main/java/com/xuatseg/yuanyin/ui)
   - 使用Jetpack Compose构建
   - 实现用户界面和交互
   - 状态展示和控制界面

2. **业务逻辑层** (app/src/main/java/com/xuatseg/yuanyin/core)
   - 核心服务协调
   - 状态管理
   - 业务流程控制

3. **模型层** (app/src/main/java/com/xuatseg/yuanyin/{llm,embedding})
   - LLM API服务
   - 本地嵌入模型
   - 规则引擎

4. **底盘控制层** (app/src/main/java/com/xuatseg/yuanyin/robot)
   - 机器人控制
   - 传感器数据处理
   - 状态监控

5. **数据层** (app/src/main/java/com/xuatseg/yuanyin/storage)
   - 数据库存储
   - 文件系统
   - 数据持久化

## 目录结构
```
app/src/main/java/com/xuatseg/yuanyin/
├── bluetooth/          # 蓝牙通信模块
│   ├── IBluetooth.kt
│   └── IBluetoothProtocol.kt
├── chat/              # 对话界面模块
│   └── IChatInterface.kt
├── core/              # 核心业务逻辑
│   ├── RobotCoreService.kt
│   ├── RobotServiceFactory.kt
│   └── RobotStateManager.kt
├── embedding/         # 嵌入模型服务
│   └── IEmbeddingService.kt
├── engine/           # 规则引擎
│   └── IRuleEngine.kt
├── llm/              # LLM服务
│   └── ILLMService.kt
├── mode/             # 模式管理
│   └── IModeManager.kt
├── robot/            # 机器人控制
│   ├── IRobotControl.kt
│   ├── IRobotDiagnostics.kt
│   └── IRobotSensor.kt
├── storage/          # 存储系统
│   ├── database/
│   │   └── IDatabase.kt
│   └── file/
│       └── IFileStorage.kt
└── ui/               # 用户界面
    ├── control/
    │   └── RobotControlComponents.kt
    ├── mode/
    │   └── ModeSwitchComponents.kt
    └── screens/
        └── MainScreen.kt
```

## 功能模块说明

### 1. 对话系统
- 支持文本/语音输入
- 本地嵌入模型处理
- LLM API集成
- 对话历史管理

### 2. 机器人控制
- 基础运动控制
- 状态监控
- 传感器数据处理
- 诊断系统

### 3. 模式管理
- 本地/LLM模式切换
- 运行状态管理
- 配置管理

### 4. 存储系统
- 传感器数据存储
- 对话历史存储
- 日志管理
- 文件系统管理

## 使用说明

### 1. 系统初始化
```kotlin
// 创建服务实例
val serviceBuilder = RobotServiceBuilder(context)
    .setSystemConfig(config)
    .setChatInterface(chatInterface)
    .setLLMService(llmService)
    .setEmbeddingService(embeddingService)
    .setRobotControl(robotControl)
    .build()

// 初始化系统
serviceBuilder.initialize()
```

### 2. 状态管理
```kotlin
// 观察系统状态
stateManager.observeState().collect { state ->
    // 处理状态变化
}

// 处理系统事件
stateManager.handleEvent(SystemEvent.Initialize)
```

### 3. 错误处理
```kotlin
try {
    // 执行操作
    handleUserInput(input)
} catch (e: Exception) {
    // 处理错误
    stateManager.handleEvent(SystemEvent.Error(e.toSystemError()))
}
```

## 开发说明

### 1. 环境要求
- Android Studio Arctic Fox或更高版本
- Kotlin 1.5.0或更高版本
- Android SDK 21+
- Jetpack Compose

### 2. 依赖项
- Jetpack Compose UI
- Kotlin Coroutines
- Android Architecture Components
- Local Embedding Model
- LLM API Client

### 3. 构建步骤
1. 克隆仓库
2. 在Android Studio中打开项目
3. 同步Gradle文件
4. 构建并运行项目

## 注意事项
1. 确保正确配置LLM API密钥
2. 检查蓝牙权限设置
3. 注意存储空间管理
4. 关注电池使用情况

## 贡献指南
1. Fork本仓库
2. 创建特性分支
3. 提交更改
4. 发起Pull Request

## 许可证
[添加许可证信息]

## 联系方式
[添加联系方式]
