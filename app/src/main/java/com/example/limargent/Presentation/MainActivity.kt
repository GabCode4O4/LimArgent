package com.example.limargent.Presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.limargent.Presentation.Dashboard.DashBoardScreen
import com.example.limargent.Presentation.Dashboard.DashBoardViewModel
import com.example.limargent.ui.theme.LimArgentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: DashBoardViewModel by viewModels()
        setContent {
            LimArgentTheme {
                DashBoardScreen(viewModel = viewModel)
            }
        }
    }
}
