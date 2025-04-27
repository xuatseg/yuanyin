package com.xuatseg.yuanyin.bluetooth.lifecycle

import com.xuatseg.yuanyin.bluetooth.ServiceError
import com.xuatseg.yuanyin.bluetooth.ServiceState
import kotlinx.coroutines.flow.Flow

/**
 * 蓝牙服务生命周期管理接口
 */
interface IBluetoothServiceLifecycle {
    /**
     * 处理生命周期事件
     * @param event 生命周期事件
     */
    fun handleLifecycleEvent(event: LifecycleEvent)

    /**
     * 获取当前生命周期状态
     * @return 当前状态
     */
    fun getCurrentState(): LifecycleState

    /**
     * 观察生命周期状态变化
     * @return 状态流
     */
    fun observeLifecycleState(): Flow<LifecycleState>

    /**
     * 添加生命周期观察者
     * @param observer 观察者
     */
    fun addObserver(observer: ILifecycleObserver)

    /**
     * 移除生命周期观察者
     * @param observer 观察者
     */
    fun removeObserver(observer: ILifecycleObserver)
}

/**
 * 生命周期事件
 */
sealed class LifecycleEvent {
    object Create : LifecycleEvent()
    object Start : LifecycleEvent()
    object Resume : LifecycleEvent()
    object Pause : LifecycleEvent()
    object Stop : LifecycleEvent()
    object Destroy : LifecycleEvent()
    data class Error(val error: ServiceError) : LifecycleEvent()
}

/**
 * 生命周期状态
 */
sealed class LifecycleState {
    object Created : LifecycleState()
    object Started : LifecycleState()
    object Resumed : LifecycleState()
    object Paused : LifecycleState()
    object Stopped : LifecycleState()
    object Destroyed : LifecycleState()
    data class Error(val error: ServiceError) : LifecycleState()
}

/**
 * 生命周期观察者接口
 */
interface ILifecycleObserver {
    fun onCreate()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
    fun onError(error: ServiceError)
}

/**
 * 生命周期状态机接口
 */
interface ILifecycleStateMachine {
    /**
     * 处理状态转换
     * @param event 触发事件
     * @return 新状态
     */
    fun processStateTransition(event: LifecycleEvent): LifecycleState

    /**
     * 验证状态转换是否有效
     * @param currentState 当前状态
     * @param event 事件
     * @return 是否有效
     */
    fun validateStateTransition(currentState: LifecycleState, event: LifecycleEvent): Boolean

    /**
     * 获取可能的下一个状态
     * @param currentState 当前状态
     * @return 可能的下一个状态列表
     */
    fun getPossibleNextStates(currentState: LifecycleState): List<LifecycleState>
}

/**
 * 生命周期事件处理器接口
 */
interface ILifecycleEventHandler {
    /**
     * 处理生命周期事件
     * @param event 事件
     * @param currentState 当前状态
     */
    fun handleEvent(event: LifecycleEvent, currentState: LifecycleState)

    /**
     * 获取事件处理结果
     * @return 处理结果流
     */
    fun getEventResults(): Flow<LifecycleEventResult>
}

/**
 * 生命周期事件结果
 */
sealed class LifecycleEventResult {
    data class Success(
        val event: LifecycleEvent,
        val previousState: LifecycleState,
        val newState: LifecycleState
    ) : LifecycleEventResult()

    data class Failure(
        val event: LifecycleEvent,
        val currentState: LifecycleState,
        val error: ServiceError
    ) : LifecycleEventResult()
}

/**
 * 生命周期配置接口
 */
interface ILifecycleConfig {
    /**
     * 获取状态转换超时时间
     */
    fun getStateTransitionTimeout(): Long

    /**
     * 获取最大重试次数
     */
    fun getMaxRetryAttempts(): Int

    /**
     * 获取重试间隔
     */
    fun getRetryInterval(): Long

    /**
     * 是否启用自动恢复
     */
    fun isAutoRecoveryEnabled(): Boolean
}

/**
 * 生命周期监控接口
 */
interface ILifecycleMonitor {
    /**
     * 记录状态变化
     * @param previousState 前一个状态
     * @param newState 新状态
     * @param timestamp 时间戳
     */
    fun recordStateTransition(
        previousState: LifecycleState,
        newState: LifecycleState,
        timestamp: Long
    )

    /**
     * 记录错误
     * @param error 错误信息
     * @param state 发生错误时的状态
     * @param timestamp 时间戳
     */
    fun recordError(
        error: ServiceError,
        state: LifecycleState,
        timestamp: Long
    )

    /**
     * 获取状态历史
     * @return 状态历史记录
     */
    fun getStateHistory(): List<StateTransitionRecord>

    /**
     * 获取错误历史
     * @return 错误历史记录
     */
    fun getErrorHistory(): List<ErrorRecord>
}

/**
 * 状态转换记录
 */
data class StateTransitionRecord(
    val previousState: LifecycleState,
    val newState: LifecycleState,
    val timestamp: Long,
    val duration: Long
)

/**
 * 错误记录
 */
data class ErrorRecord(
    val error: ServiceError,
    val state: LifecycleState,
    val timestamp: Long,
    val recovered: Boolean,
    val recoveryDuration: Long?
)
