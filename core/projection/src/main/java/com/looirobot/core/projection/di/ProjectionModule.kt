/**
 * 投影功能模块依赖注入配置
 * 
 * 该模块负责：
 * 1. 提供投影相关组件的依赖注入
 * 2. 绑定接口和实现类
 * 3. 提供应用上下文
 * 
 * 技术特点：
 * - 使用 Hilt 进行依赖注入
 * - 所有组件都是单例
 * - 清晰的依赖关系管理
 */
package com.looirobot.core.projection.di

import android.content.Context
import com.looirobot.core.projection.Projection
import com.looirobot.core.projection.ProjectionImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectionModule {
    @Binds
    @Singleton
    abstract fun bindProjection(
        projectionImpl: ProjectionImpl
    ): Projection
    
    companion object {
        @Provides
        @Singleton
        fun provideContext(
            @ApplicationContext context: Context
        ): Context = context
    }
} 