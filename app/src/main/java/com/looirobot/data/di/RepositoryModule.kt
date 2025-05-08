package com.looirobot.data.di

import com.looirobot.core.robot_control.RobotControl
import com.looirobot.data.repository.RobotRepositoryImpl
import com.looirobot.domain.repository.RobotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRobotRepository(
        robotControl: RobotControl
    ): RobotRepository {
        return RobotRepositoryImpl(robotControl)
    }
} 