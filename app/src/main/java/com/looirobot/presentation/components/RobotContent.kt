package com.looirobot.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.looirobot.domain.model.MovementDirection

@Composable
fun RobotContent(
    robotStatus: String,
    batteryLevel: Int,
    isConnected: Boolean,
    speed: Float,
    onMove: (MovementDirection, Float) -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "状态: $robotStatus",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Text(
            text = "电池电量: $batteryLevel%",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Text(
            text = "连接状态: ${if (isConnected) "已连接" else "未连接"}",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Text(
            text = "当前速度: ${speed}",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onMove(MovementDirection.FORWARD, 1.0f) }) {
                Text("前进")
            }
            Button(onClick = { onMove(MovementDirection.BACKWARD, 1.0f) }) {
                Text("后退")
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onMove(MovementDirection.LEFT, 1.0f) }) {
                Text("左转")
            }
            Button(onClick = { onMove(MovementDirection.RIGHT, 1.0f) }) {
                Text("右转")
            }
        }
        
        Button(
            onClick = onStop,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("停止")
        }
    }
} 