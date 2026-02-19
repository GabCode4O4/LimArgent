package com.example.limargent.Presentation.Dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // Important pour la survie du ViewModel
import androidx.compose.material3.MaterialTheme

class DashboardActivity : ComponentActivity() {

    private val viewModel: DashBoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DashBoardScreen(viewModel = viewModel)
            }
        }
    }
}