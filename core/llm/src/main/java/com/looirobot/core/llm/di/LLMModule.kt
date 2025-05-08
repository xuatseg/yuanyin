/**
 * LLM 模块依赖注入配置
 * 
 * 该模块负责：
 * 1. 提供 LLM 相关组件的依赖注入
 * 2. 绑定接口和实现类
 * 3. 提供应用上下文
 * 
 * 技术特点：
 * - 使用 Hilt 进行依赖注入
 * - 所有组件都是单例
 * - 清晰的依赖关系管理
 */
package com.looirobot.core.llm.di

import android.content.Context
import com.looirobot.core.llm.LLM
import com.looirobot.core.llm.LLMImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LLMModule {
    @Binds
    @Singleton
    abstract fun bindLLM(
        llmImpl: LLMImpl
    ): LLM
    
    companion object {
        @Provides
        @Singleton
        fun provideContext(
            @ApplicationContext context: Context
        ): Context = context
    }
} 