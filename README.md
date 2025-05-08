# Looi Robot App

## 项目概述
这是一个基于 Android 平台的智能机器人控制应用，采用六边形架构（Hexagonal Architecture）设计，实现了机器人控制、语音交互、人脸跟踪、投影展示等核心功能。

## 技术栈
- 开发语言：Kotlin
- UI 框架：Jetpack Compose
- 架构模式：六边形架构（Hexagonal Architecture）
- 依赖注入：Hilt
- 异步处理：Kotlin Coroutines + Flow
- 本地存储：Room
- 网络通信：Retrofit + gRPC
- 蓝牙通信：Android BluetoothGatt API
- 语音识别：SpeechRecognizer + 云端 SDK
- 人脸识别：ML Kit
- 投影功能：MediaProjection API
- 推送服务：Firebase Cloud Messaging

## 项目结构
```
app/
├── build.gradle.kts                # 应用级构建配置
├── src/
│   ├── main/
│   │   ├── java/com/looirobot/
│   │   │   ├── app/               # 应用入口
│   │   │   │   ├── LooiApp.kt    # 应用类
│   │   │   │   └── di/           # 依赖注入
│   │   │   ├── ui/               # UI 层
│   │   │   │   ├── screens/      # 各个功能页面
│   │   │   │   ├── components/   # 可复用组件
│   │   │   │   └── theme/        # 主题相关
│   │   │   ├── domain/           # 领域层
│   │   │   │   ├── model/        # 领域模型
│   │   │   │   ├── repository/   # 仓储接口
│   │   │   │   └── usecase/      # 用例实现
│   │   │   ├── data/             # 数据层
│   │   │   │   ├── repository/   # 仓储实现
│   │   │   │   ├── local/        # 本地数据源
│   │   │   │   └── remote/       # 远程数据源
│   │   │   └── core/             # 核心功能模块
│   │   │       ├── robot/        # 机器人控制
│   │   │       ├── voice/        # 语音交互
│   │   │       ├── face/         # 人脸识别
│   │   │       ├── projection/   # 投影功能
│   │   │       └── notification/ # 消息推送
│   │   └── res/                  # 资源文件
│   └── test/                     # 测试代码
└── proguard-rules.pro            # 混淆规则

buildSrc/                         # 构建脚本
├── build.gradle.kts
└── src/main/kotlin/
    └── Dependencies.kt           # 依赖版本管理
```

## 核心模块说明

### 1. 机器人控制模块 (robot)
- 负责蓝牙连接管理
- 实现机器人移动控制
- 处理连接状态监控

### 2. 语音交互模块 (voice)
- 语音唤醒功能
- 语音识别与合成
- 指令解析与执行

### 3. 人脸识别模块 (face)
- 摄像头管理
- 人脸检测与跟踪
- 位置信息处理

### 4. 投影功能模块 (projection)
- 投影设备管理
- 内容播放控制
- 状态同步

### 5. 消息推送模块 (notification)
- FCM 消息处理
- 本地通知管理
- 消息展示控制

## 开发规范

### 命名规范
- 类名：大驼峰命名法（PascalCase）
- 函数/变量：小驼峰命名法（camelCase）
- 常量：全大写下划线分隔（UPPER_SNAKE_CASE）
- 资源文件：小写下划线分隔（lower_snake_case）

### 代码规范
- 遵循 Kotlin 官方编码规范
- 使用 Ktlint 进行代码格式化
- 关键函数必须添加 KDoc 文档注释

### 版本控制
- 主分支：main
- 开发分支：develop
- 功能分支：feature/*
- 发布分支：release/*
- 修复分支：hotfix/*

## 构建与运行

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17 或更高版本
- Android SDK 34 或更高版本

### 构建步骤
1. 克隆项目
```bash
git clone https://github.com/your-org/looi-robot.git
```

2. 同步依赖
```bash
./gradlew build
```

3. 运行应用
```bash
./gradlew installDebug
```

## 测试策略
- 单元测试：JUnit5 + MockK
- UI 测试：Compose UI Testing
- 集成测试：Espresso
- 性能测试：Android Profiler

## 发布流程
1. 版本号更新
2. 更新日志编写
3. 代码审查
4. 自动化测试
5. 打包发布
6. 应用商店发布

## 贡献指南
1. Fork 项目
2. 创建功能分支
3. 提交变更
4. 发起 Pull Request

## 许可证
MIT License 