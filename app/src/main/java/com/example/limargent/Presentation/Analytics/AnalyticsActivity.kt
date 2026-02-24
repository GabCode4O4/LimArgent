package com.example.limargent.Presentation.Analytics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.limargent.Presentation.Dashboard.DashBoardScreen
import com.example.limargent.Presentation.Dashboard.DashBoardViewModel
import kotlin.getValue

class AnalyticsActivity : ComponentActivity() {

    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AnalyticsScreen(viewModel = viewModel)
            }
        }
    }
}