package com.example.limargent.Presentation.Configuration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.limargent.ui.theme.LimArgentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfigurationActivity : ComponentActivity() {
    private val viewModel: ConfigurationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by viewModel.theme.collectAsState()
            LimArgentTheme(theme = theme) {
                ConfigurationScreen(viewModel = viewModel)
            }
        }
    }
}