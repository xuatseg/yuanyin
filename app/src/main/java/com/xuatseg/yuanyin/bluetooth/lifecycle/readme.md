# Bluetooth Service Lifecycle

本目录包含蓝牙服务生命周期管理相关的接口定义。

## 核心接口

### IBluetoothServiceLifecycle
蓝牙服务生命周期主接口，负责管理服务的整个生命周期。
- 处理生命周期事件
- 获取和观察生命周期状态
- 管理生命周期观察者

### ILifecycleStateMachine
生命周期状态机接口，负责状态转换的核心逻辑。
- 处理状态转换
- 验证状态转换
- 获取可能的下一个状态

### ILifecycleEventHandler
生命周期事件处理器接口，负责具体事件的处理。
- 处理生命周期事件
- 提供事件处理结果流

### ILifecycleMonitor
生命周期监控接口，负责记录和追踪状态变化。
- 记录状态转换
- 记录错误
- 提供状态和错误历史

### ILifecycleConfig
生命周期配置接口，提供配置参数。
- 状态转换超时时间
- 重试策略配置
- 自动恢复配置

## 数据类型

### LifecycleEvent
定义了所有可能的生命周期事件：
- Create, Start, Resume
- Pause, Stop, Destroy
- Error

### LifecycleState
定义了所有可能的生命周期状态：
- Created, Started, Resumed
- Paused, Stopped, Destroyed
- Error
