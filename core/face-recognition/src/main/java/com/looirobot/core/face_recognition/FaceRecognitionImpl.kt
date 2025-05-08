/**
 * 人脸识别核心实现类
 * 
 * 该实现类负责：
 * 1. 实现人脸识别接口定义的所有功能
 * 2. 管理检测状态
 * 3. 处理图像分析
 * 4. 错误处理
 * 
 * 技术特点：
 * - 使用 ML Kit 进行人脸检测
 * - 使用 StateFlow 管理状态
 * - 支持实时特征点识别
 * - 完整的错误处理机制
 */
package com.looirobot.core.face_recognition

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FaceRecognitionImpl @Inject constructor() : FaceRecognition {
    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
        
        FaceDetection.getClient(options)
    }
    
    private val _detectionStatus = MutableStateFlow(
        DetectionStatus(isDetecting = false)
    )
    
    private val _detectedFaces = MutableStateFlow<List<FaceDetectionResult>>(emptyList())
    
    override fun getDetectionStatus(): Flow<DetectionStatus> = _detectionStatus.asStateFlow()
    
    override fun getDetectedFaces(): Flow<List<FaceDetectionResult>> = _detectedFaces.asStateFlow()
    
    override suspend fun startDetection() {
        try {
            _detectionStatus.value = _detectionStatus.value.copy(
                isDetecting = true,
                error = null
            )
        } catch (e: Exception) {
            handleError(e)
            throw FaceRecognitionError.DetectionError("Failed to start detection: ${e.message}")
        }
    }
    
    override suspend fun stopDetection() {
        try {
            _detectionStatus.value = _detectionStatus.value.copy(
                isDetecting = false,
                error = null
            )
            _detectedFaces.value = emptyList()
        } catch (e: Exception) {
            handleError(e)
            throw FaceRecognitionError.DetectionError("Failed to stop detection: ${e.message}")
        }
    }
    
    override suspend fun processImage(bitmap: Bitmap): List<FaceDetectionResult> {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            
            return suspendCancellableCoroutine { continuation ->
                faceDetector.process(image)
                    .addOnSuccessListener { faces ->
                        val results = faces.map { face ->
                            FaceDetectionResult(
                                boundingBox = face.boundingBox,
                                confidence = face.smilingProbability ?: 0f,
                                faceId = face.trackingId?.toString(),
                                landmarks = face.allLandmarks.map { landmark ->
                                    FaceLandmark(
                                        type = when (landmark.landmarkType) {
                                            FaceLandmark.LEFT_EYE -> LandmarkType.LEFT_EYE
                                            FaceLandmark.RIGHT_EYE -> LandmarkType.RIGHT_EYE
                                            FaceLandmark.NOSE_BASE -> LandmarkType.NOSE
                                            FaceLandmark.MOUTH_LEFT -> LandmarkType.MOUTH_LEFT
                                            FaceLandmark.MOUTH_RIGHT -> LandmarkType.MOUTH_RIGHT
                                            else -> null
                                        } ?: LandmarkType.NOSE,
                                        position = landmark.position
                                    )
                                }
                            )
                        }
                        _detectedFaces.value = results
                        continuation.resume(results)
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(
                            FaceRecognitionError.ProcessingError("Failed to process image: ${e.message}")
                        )
                    }
            }
        } catch (e: Exception) {
            handleError(e)
            throw when (e) {
                is FaceRecognitionError -> e
                else -> FaceRecognitionError.ProcessingError("Failed to process image: ${e.message}")
            }
        }
    }
    
    private fun handleError(e: Exception) {
        _detectionStatus.value = _detectionStatus.value.copy(
            error = e.message
        )
    }
}

sealed class FaceRecognitionError : Exception() {
    data class DetectionError(override val message: String) : FaceRecognitionError()
    data class ProcessingError(override val message: String) : FaceRecognitionError()
} 