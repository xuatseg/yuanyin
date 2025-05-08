# Looi 机器人项目文件结构

```
looirobot/
├── app/                           # Android 应用主模块
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/looirobot/
│   │   │   │   ├── di/           # 依赖注入模块
│   │   │   │   ├── domain/       # 领域层
│   │   │   │   │   ├── model/    # 领域模型
│   │   │   │   │   ├── repository/# 仓库接口
│   │   │   │   │   └── usecase/  # 用例实现
│   │   │   │   ├── data/         # 数据层
│   │   │   │   │   ├── api/      # API 接口
│   │   │   │   │   ├── db/       # 本地数据库
│   │   │   │   │   ├── repository/# 仓库实现
│   │   │   │   │   └── source/   # 数据源
│   │   │   │   ├── presentation/ # 表现层
│   │   │   │   │   ├── ui/       # UI 组件
│   │   │   │   │   ├── viewmodel/# ViewModel
│   │   │   │   │   └── state/    # UI 状态
│   │   │   │   └── core/         # 核心功能
│   │   │   │       ├── common/   # 通用工具
│   │   │   │       ├── network/  # 网络相关
│   │   │   │       └── utils/    # 工具类
│   │   │   ├── res/              # 资源文件
│   │   │   └── AndroidManifest.xml
│   │   └── test/                 # 测试代码
│   └── build.gradle
│
├── core/                          # 核心功能模块
│   ├── robot_control/            # 机器人控制模块
│   ├── voice_interaction/        # 语音交互模块
│   ├── face_recognition/         # 人脸识别模块
│   ├── projection/               # 投影模块
│   └── llm/                      # 大语言模型模块
│
├── data/                         # 数据模块
│   ├── remote/                   # 远程数据源
│   ├── local/                    # 本地数据源
│   └── repository/               # 数据仓库
│
├── domain/                       # 领域模块
│   ├── model/                    # 领域模型
│   ├── repository/               # 仓库接口
│   └── usecase/                  # 用例
│
├── ui/                           # UI 模块
│   ├── common/                   # 通用 UI 组件
│   ├── theme/                    # 主题相关
│   └── screens/                  # 各个界面
│
├── docs/                         # 文档
│   ├── api/                      # API 文档
│   ├── design/                   # 设计文档
│   └── guides/                   # 使用指南
│
├── buildSrc/                     # 构建配置
│   └── Dependencies.kt
│
├── gradle/                       # Gradle 配置
├── scripts/                      # 构建脚本
├── .gitignore
├── build.gradle
├── settings.gradle
└── README.md
```

## 模块说明

### 1. app 模块
- 主应用模块，包含 Android 应用的主要代码
- 采用 MVVM 架构模式
- 包含依赖注入、领域层、数据层和表现层

### 2. core 模块
- 机器人控制：负责机器人的运动控制、传感器数据采集等
- 语音交互：语音识别、语音合成等功能
- 人脸识别：人脸检测、识别和跟踪
- 投影功能：投影控制和内容管理
- LLM 集成：大语言模型集成和对话管理

### 3. data 模块
- 处理所有数据相关的操作
- 包含远程数据源、本地数据源和数据仓库实现

### 4. domain 模块
- 包含核心业务逻辑
- 定义领域模型和用例
- 提供仓库接口

### 5. ui 模块
- 包含所有 UI 相关的代码
- 提供通用 UI 组件
- 实现各个功能界面

### 6. docs 模块
- 项目文档
- API 文档
- 设计文档
- 使用指南

## 技术栈

- 开发语言：Kotlin
- 架构模式：MVVM + Clean Architecture
- 依赖注入：Hilt
- 异步处理：Coroutines + Flow
- 本地存储：Room
- 网络请求：Retrofit + OkHttp
- UI 框架：Jetpack Compose
- 构建工具：Gradle 