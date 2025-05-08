/**
 * 语音交互模块依赖注入配置
 * 
 * 该模块负责：
 * 1. 提供语音交互相关组件的依赖注入
 * 2. 绑定接口和实现类
 * 3. 提供应用上下文
 * 
 * 技术特点：
 * - 使用 Hilt 进行依赖注入
 * - 所有组件都是单例
 * - 清晰的依赖关系管理
 */
package com.looirobot.core.voice_interaction.di

import com.looirobot.core.voice_interaction.VoiceInteraction
import com.looirobot.core.voice_interaction.VoiceInteractionImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VoiceInteractionModule {
    @Binds
    @Singleton
    abstract fun bindVoiceInteraction(
        voiceInteractionImpl: VoiceInteractionImpl
    ): VoiceInteraction
} 