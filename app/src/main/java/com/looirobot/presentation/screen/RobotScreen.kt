package com.looirobot.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.looirobot.core.utils.collectAsStateWithLifecycle
import com.looirobot.domain.model.MovementDirection
import com.looirobot.presentation.viewmodel.RobotState
import com.looirobot.presentation.viewmodel.RobotViewModel
import com.looirobot.presentation.components.LoadingIndicator
import com.looirobot.presentation.components.ErrorMessage
import com.looirobot.presentation.components.RobotContent

@Composable
fun RobotScreen(
    viewModel: RobotViewModel = hiltViewModel()
) {
    val robotState by viewModel.robotState.collectAsStateWithLifecycle(initialValue = RobotState())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            robotState.error != null -> {
                ErrorMessage(errorMessage = robotState.error)
            }
            else -> {
                RobotContent(
                    robotStatus = robotState.status,
                    batteryLevel = robotState.batteryLevel,
                    isConnected = robotState.isConnected,
                    speed = robotState.currentSpeed,
                    onMove = { direction, speed ->
                        viewModel.move(direction, speed)
                    },
                    onStop = {
                        viewModel.stop()
                    }
                )
            }
        }
    }
} 