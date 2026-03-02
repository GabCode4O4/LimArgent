package com.example.limargent.Presentation.Main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.limargent.Presentation.Analytics.AnalyticsScreen
import com.example.limargent.Presentation.Analytics.AnalyticsViewModel
import com.example.limargent.Presentation.Configuration.ConfigurationScreen
import com.example.limargent.Presentation.Configuration.ConfigurationViewModel
import com.example.limargent.Presentation.Dashboard.DashBoardScreen
import com.example.limargent.Presentation.Dashboard.DashBoardViewModel
import com.example.limargent.Presentation.Home.HomeScreen
import com.example.limargent.Presentation.Home.HomeViewModel

enum class AppScreen(val title: String, val icon: ImageVector) {
    HOME("Accueil", Icons.Default.Home),
    DASHBOARD("Tableau de bord", Icons.Default.Dashboard),
    ANALYTICS("Analytique", Icons.Default.Analytics),
    CONFIGURATION("Config", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onLogout: () -> Unit
) {
    var screenAct by remember { mutableStateOf(AppScreen.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppScreen.values().forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = screenAct == screen,
                        onClick = { screenAct = screen }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (screenAct) {
                AppScreen.HOME -> {
                    val viewModel = hiltViewModel<HomeViewModel>()
                    HomeScreen(
                        viewModel = viewModel,
                        onLogout = onLogout
                    )
                }
                AppScreen.DASHBOARD -> {
                    val viewModel = hiltViewModel<DashBoardViewModel>()
                    DashBoardScreen(viewModel = viewModel)
                }
                AppScreen.ANALYTICS -> {
                    val viewModel = hiltViewModel<AnalyticsViewModel>()
                    AnalyticsScreen(viewModel = viewModel)
                }
                AppScreen.CONFIGURATION -> {
                    val viewModel = hiltViewModel<ConfigurationViewModel>()
                    ConfigurationScreen(viewModel = viewModel)
                }
            }
        }
    }
}