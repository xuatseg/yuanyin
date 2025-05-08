/**
 * 人脸识别模块依赖注入配置
 * 
 * 该模块负责：
 * 1. 提供人脸识别相关组件的依赖注入
 * 2. 绑定接口和实现类
 * 3. 提供应用上下文
 * 
 * 技术特点：
 * - 使用 Hilt 进行依赖注入
 * - 所有组件都是单例
 * - 清晰的依赖关系管理
 */
package com.looirobot.core.face_recognition.di

import com.looirobot.core.face_recognition.FaceRecognition
import com.looirobot.core.face_recognition.FaceRecognitionImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FaceRecognitionModule {
    @Binds
    @Singleton
    abstract fun bindFaceRecognition(
        faceRecognitionImpl: FaceRecognitionImpl
    ): FaceRecognition
} 