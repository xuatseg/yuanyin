package com.xuatseg.yuanyin.engine

import kotlinx.coroutines.flow.Flow

/**
 * 规则引擎接口
 */
interface IRuleEngine {
    /**
     * 评估输入并决定处理方式
     * @param input 输入内容
     * @return 处理决策
     */
    suspend fun evaluate(input: RuleInput): ProcessingDecision

    /**
     * 添加规则
     * @param rule 规则
     */
    fun addRule(rule: IRule)

    /**
     * 移除规则
     * @param ruleId 规则ID
     */
    fun removeRule(ruleId: String)

    /**
     * 更新规则优先级
     * @param ruleId 规则ID
     * @param priority 新优先级
     */
    fun updateRulePriority(ruleId: String, priority: Int)

    /**
     * 获取所有规则
     * @return 规则列表
     */
    fun getRules(): List<IRule>
}

/**
 * 规则接口
 */
interface IRule {
    /**
     * 规则ID
     */
    val id: String

    /**
     * 规则优先级
     */
    val priority: Int

    /**
     * 规则描述
     */
    val description: String

    /**
     * 评估输入
     * @param input 输入内容
     * @return 规则匹配结果
     */
    suspend fun evaluate(input: RuleInput): RuleResult
}

/**
 * 规则输入
 */
data class RuleInput(
    val text: String,
    val type: InputType,
    val metadata: Map<String, Any>? = null,
    val context: ProcessingContext? = null
)

/**
 * 输入类型
 */
enum class InputType {
    TEXT,
    VOICE,
    COMMAND,
    SYSTEM
}

/**
 * 处理上下文
 */
data class ProcessingContext(
    val sessionId: String,
    val userContext: Map<String, Any>,
    val systemContext: Map<String, Any>,
    val previousInteractions: List<Interaction>
)

/**
 * 交互记录
 */
data class Interaction(
    val input: String,
    val response: String,
    val processor: ProcessorType,
    val timestamp: Long
)

/**
 * 处理器类型
 */
enum class ProcessorType {
    LOCAL_EMBEDDING,
    LLM_API,
    HYBRID
}

/**
 * 规则结果
 */
data class RuleResult(
    val matches: Boolean,
    val confidence: Float,
    val suggestedProcessor: ProcessorType,
    val metadata: Map<String, Any>? = null
)

/**
 * 处理决策
 */
data class ProcessingDecision(
    val processor: ProcessorType,
    val confidence: Float,
    val matchedRules: List<IRule>,
    val parameters: ProcessingParameters
)

/**
 * 处理参数
 */
data class ProcessingParameters(
    val useCache: Boolean = true,
    val priority: ProcessingPriority = ProcessingPriority.NORMAL,
    val timeout: Long? = null,
    val retryStrategy: RetryStrategy? = null,
    val customParameters: Map<String, Any>? = null
)

/**
 * 处理优先级
 */
enum class ProcessingPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}

/**
 * 重试策略
 */
data class RetryStrategy(
    val maxAttempts: Int,
    val initialDelay: Long,
    val maxDelay: Long,
    val backoffMultiplier: Float
)

/**
 * 规则管理器接口
 */
interface IRuleManager {
    /**
     * 加载规则
     * @param source 规则源
     */
    suspend fun loadRules(source: RuleSource)

    /**
     * 保存规则
     * @param rules 规则列表
     */
    suspend fun saveRules(rules: List<IRule>)

    /**
     * 验证规则
     * @param rule 规则
     * @return 验证结果
     */
    fun validateRule(rule: IRule): ValidationResult
}

/**
 * 规则源
 */
sealed class RuleSource {
    data class File(val path: String) : RuleSource()
    data class Remote(val url: String) : RuleSource()
    data class Database(val config: Map<String, Any>) : RuleSource()
}

/**
 * 验证结果
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)

/**
 * 规则监控接口
 */
interface IRuleMonitor {
    /**
     * 记录规则执行
     * @param rule 规则
     * @param input 输入
     * @param result 结果
     */
    fun recordRuleExecution(
        rule: IRule,
        input: RuleInput,
        result: RuleResult
    )

    /**
     * 获取规则统计
     * @param ruleId 规则ID
     */
    fun getRuleStatistics(ruleId: String): RuleStatistics

    /**
     * 观察规则执行
     */
    fun observeRuleExecutions(): Flow<RuleExecution>
}

/**
 * 规则统计
 */
data class RuleStatistics(
    val totalExecutions: Int,
    val matchRate: Float,
    val averageConfidence: Float,
    val averageExecutionTime: Long,
    val processorDistribution: Map<ProcessorType, Int>
)

/**
 * 规则执行记录
 */
data class RuleExecution(
    val ruleId: String,
    val input: RuleInput,
    val result: RuleResult,
    val executionTime: Long,
    val timestamp: Long
)
