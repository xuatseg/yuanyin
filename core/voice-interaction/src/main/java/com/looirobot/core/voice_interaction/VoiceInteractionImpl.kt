/**
 * 语音交互核心实现类
 * 
 * 该实现类负责：
 * 1. 实现语音交互接口定义的所有功能
 * 2. 管理识别和合成状态
 * 3. 处理语音命令
 * 4. 错误处理
 * 
 * 技术特点：
 * - 使用 Speech Recognition API
 * - 使用 Text-to-Speech API
 * - 使用 StateFlow 管理状态
 * - 完整的错误处理机制
 */
package com.looirobot.core.voice_interaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceInteractionImpl @Inject constructor(
    private val context: Context
) : VoiceInteraction {
    private val _voiceStatus = MutableStateFlow(
        VoiceStatus(
            isListening = false,
            isSpeaking = false
        )
    )
    
    private val _recognizedText = MutableStateFlow("")
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    
    init {
        initializeSpeechRecognizer()
        initializeTextToSpeech()
    }
    
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }
        }
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
            }
        }
    }
    
    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _voiceStatus.value = _voiceStatus.value.copy(isListening = true)
        }
        
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                _recognizedText.value = matches[0]
            }
            _voiceStatus.value = _voiceStatus.value.copy(isListening = false)
        }
        
        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            _voiceStatus.value = _voiceStatus.value.copy(
                isListening = false,
                error = errorMessage
            )
        }
        
        @Deprecated("This method is deprecated in RecognitionListener")
        override fun onBeginningOfSpeech() {}
        
        @Deprecated("This method is deprecated in RecognitionListener")
        override fun onRmsChanged(rmsdB: Float) {}
        
        @Deprecated("This method is deprecated in RecognitionListener")
        override fun onBufferReceived(buffer: ByteArray?) {}
        
        @Deprecated("This method is deprecated in RecognitionListener")
        override fun onEndOfSpeech() {}
        
        @Deprecated("This method is deprecated in RecognitionListener")
        override fun onPartialResults(partialResults: Bundle?) {}
        
        @Deprecated("This method is deprecated in RecognitionListener")
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
    
    override fun getVoiceStatus(): Flow<VoiceStatus> = _voiceStatus.asStateFlow()
    
    override fun getRecognizedText(): Flow<String> = _recognizedText.asStateFlow()
    
    override suspend fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            handleError(e)
            throw VoiceInteractionError.RecognitionError("Failed to start listening: ${e.message}")
        }
    }
    
    override suspend fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _voiceStatus.value = _voiceStatus.value.copy(isListening = false)
        } catch (e: Exception) {
            handleError(e)
            throw VoiceInteractionError.RecognitionError("Failed to stop listening: ${e.message}")
        }
    }
    
    override suspend fun speak(text: String) {
        try {
            _voiceStatus.value = _voiceStatus.value.copy(isSpeaking = true)
            
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                
                override fun onDone(utteranceId: String?) {
                    _voiceStatus.value = _voiceStatus.value.copy(isSpeaking = false)
                }
                
                override fun onError(utteranceId: String?) {
                    _voiceStatus.value = _voiceStatus.value.copy(
                        isSpeaking = false,
                        error = "Failed to speak text"
                    )
                }
            })
            
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID")
        } catch (e: Exception) {
            handleError(e)
            throw VoiceInteractionError.SynthesisError("Failed to speak text: ${e.message}")
        }
    }
    
    private fun handleError(e: Exception) {
        _voiceStatus.value = _voiceStatus.value.copy(
            error = e.message
        )
    }
    
    fun release() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

sealed class VoiceInteractionError : Exception() {
    data class RecognitionError(override val message: String) : VoiceInteractionError()
    data class SynthesisError(override val message: String) : VoiceInteractionError()
    data class ConfigurationError(override val message: String) : VoiceInteractionError()
} 