package com.example.limargent.Presentation.Main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.limargent.Presentation.Dashboard.DashBoardViewModel
import com.example.limargent.ui.theme.LimArgentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: DashBoardViewModel by viewModels()
        setContent {
            LimArgentTheme {
                MainScreen()
            }
        }
    }
}