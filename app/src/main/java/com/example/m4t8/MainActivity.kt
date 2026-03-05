package com.example.m4t8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m4t8.ui.theme.M4t8Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            M4t8Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PhotoProcessingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PhotoProcessingScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf("Готово к запуску") }
    var resultMessage by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = currentStep,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        if (isRunning) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = "Прогресс шага: ${(progress * 100).toInt()}%")
        }

        Button(
            onClick = {
                scope.launch {
                    isRunning = true
                    resultMessage = ""

                    try {
                        processStep(
                            title = "Сжимаем фото…",
                            onStatusUpdate = {
                                currentStep = it
                            },
                            onProgressUpdate = {
                                progress = it
                            }
                        )

                        processStep(
                            title = "Добавляем водяной знак…",
                            onStatusUpdate = {
                                currentStep = it
                            },
                            onProgressUpdate = {
                                progress = it
                            }
                        )

                        processStep(
                            title = "Загружаем в облако…",
                            onStatusUpdate = {
                                currentStep = it
                            },
                            onProgressUpdate = {
                                progress = it
                            }
                        )

                        val filePath = "cloud://uploads/photo_ready_2026_01.jpg"
                        currentStep = "Готово! Фото загружено"
                        resultMessage = "Файл успешно сохранён: $filePath"
                    } catch (e: IllegalStateException) {
                        currentStep = "Ошибка обработки"
                        resultMessage = e.message ?: "Неизвестная ошибка"
                    } finally {
                        isRunning = false
                        progress = 0f
                    }
                }
            },
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Начать обработку и загрузку")
        }

        if (resultMessage.isNotBlank()) {
            Text(text = resultMessage)
        }
    }
}

private suspend fun processStep(
    title: String,
    onStatusUpdate: (String) -> Unit,
    onProgressUpdate: (Float) -> Unit
) {
    onStatusUpdate(title)
    for (i in 0..100) {
        delay(25)
        onProgressUpdate(i / 100f)
    }

    if (Random.nextFloat() < 0.15f) {
        throw IllegalStateException("Шаг «$title» завершился с ошибкой. Цепочка отменена.")
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoProcessingScreenPreview() {
    M4t8Theme {
        PhotoProcessingScreen()
    }
}
