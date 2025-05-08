/**
 * 人脸识别核心接口定义
 * 
 * 该接口定义了人脸识别功能的核心操作：
 * 1. 人脸检测控制（开始/停止）
 * 2. 图像处理
 * 3. 特征点识别
 * 4. 状态监控
 * 
 * 包含的数据类：
 * - FaceDetectionResult：检测结果
 * - FaceLandmark：面部特征点
 * - DetectionStatus：检测状态
 * - LandmarkType：特征点类型
 */
package com.looirobot.core.face_recognition

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface FaceRecognition {
    suspend fun startDetection()
    suspend fun stopDetection()
    suspend fun processImage(bitmap: Bitmap): List<FaceDetectionResult>
    fun getDetectionStatus(): Flow<DetectionStatus>
    fun getDetectedFaces(): Flow<List<FaceDetectionResult>>
}

data class FaceDetectionResult(
    val boundingBox: android.graphics.Rect,
    val confidence: Float,
    val faceId: String? = null,
    val landmarks: List<FaceLandmark> = emptyList()
)

data class FaceLandmark(
    val type: LandmarkType,
    val position: android.graphics.PointF
)

enum class LandmarkType {
    LEFT_EYE,
    RIGHT_EYE,
    NOSE,
    MOUTH_LEFT,
    MOUTH_RIGHT
}

data class DetectionStatus(
    val isDetecting: Boolean,
    val error: String? = null
) 