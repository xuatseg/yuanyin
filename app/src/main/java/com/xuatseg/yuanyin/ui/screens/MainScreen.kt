package com.xuatseg.yuanyin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.xuatseg.yuanyin.mode.ProcessingMode
import com.xuatseg.yuanyin.ui.control.IRobotControlViewModel
import com.xuatseg.yuanyin.ui.mode.IModeSwitchViewModel
import com.xuatseg.yuanyin.ui.mode.ModeSwitchButton
import com.xuatseg.yuanyin.ui.mode.ModeSwitchEvent
import com.xuatseg.yuanyin.ui.mode.ModeSwitchUiState
import com.xuatseg.yuanyin.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modeSwitchViewModel: IModeSwitchViewModel,
    robotControlViewModel: IRobotControlViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val modeSwitchUiState by modeSwitchViewModel.getUiState().collectAsState(initial = ModeSwitchUiState(
        currentMode = ProcessingMode.LOCAL,
        availableModes = listOf()
    )
    )

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                        .padding(16.dp)
                ) {
                    Text(
                        "模式切换",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    ModeSwitchButton(
                        currentMode = modeSwitchUiState.currentMode,
                        onModeSwitch = { mode ->
                            modeSwitchViewModel.handleEvent(ModeSwitchEvent.SwitchMode(mode))
                        }
                    )
                }
            }
        }
    ) {
        // 主要内容区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                // 添加水平拖动手势
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        scope.launch {
                            if (dragAmount < 0) { // 向左滑动
                                drawerState.open()
                            } else { // 向右滑动
                                drawerState.close()
                            }
                        }
                    }
                }
        ) {
            // 居中显示机器人表情
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 机器人表情
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "^_^",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
