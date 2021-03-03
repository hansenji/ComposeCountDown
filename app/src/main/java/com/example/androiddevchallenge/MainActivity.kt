/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

const val INITIAL_TIME = 10_000L

@Composable
fun MyApp() {
    val coroutineScope = rememberCoroutineScope()
    val countDownTime = rememberSaveable { mutableStateOf(INITIAL_TIME) }
    val run = rememberSaveable { mutableStateOf(false) }
    if (run.value) {
        coroutineScope.launch {
            val ticker = ticker(5)
            for (event in ticker) {
                if (!run.value) return@launch
                val value = countDownTime.value
                if (value > 0) {
                    countDownTime.value = value - 5L
                } else {
                    run.value = false
                }
            }
        }
    }
    Surface(color = MaterialTheme.colors.background) {
        Timer(
            run.value,
            countDownTime.value,
            onStartStop = { run.value = !run.value },
            onReset = { countDownTime.value = INITIAL_TIME }
        )
    }
}

const val PROGRESS_TIME = 1_000L

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Timer(
    running: Boolean,
    timeMs: Long,
    onStartStop: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (PROGRESS_TIME - (timeMs % PROGRESS_TIME)) / PROGRESS_TIME.toFloat()
    val even = (timeMs / PROGRESS_TIME % 2) == 0L
    val color1 = if (even) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
    val color2 = if (!even) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
    val resetButtonVisible = timeMs != INITIAL_TIME && !running

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = 1F,
            color = color1,
            modifier = Modifier
                .padding(16.dp)
                .aspectRatio(1F)
                .fillMaxSize()
                .align(Alignment.Center)
        )
        CircularProgressIndicator(
            progress = progress,
            color = color2,
            modifier = Modifier
                .padding(16.dp)
                .aspectRatio(1F)
                .fillMaxSize()
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(ButtonDefaults.MinWidth, ButtonDefaults.MinHeight))
            Text(text = "%02.2f".format(timeMs / 1000.0), style = MaterialTheme.typography.h3)
            Row {
                Button(
                    onClick = {
                        onStartStop()
                    }, modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape),
                    enabled = timeMs != 0L
                ) {
                    Image(
                        imageVector = if (running) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (running) "Pause" else "Start",

                        )
                }
                AnimatedVisibility(resetButtonVisible) {
                    Button(
                        onClick = { onReset() }, modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            imageVector = Icons.Outlined.RestartAlt,
                            contentDescription = "Reset",
                        )
                    }
                }
            }

        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
