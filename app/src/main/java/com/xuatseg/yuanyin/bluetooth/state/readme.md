# Bluetooth Service State Management

本目录包含蓝牙服务状态管理相关的接口定义。

## 核心接口

### IBluetoothStateManager
蓝牙服务状态管理主接口
- 更新和获取服务状态
- 提供状态观察流
- 管理状态观察者
- 错误处理和状态恢复

### IStateObserver
状态观察者接口
- 状态变化通知
- 错误状态回调
- 恢复尝试回调

### IStateTransitionValidator
状态转换验证器接口
- 验证状态转换有效性
- 获取有效的下一状态
- 判断终态

### IStateRecoveryStrategy
状态恢复策略接口
- 判断错误是否可恢复
- 获取恢复操作列表
- 执行恢复操作

### IStatePersistence
状态持久化接口
- 保存状态
- 加载状态
- 清除状态

### IStateMonitor
状态监控接口
- 记录状态变化
- 提供状态统计
- 重置监控数据

## 数据类型

### RecoveryAction
定义了可能的恢复操作：
- Restart
- Reconnect
- ResetConnection
- ClearCache
- Custom

### StateStatistics
状态统计信息数据类
- 状态变化次数
- 平均状态持续时间
- 错误计数
- 恢复尝试统计

### StateChangeRecord
状态变化记录数据类
- 记录状态变化详情
- 包含时间戳和持续时间
- 记录触发原因
