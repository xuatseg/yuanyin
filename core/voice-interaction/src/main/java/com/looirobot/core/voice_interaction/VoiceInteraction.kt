/**
 * 语音交互核心接口定义
 * 
 * 该接口定义了语音交互功能的核心操作：
 * 1. 语音识别控制（开始/停止）
 * 2. 语音合成
 * 3. 语音命令处理
 * 4. 状态监控
 * 
 * 包含的数据类：
 * - RecognitionResult：识别结果
 * - SynthesisStatus：合成状态
 * - CommandType：命令类型
 * - InteractionError：错误类型定义
 */
package com.looirobot.core.voice_interaction

import kotlinx.coroutines.flow.Flow

interface VoiceInteraction {
    suspend fun startListening()
    suspend fun stopListening()
    suspend fun speak(text: String)
    fun getVoiceStatus(): Flow<VoiceStatus>
    fun getRecognizedText(): Flow<String>
}

data class VoiceStatus(
    val isListening: Boolean,
    val isSpeaking: Boolean,
    val error: String? = null
) 