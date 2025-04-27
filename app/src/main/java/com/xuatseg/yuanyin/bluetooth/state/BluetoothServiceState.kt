package com.xuatseg.yuanyin.bluetooth.state

import com.xuatseg.yuanyin.bluetooth.ServiceError
import com.xuatseg.yuanyin.bluetooth.ServiceState
import kotlinx.coroutines.flow.Flow

/**
 * 蓝牙服务状态管理接口
 */
interface IBluetoothStateManager {
    /**
     * 更新状态
     * @param newState 新状态
     */
    fun updateState(newState: ServiceState)

    /**
     * 获取当前状态
     * @return 当前状态
     */
    fun getCurrentState(): ServiceState

    /**
     * 观察状态变化
     * @return 状态流
     */
    fun observeState(): Flow<ServiceState>

    /**
     * 添加状态观察者
     * @param observer 观察者
     */
    fun addStateObserver(observer: IStateObserver)

    /**
     * 移除状态观察者
     * @param observer 观察者
     */
    fun removeStateObserver(observer: IStateObserver)

    /**
     * 处理错误状态
     * @param error 错误信息
     */
    fun handleError(error: ServiceError)

    /**
     * 尝试恢复状态
     * @return 是否恢复成功
     */
    fun attemptRecovery(): Boolean
}

/**
 * 状态观察者接口
 */
interface IStateObserver {
    /**
     * 状态变化回调
     * @param oldState 旧状态
     * @param newState 新状态
     */
    fun onStateChanged(oldState: ServiceState, newState: ServiceState)

    /**
     * 错误状态回调
     * @param error 错误信息
     */
    fun onError(error: ServiceError)

    /**
     * 恢复尝试回调
     * @param successful 是否成功
     * @param error 错误信息（如果失败）
     */
    fun onRecoveryAttempt(successful: Boolean, error: ServiceError?)
}

/**
 * 状态转换验证器接口
 */
interface IStateTransitionValidator {
    /**
     * 验证状态转换是否有效
     * @param currentState 当前状态
     * @param newState 新状态
     * @return 是否有效
     */
    fun validateTransition(currentState: ServiceState, newState: ServiceState): Boolean

    /**
     * 获取有效的下一个状态
     * @param currentState 当前状态
     * @return 可能的下一个状态列表
     */
    fun getValidNextStates(currentState: ServiceState): List<ServiceState>

    /**
     * 检查是否是终态
     * @param state 状态
     * @return 是否是终态
     */
    fun isFinalState(state: ServiceState): Boolean
}

/**
 * 状态恢复策略接口
 */
interface IStateRecoveryStrategy {
    /**
     * 确定是否可以恢复
     * @param error 错误信息
     * @return 是否可恢复
     */
    fun isRecoverable(error: ServiceError): Boolean

    /**
     * 获取恢复操作
     * @param error 错误信息
     * @return 恢复操作列表
     */
    fun getRecoveryActions(error: ServiceError): List<RecoveryAction>

    /**
     * 执行恢复操作
     * @param action 恢复操作
     * @return 是否成功
     */
    fun executeRecoveryAction(action: RecoveryAction): Boolean
}

/**
 * 恢复操作
 */
sealed class RecoveryAction {
    object Restart : RecoveryAction()
    object Reconnect : RecoveryAction()
    object ResetConnection : RecoveryAction()
    object ClearCache : RecoveryAction()
    data class Custom(val action: () -> Boolean) : RecoveryAction()
}

/**
 * 状态持久化接口
 */
interface IStatePersistence {
    /**
     * 保存状态
     * @param state 状态
     */
    fun saveState(state: ServiceState)

    /**
     * 加载状态
     * @return 保存的状态
     */
    fun loadState(): ServiceState?

    /**
     * 清除保存的状态
     */
    fun clearState()
}

/**
 * 状态监控接口
 */
interface IStateMonitor {
    /**
     * 记录状态变化
     * @param oldState 旧状态
     * @param newState 新状态
     * @param timestamp 时间戳
     */
    fun recordStateChange(
        oldState: ServiceState,
        newState: ServiceState,
        timestamp: Long
    )

    /**
     * 获取状态统计信息
     * @return 状态统计信息
     */
    fun getStateStatistics(): StateStatistics

    /**
     * 重置监控数据
     */
    fun resetMonitorData()
}

/**
 * 状态统计信息
 */
data class StateStatistics(
    val stateChanges: Map<ServiceState, Int>,
    val averageStateDuration: Map<ServiceState, Long>,
    val errorCount: Int,
    val recoveryAttempts: Int,
    val successfulRecoveries: Int
)

/**
 * 状态变化记录
 */
data class StateChangeRecord(
    val oldState: ServiceState,
    val newState: ServiceState,
    val timestamp: Long,
    val duration: Long,
    val trigger: StateTrigger
)

/**
 * 状态变化触发器
 */
sealed class StateTrigger {
    object User : StateTrigger()
    object System : StateTrigger()
    data class Error(val error: ServiceError) : StateTrigger()
    object Recovery : StateTrigger()
}
