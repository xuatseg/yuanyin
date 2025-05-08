/**
 * 投影功能核心接口定义
 * 
 * 该接口定义了投影功能的核心操作：
 * 1. 投影控制（开始/停止）
 * 2. 内容投影（图像/视频）
 * 3. 参数调节（亮度/对比度/焦距）
 * 4. 状态监控
 * 
 * 包含的数据类：
 * - ProjectionStatus：当前投影状态
 * - ContentType：投影内容类型
 * - ProjectionError：错误类型定义
 */
package com.looirobot.core.projection

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface Projection {
    suspend fun startProjection()
    suspend fun stopProjection()
    suspend fun projectImage(bitmap: Bitmap)
    suspend fun projectVideo(videoPath: String)
    suspend fun adjustBrightness(level: Float)
    suspend fun adjustContrast(level: Float)
    suspend fun adjustFocus(level: Float)
    fun getProjectionStatus(): Flow<ProjectionStatus>
}

data class ProjectionStatus(
    val isProjecting: Boolean,
    val currentContent: ContentType? = null,
    val brightness: Float = 1.0f,
    val contrast: Float = 1.0f,
    val focus: Float = 1.0f,
    val error: String? = null
)

enum class ContentType {
    IMAGE,
    VIDEO,
    NONE
}

sealed class ProjectionError : Exception() {
    data class ContentNotFound(override val message: String) : ProjectionError()
    data class HardwareError(override val message: String) : ProjectionError()
    data class ConfigurationError(override val message: String) : ProjectionError()
} 