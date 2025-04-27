package com.xuatseg.yuanyin.chat

import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * 聊天界面接口
 */
interface IChatInterface {
    /**
     * 发送文本消息
     * @param message 消息内容
     * @return 消息ID
     */
    suspend fun sendTextMessage(message: String): String

    /**
     * 发送语音消息
     * @param audioFile 语音文件
     * @return 消息ID
     */
    suspend fun sendVoiceMessage(audioFile: File): String

    /**
     * 获取消息历史
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 消息列表
     */
    suspend fun getMessageHistory(limit: Int, offset: Int): List<ChatMessage>

    /**
     * 观察新消息
     * @return 消息流
     */
    fun observeNewMessages(): Flow<ChatMessage>

    /**
     * 观察消息状态变化
     * @return 消息状态流
     */
    fun observeMessageStatus(): Flow<MessageStatus>

    /**
     * 重试发送消息
     * @param messageId 消息ID
     */
    suspend fun retryMessage(messageId: String)

    /**
     * 删除消息
     * @param messageId 消息ID
     */
    suspend fun deleteMessage(messageId: String)
}

/**
 * 聊天消息
 */
data class ChatMessage(
    val id: String,
    val content: MessageContent,
    val sender: MessageSender,
    val timestamp: Long,
    val status: MessageStatus,
    val metadata: Map<String, Any>? = null
)

/**
 * 消息内容
 */
sealed class MessageContent {
    data class Text(val text: String) : MessageContent()
    data class Voice(
        val audioFile: File,
        val duration: Long,
        val transcript: String? = null
    ) : MessageContent()
    data class Error(val error: String) : MessageContent()
}

/**
 * 消息发送者
 */
sealed class MessageSender {
    object User : MessageSender()
    object Bot : MessageSender()
    data class System(val type: String) : MessageSender()
}

/**
 * 消息状态
 */
sealed class MessageStatus {
    object Sending : MessageStatus()
    object Sent : MessageStatus()
    object Delivered : MessageStatus()
    object Read : MessageStatus()
    data class Failed(val error: String) : MessageStatus()
}

/**
 * 语音处理接口
 */
interface IVoiceProcessor {
    /**
     * 开始录音
     */
    fun startRecording()

    /**
     * 停止录音
     * @return 录音文件
     */
    suspend fun stopRecording(): File

    /**
     * 播放语音
     * @param audioFile 语音文件
     */
    fun playVoice(audioFile: File)

    /**
     * 停止播放
     */
    fun stopPlaying()

    /**
     * 语音转文字
     * @param audioFile 语音文件
     * @return 转换结果
     */
    suspend fun speechToText(audioFile: File): String

    /**
     * 文字转语音
     * @param text 文字内容
     * @return 语音文件
     */
    suspend fun textToSpeech(text: String): File
}

/**
 * 聊天配置接口
 */
interface IChatConfig {
    /**
     * 获取消息历史保存天数
     */
    fun getMessageHistoryDays(): Int

    /**
     * 获取语音最大时长（秒）
     */
    fun getMaxVoiceDuration(): Int

    /**
     * 获取消息重试次数
     */
    fun getMaxRetryAttempts(): Int

    /**
     * 获取语音质量设置
     */
    fun getVoiceQualitySettings(): VoiceQualitySettings
}

/**
 * 语音质量设置
 */
data class VoiceQualitySettings(
    val sampleRate: Int,
    val bitRate: Int,
    val channels: Int
)

/**
 * 聊天状态监听器
 */
interface IChatStateListener {
    /**
     * 连接状态变化
     * @param connected 是否连接
     */
    fun onConnectionStateChanged(connected: Boolean)

    /**
     * 新消息到达
     * @param message 消息
     */
    fun onNewMessage(message: ChatMessage)

    /**
     * 消息状态更新
     * @param messageId 消息ID
     * @param status 新状态
     */
    fun onMessageStatusUpdated(messageId: String, status: MessageStatus)

    /**
     * 错误发生
     * @param error 错误信息
     */
    fun onError(error: String)
}
