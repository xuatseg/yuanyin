# Gradle 构建指南

## IDE 通用性配置

项目已经配置为支持多种 IDE，包括：
- IntelliJ IDEA
- Android Studio
- Eclipse
- VS Code
- NetBeans

主要的 IDE 配置文件都已经在 `.gitignore` 中配置忽略。

## Gradle Wrapper 配置

项目使用 Gradle Wrapper 进行构建，确保团队成员使用相同的 Gradle 版本。如果遇到 Gradle Wrapper 相关问题，可以：

1. 确保 `gradle/wrapper/gradle-wrapper.jar` 文件存在
2. 确保 `gradlew` 脚本有执行权限（`chmod +x gradlew`）
3. 检查 `gradle-wrapper.properties` 中的 Gradle 版本配置

## 常用构建命令

### 1. 基础构建命令
```bash
# 清理构建目录
./gradlew clean

# 构建整个项目
./gradlew build

# 仅编译项目，不执行测试
./gradlew assemble

# 查看所有可用的 Gradle 任务
./gradlew tasks
```

### 2. Android 特定命令
```bash
# 构建所有变体的 Debug APK
./gradlew assembleDebug

# 构建所有变体的 Release APK
./gradlew assembleRelease

# 安装 Debug 版本到连接的设备
./gradlew installDebug

# 运行单元测试
./gradlew test

# 运行 Android 设备测试
./gradlew connectedAndroidTest
```

### 3. 项目依赖相关
```bash
# 查看项目依赖
./gradlew dependencies

# 更新依赖
./gradlew build --refresh-dependencies
```

### 4. 开发调试相关
```bash
# 生成源代码文档
./gradlew dokka

# 检查依赖更新
./gradlew dependencyUpdates

# 运行 lint 检查
./gradlew lint
```

### 5. 多模块项目命令
```bash
# 构建特定模块
./gradlew :app:build

# 清理特定模块
./gradlew :app:clean

# 运行特定模块的测试
./gradlew :app:test
```

## 构建优化提示

1. 所有命令都应在项目根目录下执行
2. 第一次运行会下载 Gradle 和依赖，可能会比较慢
3. 可以添加 `--stacktrace` 参数来查看详细错误信息
4. 使用 `--parallel` 参数可以启用并行构建
5. 使用 `--offline` 参数在离线模式下构建

完整的构建命令示例：
```bash
./gradlew clean build --parallel --stacktrace
```

## 查看更多任务

要查看项目中所有可用的任务及其描述，运行：
```bash
./gradlew tasks --all
``` 