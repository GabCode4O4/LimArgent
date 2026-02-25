package com.example.limargent.Presentation.Configuration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.limargent.Presentation.Configuration.ConfigurationScreen
import com.example.limargent.Presentation.Configuration.ConfigurationViewModel
import kotlin.getValue

class ConfigurationActivity : ComponentActivity(){
        private val viewModel: ConfigurationViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                ConfigurationScreen(viewModel = viewModel)
            }
        }
    }

}