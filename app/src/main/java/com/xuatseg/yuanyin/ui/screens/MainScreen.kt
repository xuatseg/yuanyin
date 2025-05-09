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
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.xuatseg.yuanyin.ui.VideoPlayer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog

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

    // 新增遮罩弹窗状态
    var showSwitchMask by remember { mutableStateOf(false) }
    var showSettingsMask by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027), // 深蓝
                        Color(0xFF2C5364), // 蓝紫
                        Color(0xFF1A2980)  // 科幻蓝
                    )
                )
            )
    ) {
        // 1. 视频背景最底层
        val context = LocalContext.current
        val videoUri = remember {
            Uri.parse("android.resource://" + context.packageName + "/raw/face_happy")
        }
        VideoPlayer(
            videoUri = videoUri,
            modifier = Modifier.fillMaxSize(),
            repeat = true,
            mute = true
        )
        // 2. 半透明遮罩增强科幻感
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA0F2027))
        )
        // 3. 右侧竖向排列按钮
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 24.dp, top = 48.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            // 切换按钮（开关图标）
            IconButton(onClick = { showSwitchMask = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "切换模式",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // 设置按钮
            IconButton(onClick = { showSettingsMask = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        // 4. 切换遮罩弹窗
        if (showSwitchMask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)) // 遮罩外部区域
                    .clickable { showSwitchMask = false }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 0.dp)
                        .width(220.dp)
                        .fillMaxHeight()
                        .background(Color(0xEE222B3A), shape = MaterialTheme.shapes.medium)
                        .clickable(enabled = false) { }, // 禁止遮罩内容冒泡
                ) {
                    // 竖向排列所有模式
                    val modes = ProcessingMode.values()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text("模式切换", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        modes.forEach { mode ->
                            val isCurrent = mode == modeSwitchUiState.currentMode
                            Button(
                                onClick = {
                                    modeSwitchViewModel.handleEvent(ModeSwitchEvent.SwitchMode(mode))
                                    showSwitchMask = false
                                },
                                enabled = !isCurrent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(text = mode.name, color = if (isCurrent) Color.Gray else Color.White)
                            }
                        }
                    }
                }
            }
        }
        // 5. 设置遮罩弹窗
        if (showSettingsMask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)) // 遮罩外部区域
                    .clickable { showSettingsMask = false }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 0.dp)
                        .width(320.dp)
                        .fillMaxHeight()
                        .background(Color(0xEE222B3A), shape = MaterialTheme.shapes.medium)
                        .clickable(enabled = false) { }, // 禁止遮罩内容冒泡
                ) {
                    // 左右布局：左avatar，右个性信息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 随机avatar（可用默认头像）
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color.LightGray, shape = MaterialTheme.shapes.large),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", fontSize = 36.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        // 个性信息
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("昵称：未来机器人", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("签名：探索未来，连接智能世界。", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
