package com.xuatseg.yuanyin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.xuatseg.yuanyin.mode.ModeManagerStub
import com.xuatseg.yuanyin.mode.ModeMonitorStub
import com.xuatseg.yuanyin.mode.ModePersistenceStub
import com.xuatseg.yuanyin.ui.control.RobotControlViewModel
import com.xuatseg.yuanyin.ui.screens.MainScreen
import com.xuatseg.yuanyin.ui.theme.YuanYinTheme
import com.xuatseg.yuanyin.viewmodel.MainViewModel
import com.xuatseg.yuanyin.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            modeManager = ModeManagerStub(),
            modePersistence = ModePersistenceStub(),
            modeMonitor = ModeMonitorStub()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YuanYinTheme {
                MainScreen(
                    viewModel = viewModel,
                    modeSwitchViewModel = viewModel,
                    robotControlViewModel = RobotControlViewModel()
                )
            }
        }
    }
}
