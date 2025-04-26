package com.xuatseg.yuanyin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.xuatseg.yuanyin.ui.screens.MainScreen
import com.xuatseg.yuanyin.ui.theme.YuanYinTheme
import com.xuatseg.yuanyin.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YuanYinTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
