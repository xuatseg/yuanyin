/**
 * 机器人控制模块依赖注入配置
 * 
 * 该模块负责：
 * 1. 提供机器人控制相关组件的依赖注入
 * 2. 绑定接口和实现类
 * 3. 提供应用上下文
 * 
 * 技术特点：
 * - 使用 Hilt 进行依赖注入
 * - 所有组件都是单例
 * - 清晰的依赖关系管理
 */
package com.looirobot.core.robot_control.di

import android.content.Context
import com.looirobot.core.robot_control.RobotControl
import com.looirobot.core.robot_control.RobotControlImpl
import com.looirobot.core.robot_control.hardware.BluetoothHardwareInterface
import com.looirobot.core.robot_control.hardware.BluetoothScanner
import com.looirobot.core.robot_control.hardware.HardwareInterface
import com.looirobot.core.robot_control.hardware.MotorController
import com.looirobot.core.robot_control.hardware.SensorManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RobotControlModule {
    @Binds
    @Singleton
    abstract fun bindRobotControl(impl: RobotControlImpl): RobotControl
    
    @Binds
    @Singleton
    abstract fun bindHardwareInterface(impl: BluetoothHardwareInterface): HardwareInterface
    
    companion object {
        @Provides
        @Singleton
        fun provideBluetoothScanner(
            @ApplicationContext context: Context
        ): BluetoothScanner {
            return BluetoothScanner(context)
        }
        
        @Provides
        @Singleton
        fun provideMotorController(hardwareInterface: HardwareInterface): MotorController {
            return MotorController(hardwareInterface)
        }
        
        @Provides
        @Singleton
        fun provideSensorManager(hardwareInterface: HardwareInterface): SensorManager {
            return SensorManager(hardwareInterface)
        }
    }
} 