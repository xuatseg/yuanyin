# Bluetooth Module Architecture

本目录包含蓝牙模块的核心接口和实现。

## 核心接口

### IBluetooth
蓝牙基础接口
- 定义基本的蓝牙操作
- 设备连接管理
- 数据传输功能

### IBluetoothProtocol
蓝牙通信协议接口
- 定义数据包格式
- 协议解析和封装
- 通信规则定义

### IBluetoothService
蓝牙服务接口
- 高级服务抽象
- 生命周期管理
- 状态管理集成

## 主要子模块

### [Lifecycle](lifecycle/readme.md)
生命周期管理模块
- 服务生命周期控制
- 状态机实现
- 事件处理

### [State](state/readme.md)
状态管理模块
- 服务状态管理
- 状态转换控制
- 状态恢复策略

## 实现类

### BluetoothServiceImpl
蓝牙服务实现类
- 实现IBluetoothService接口
- 集成生命周期管理
- 集成状态管理
- 提供完整的蓝牙服务功能
