package com.example.limargent.presentation.configuration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import com.example.limargent.ui.theme.LimArgentTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@AndroidEntryPoint
class ConfigurationActivity : ComponentActivity() {
    private val viewModel: ConfigurationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            LimArgentTheme(theme = theme) {
                ConfigurationScreen(viewModel = viewModel, onLogout = {})
            }
        }
    }
}