package com.example.limargent.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.limargent.presentation.analytics.AnalyticsScreen
import com.example.limargent.presentation.analytics.AnalyticsViewModel
import com.example.limargent.presentation.configuration.ConfigurationScreen
import com.example.limargent.presentation.configuration.ConfigurationViewModel
import com.example.limargent.presentation.home.AddMouvement
import com.example.limargent.presentation.home.HomeScreen
import com.example.limargent.presentation.home.HomeViewModel
import com.example.limargent.presentation.objectif.ObjectifScreen
import com.example.limargent.presentation.objectif.ObjectifViewModel
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.example.limargent.R

enum class AppScreen(val title: String, val icon: ImageVector) {
    HOME("Accueil", Icons.Default.Home),
    ANALYTICS("Analytique", Icons.Default.Analytics),
    OBJECTIF("Objectif", Icons.Default.TrackChanges),
    CONFIGURATION("Config", Icons.Default.Settings)


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onLogout: () -> Unit
) {
    var screenAct by remember {mutableStateOf(AppScreen.HOME)}
    var showAddMouv by remember {mutableStateOf(false)}

    val homeViewModel: HomeViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppScreen.entries.forEach {screen ->
                    NavigationBarItem(
                        icon = {Icon(screen.icon, contentDescription = screen.title)},
                        label = {Text(screen.title)},
                        selected = screenAct == screen,
                        onClick = {screenAct = screen}
                    )
                }
            }
        },
        floatingActionButton = {
            if (screenAct == AppScreen.HOME){
                FloatingActionButton(
                    onClick = {showAddMouv = true}
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Ajouter un mouvement")
                }
            }
        }
    ){innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)){
            when (screenAct) {
                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = homeViewModel
                    )
                }
                AppScreen.ANALYTICS -> {
                    val viewModel = hiltViewModel<AnalyticsViewModel>()
                    AnalyticsScreen(viewModel = viewModel)
                }
                AppScreen.OBJECTIF->{
                    val viewModel = hiltViewModel<ObjectifViewModel>()
                    ObjectifScreen(viewModel = viewModel)
                }
                AppScreen.CONFIGURATION -> {
                    val viewModel = hiltViewModel<ConfigurationViewModel>()
                    ConfigurationScreen(viewModel = viewModel, onLogout = onLogout)
                }

            }
        }

        if (showAddMouv){
            AddMouvement(
                mouvementToEdit = null,
                categories = homeViewModel.categories,
                onDismiss = {showAddMouv = false},
                onConfirm = {mouvement ->
                    homeViewModel.saveMouvement(mouvement)
                    showAddMouv = false
                }
            )
        }
    }
}

