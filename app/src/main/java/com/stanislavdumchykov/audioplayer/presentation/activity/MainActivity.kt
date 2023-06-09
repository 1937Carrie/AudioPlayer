package com.stanislavdumchykov.audioplayer.presentation.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.stanislavdumchykov.audioplayer.presentation.screen.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val permissionState = rememberPermissionState(
                permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_AUDIO
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )

            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(key1 = lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        permissionState.launchPermissionRequest()
                    }

                }
                lifecycleOwner.lifecycle.addObserver(observer)


                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF000000)
            ) {
                if (permissionState.status == PermissionStatus.Granted) {
                    val audioViewModel: AudioViewModel = viewModel()
                    val audioList = audioViewModel.audioList

                    HomeScreen(
                        progress = audioViewModel.currentAudioProgress.value,
                        onProgressChange = {
                            audioViewModel.seekTo(it)
                        },
                        isAudioPlaying = audioViewModel.isAudioPlaying,
                        audioList = audioList,
                        currentPlayingAudio = audioViewModel.currentPlayingAudio.value,
                        onStart = {
                            audioViewModel.playAudio(it)
                        },
                        onItemClick = {
                            audioViewModel.playAudio(it)
                        },
                        onNext = {
                            audioViewModel.skipToNext()
                        }
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "Grant permission first to use this app")
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("this.moveTaskToBack(true)"))
    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }

}