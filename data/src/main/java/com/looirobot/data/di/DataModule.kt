/**
 * 数据层依赖注入模块
 * 
 * 该模块负责：
 * 1. 提供数据层组件的依赖注入配置
 * 2. 绑定接口和实现类
 * 3. 管理数据源的生命周期
 * 
 * 主要组件：
 * - LLMRepository 及其实现
 * - 远程数据源
 * - 本地数据源
 * - 应用上下文
 * 
 * 技术特点：
 * - 使用 Hilt 进行依赖注入
 * - 所有组件都是单例
 * - 清晰的依赖关系管理
 */
package com.looirobot.data.di

import android.content.Context
import com.looirobot.data.source.LLMDataSource
import com.looirobot.data.source.LocalLLMDataSource
import com.looirobot.data.source.RemoteLLMDataSource
import com.looirobot.domain.repository.LLMRepository
import com.looirobot.data.repository.LLMRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindLLMRepository(
        llmRepositoryImpl: LLMRepositoryImpl
    ): LLMRepository
    
    @Binds
    @Singleton
    abstract fun bindRemoteLLMDataSource(
        remoteLLMDataSource: RemoteLLMDataSource
    ): LLMDataSource
    
    @Binds
    @Singleton
    abstract fun bindLocalLLMDataSource(
        localLLMDataSource: LocalLLMDataSource
    ): LLMDataSource
    
    companion object {
        @Provides
        @Singleton
        fun provideContext(
            @ApplicationContext context: Context
        ): Context = context
    }
} 