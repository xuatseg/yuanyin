package com.looirobot.core.llm

import android.content.Context
import com.looirobot.core.llm.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LLMImpl @Inject constructor(
    private val context: Context
) : LLM {
    private val _status = MutableStateFlow(
        LLMStatus(
            isProcessing = false,
            currentModel = LLMModel.GPT_3_5_TURBO,
            parameters = LLMParameters()
        )
    )
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService = retrofit.create(LLMApiService::class.java)
    
    override fun getStatus(): Flow<LLMStatus> = _status.asStateFlow()
    
    override suspend fun generateResponse(prompt: String): String {
        try {
            validateParameters()
            _status.value = _status.value.copy(isProcessing = true)
            
            val response = apiService.generateCompletion(
                CompletionRequest(
                    model = _status.value.currentModel.name,
                    prompt = prompt,
                    parameters = _status.value.parameters
                )
            )
            
            _status.value = _status.value.copy(isProcessing = false)
            return response.text
        } catch (e: Exception) {
            handleError(e)
            throw when (e) {
                is retrofit2.HttpException -> {
                    when (e.code()) {
                        401 -> LLMError.AuthenticationError("Invalid API key")
                        429 -> LLMError.NetworkError("Rate limit exceeded")
                        else -> LLMError.NetworkError("API error: ${e.message()}")
                    }
                }
                else -> LLMError.NetworkError("Failed to generate response: ${e.message}")
            }
        }
    }
    
    override suspend fun generateStreamingResponse(prompt: String): Flow<String> = flow {
        try {
            validateParameters()
            _status.value = _status.value.copy(isProcessing = true)
            
            apiService.generateStreamingCompletion(
                CompletionRequest(
                    model = _status.value.currentModel.name,
                    prompt = prompt,
                    parameters = _status.value.parameters
                )
            ).collect { response ->
                emit(response.text)
            }
            
            _status.value = _status.value.copy(isProcessing = false)
        } catch (e: Exception) {
            handleError(e)
            throw when (e) {
                is retrofit2.HttpException -> {
                    when (e.code()) {
                        401 -> LLMError.AuthenticationError("Invalid API key")
                        429 -> LLMError.NetworkError("Rate limit exceeded")
                        else -> LLMError.NetworkError("API error: ${e.message()}")
                    }
                }
                else -> LLMError.NetworkError("Failed to generate streaming response: ${e.message}")
            }
        }
    }
    
    override suspend fun analyzeImage(imagePath: String): String {
        try {
            validateParameters()
            _status.value = _status.value.copy(isProcessing = true)
            
            val response = apiService.analyzeImage(
                ImageAnalysisRequest(
                    model = _status.value.currentModel.name,
                    imagePath = imagePath,
                    parameters = _status.value.parameters
                )
            )
            
            _status.value = _status.value.copy(isProcessing = false)
            return response.text
        } catch (e: Exception) {
            handleError(e)
            throw when (e) {
                is retrofit2.HttpException -> {
                    when (e.code()) {
                        401 -> LLMError.AuthenticationError("Invalid API key")
                        429 -> LLMError.NetworkError("Rate limit exceeded")
                        else -> LLMError.NetworkError("API error: ${e.message()}")
                    }
                }
                else -> LLMError.NetworkError("Failed to analyze image: ${e.message}")
            }
        }
    }
    
    override suspend fun setModel(model: LLMModel) {
        _status.value = _status.value.copy(currentModel = model)
    }
    
    override suspend fun setParameters(parameters: LLMParameters) {
        validateParameters(parameters)
        _status.value = _status.value.copy(parameters = parameters)
    }
    
    private fun validateParameters(parameters: LLMParameters = _status.value.parameters) {
        if (parameters.temperature !in 0.0f..2.0f) {
            throw LLMError.ParameterError("Temperature must be between 0.0 and 2.0")
        }
        if (parameters.maxTokens <= 0) {
            throw LLMError.ParameterError("Max tokens must be greater than 0")
        }
        if (parameters.topP !in 0.0f..1.0f) {
            throw LLMError.ParameterError("Top P must be between 0.0 and 1.0")
        }
        if (parameters.frequencyPenalty !in -2.0f..2.0f) {
            throw LLMError.ParameterError("Frequency penalty must be between -2.0 and 2.0")
        }
        if (parameters.presencePenalty !in -2.0f..2.0f) {
            throw LLMError.ParameterError("Presence penalty must be between -2.0 and 2.0")
        }
    }
    
    private fun handleError(e: Exception) {
        _status.value = _status.value.copy(
            isProcessing = false,
            error = e.message
        )
    }
} 