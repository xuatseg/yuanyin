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
import com.xuatseg.yuanyin.model.WorkMode
import com.xuatseg.yuanyin.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val botState by viewModel.botState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI机器人控制台") },
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
            // 状态卡片
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // 连接状态
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("连接状态")
                        Text(
                            text = if (botState.isConnected) "已连接" else "未连接",
                            color = if (botState.isConnected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 电池状态
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("电池电量")
                        Text("${botState.batteryLevel}%")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 工作模式
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("工作模式")
                        Text(
                            text = when(botState.workMode) {
                                WorkMode.STANDBY -> "待机"
                                WorkMode.WORKING -> "工作中"
                                WorkMode.CHARGING -> "充电中"
                                WorkMode.ERROR -> "错误"
                            }
                        )
                    }
                }
            }

            // 控制按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        viewModel.updateConnectionStatus(!botState.isConnected)
                    }
                ) {
                    Text(if (botState.isConnected) "断开连接" else "连接设备")
                }

                Button(
                    onClick = {
                        viewModel.updateWorkMode(
                            if (botState.workMode == WorkMode.STANDBY)
                                WorkMode.WORKING
                            else
                                WorkMode.STANDBY
                        )
                    },
                    enabled = botState.isConnected
                ) {
                    Text(if (botState.workMode == WorkMode.WORKING) "停止" else "开始工作")
                }
            }

            // 错误提示
            botState.error?.let { error ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        IconButton(onClick = { viewModel.setError(null) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
