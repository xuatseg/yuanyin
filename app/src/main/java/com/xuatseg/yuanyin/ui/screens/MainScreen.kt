package com.xuatseg.yuanyin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuatseg.yuanyin.core.IRobotStateManager
import com.xuatseg.yuanyin.model.WorkMode
import com.xuatseg.yuanyin.mode.ProcessingMode
import com.xuatseg.yuanyin.robot.RobotState
import com.xuatseg.yuanyin.robot.SensorType
import com.xuatseg.yuanyin.robot.SpeedCommand
import com.xuatseg.yuanyin.ui.control.*
import com.xuatseg.yuanyin.ui.mode.*
import com.xuatseg.yuanyin.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modeSwitchViewModel: IModeSwitchViewModel,
    robotControlViewModel: IRobotControlViewModel
) {
    val botState by viewModel.botState.collectAsState()
    val modeSwitchUiState by modeSwitchViewModel.getUiState().collectAsState(initial = ModeSwitchUiState(
        currentMode = ProcessingMode.LOCAL,
        availableModes = listOf()
    ))
    val robotControlUiState by robotControlViewModel.getUiState().collectAsState(initial = RobotControlUiState(
        robotState = robotControlViewModel.getCurrentRobotState(),
        sensorData = robotControlViewModel.getSensorData(),
        systemHealth = robotControlViewModel.getSystemHealth(),
        isConnected = false
    ))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI机器人控制台") },
                actions = {
                    // 添加模式切换按钮
                    ModeSwitchButton(
                        currentMode = modeSwitchUiState.currentMode,
                        onModeSwitch = { mode ->
                            modeSwitchViewModel.handleEvent(ModeSwitchEvent.SwitchMode(mode))
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 状态栏
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 模式状态指示器
                ModeStatusIndicator(
                    modeState = modeSwitchViewModel.getCurrentModeState(),
                    showDetails = true
                )

                // 电池状态
                BatteryIndicator(
                    level = botState.batteryLevel.toFloat(),
                    isCharging = botState.workMode == WorkMode.CHARGING
                )
            }

            // 机器人控制面板
            RobotControlPanel(
                uiState = robotControlUiState,
                onControlEvent = { event ->
                    robotControlViewModel.handleControlEvent(event)
                },
                modifier = Modifier.weight(1f)
            )

            // 传感器数据显示
            SensorDataDisplay(
                sensorData = robotControlUiState.sensorData,
                selectedSensors = setOf(
                    SensorType.IMU,
                    SensorType.LIDAR,
                    SensorType.BATTERY
                )
            )

            // 控制按钮组
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 方向控制
                DirectionalControls(
                    onMove = { command ->
                        robotControlViewModel.handleControlEvent(RobotControlEvent.Move(command))
                    },
                    isEnabled = robotControlUiState.isConnected
                )

                // 紧急停止按钮
                EmergencyStopButton(
                    onStop = {
                        robotControlViewModel.handleControlEvent(RobotControlEvent.Stop)
                    },
                    isEnabled = robotControlUiState.isConnected
                )
            }

            // 速度控制
            SpeedControl(
                currentSpeed = robotControlUiState.robotState.speed.let {
                    SpeedCommand(it.linear, it.angular)
                },
                onSpeedChange = { speedCommand ->
                    robotControlViewModel.handleControlEvent(RobotControlEvent.SetSpeed(speedCommand))
                },
                isEnabled = robotControlUiState.isConnected
            )

            // 系统健康状态
            HealthIndicator(
                health = robotControlUiState.systemHealth,
                showDetails = true
            )

            // 错误提示
            robotControlUiState.error?.let { error ->
                ErrorSnackbar(
                    error = error,
                    onDismiss = {
                        robotControlViewModel.handleControlEvent(RobotControlEvent.ClearError)
                    }
                )
            }

            // 模式错误提示
            modeSwitchUiState.error?.let { error ->
                ModeErrorSnackbar(
                    error = error,
                    onDismiss = {
                        modeSwitchViewModel.handleEvent(ModeSwitchEvent.DismissError)
                    }
                )
            }
        }
    }
}
