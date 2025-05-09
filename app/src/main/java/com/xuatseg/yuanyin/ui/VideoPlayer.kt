package com.xuatseg.yuanyin.ui

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUri: Uri,
    modifier: Modifier = Modifier,
    repeat: Boolean = true,
    mute: Boolean = true
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            playWhenReady = true
            repeatMode = if (repeat) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
            volume = if (mute) 0f else 1f
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            modifier = modifier
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
} 