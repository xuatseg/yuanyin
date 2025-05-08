package com.looirobot.domain.model

data class Robot(
    val id: String,
    val name: String,
    val batteryLevel: Float,
    val isConnected: Boolean
) 