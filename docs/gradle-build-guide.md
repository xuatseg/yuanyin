# Gradle 构建指南

## IDE 通用性配置

项目已经配置为支持多种 IDE，包括：
- IntelliJ IDEA
- Android Studio
- Eclipse
- VS Code
- NetBeans

主要的 IDE 配置文件都已经在 `.gitignore` 中配置忽略。

## Gradle Wrapper 使用说明

Gradle Wrapper 是一个脚本，它会自动下载指定版本的 Gradle。这确保了所有开发者使用相同版本的 Gradle 进行构建。

### Windows 系统
在 Windows 系统中，使用 `gradlew.bat` 文件：
```batch
# 清理项目
gradlew.bat clean

# 构建项目
gradlew.bat build

# 查看所有可用任务
gradlew.bat tasks
```

### Linux/macOS 系统
在 Linux/macOS 系统中，使用 `gradlew` 文件：
```bash
# 首次使用前添加执行权限
chmod +x gradlew

# 清理项目
./gradlew clean

# 构建项目
./gradlew build

# 查看所有可用任务
./gradlew tasks
```

## Gradle Wrapper 配置

项目使用 Gradle Wrapper 进行构建，确保团队成员使用相同的 Gradle 版本。如果遇到 Gradle Wrapper 相关问题，可以：

1. 确保 `gradle/wrapper/gradle-wrapper.jar` 文件存在
2. 确保 `gradlew` 脚本有执行权限（`chmod +x gradlew`）
3. 检查 `gradle-wrapper.properties` 中的 Gradle 版本配置


## 常用构建命令

**以下命令在 Windows 系统中使用 `gradlew.bat`，在 Linux/macOS 系统中使用 `./gradlew`。**
**后面的命令用 ${gradle} 替代 ./gradlew.bat 或 ./gradlew**
```bash
./gradlew clean # linux/macos
或
gradlew.bat clean # windows
```
### 1. 基础构建命令
```bash
# 清理构建目录
${gradle} clean

# 构建整个项目
${gradle} build

# 仅编译项目，不执行测试
${gradle} assemble

# 查看所有可用的 Gradle 任务
${gradle} tasks
```

### 2. Android 特定命令
```bash
# 构建 Debug APK
${gradle} assembleDebug

# 构建 Release APK
${gradle} assembleRelease

# 安装 Debug 版本到设备
${gradle} installDebug

# 运行单元测试
${gradle} test

# 运行 Android 设备测试
${gradle} connectedAndroidTest
```

### 3. 项目依赖相关
```bash
# 查看项目依赖
${gradle} dependencies

# 更新依赖
${gradle} build --refresh-dependencies
```

### 4. 开发调试相关
```bash
# 生成源代码文档
${gradle} dokka

# 检查依赖更新
${gradle} dependencyUpdates

# 运行 lint 检查
${gradle} lint
```

### 5. 多模块项目命令
```bash
# 构建特定模块
${gradle} :app:build

# 清理特定模块
${gradle} :app:clean

# 运行特定模块的测试
${gradle} :app:test
```

## 常见问题解决

### 1. Gradle Wrapper 问题

#### Windows 系统
如果遇到 "gradle-wrapper.jar not found" 错误：
1. 检查 `gradle/wrapper/gradle-wrapper.jar` 文件是否存在
2. 如果不存在，可以运行：
```batch
gradle wrapper # gradle非${gradle}
```

#### Linux/macOS 系统
如果遇到权限问题：
```bash
chmod +x gradlew
```

### 2. 构建失败问题

对于任何系统：
1. 清理构建缓存：
   - Windows: `gradlew.bat clean`
   - Linux/macOS: `./gradlew clean`

2. 使用 `--stacktrace` 参数查看详细错误：
   - Windows: `gradlew.bat build --stacktrace`
   - Linux/macOS: `./gradlew build --stacktrace`

3. 刷新依赖：
   - Windows: `gradlew.bat build --refresh-dependencies`
   - Linux/macOS: `./gradlew build --refresh-dependencies`

## 构建优化提示

1. 使用并行构建加快速度：
   - Windows: `gradlew.bat build --parallel`
   - Linux/macOS: `./gradlew build --parallel`

2. 离线模式构建（如果已经下载过依赖）：
   - Windows: `gradlew.bat build --offline`
   - Linux/macOS: `./gradlew build --offline`

3. 增加 Gradle 守护进程的内存：
   在 `gradle.properties` 文件中添加：
   ```properties
   org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
   ```

## 查看更多任务

要查看项目中所有可用的任务及其描述，运行：
```bash
${gradle} tasks --all
``` 