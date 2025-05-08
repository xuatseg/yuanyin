package com.looirobot.core.network

object NetworkConfig {
    const val CONNECT_TIMEOUT = 10_000L // 10秒
    const val READ_TIMEOUT = 10_000L // 10秒
    const val WRITE_TIMEOUT = 10_000L // 10秒
    const val MAX_RETRIES = 3
    const val RETRY_DELAY = 1000L // 1秒
} 