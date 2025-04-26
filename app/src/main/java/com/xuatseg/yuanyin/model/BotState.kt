package com.xuatseg.yuanyin.model

/**
 * 机器人状态数据模型
 */
data class BotState(
    // 连接状态
    val isConnected: Boolean = false,
    // 电池电量
    val batteryLevel: Int = 100,
    // 当前工作模式
    val workMode: WorkMode = WorkMode.STANDBY,
    // 错误状态
    val error: String? = null
)

/**
 * 机器人工作模式
 */
enum class WorkMode {
    STANDBY,    // 待机模式
    WORKING,    // 工作模式
    CHARGING,   // 充电模式
    ERROR       // 错误模式
}
