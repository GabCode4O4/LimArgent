package com.example.limargent.Presentation.Main

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
import com.example.limargent.Presentation.Analytics.AnalyticsScreen
import com.example.limargent.Presentation.Analytics.AnalyticsViewModel
import com.example.limargent.Presentation.Configuration.ConfigurationScreen
import com.example.limargent.Presentation.Configuration.ConfigurationViewModel
import com.example.limargent.Presentation.Home.AddMouvement
import com.example.limargent.Presentation.Home.HomeScreen
import com.example.limargent.Presentation.Home.HomeViewModel
import com.example.limargent.Presentation.Objectif.ObjectifScreen
import com.example.limargent.Presentation.Objectif.ObjectifViewModel
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.example.limargent.R

enum class AppScreen(val title: String, val icon: ImageVector) {
    HOME("Accueil", Icons.Default.Home),
    ANALYTICS("Analytique", Icons.Default.Analytics),
    CONFIGURATION("Config", Icons.Default.Settings),
    OBJECTIF("Objectif", Icons.Default.AddBusiness)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onLogout: () -> Unit
) {
    var screenAct by remember { mutableStateOf(AppScreen.HOME) }
    var showAddMouv by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = screenAct == screen,
                        onClick = { screenAct = screen }
                    )
                }
            }
        },
        floatingActionButton = {
            if (screenAct == AppScreen.HOME) {
                FloatingActionButton(
                    onClick = { showAddMouv = true }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Ajouter un mouvement")
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
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

        if (showAddMouv) {
            AddMouvement(
                categories = homeViewModel.categories,
                onDismiss = { showAddMouv = false },
                onConfirm = { titre, montant, categorie, date, type, isRecurrent, frequence, nextDate ->
                    homeViewModel.addMouvement(titre, montant, categorie, date, type, isRecurrent, frequence, nextDate)
                    showAddMouv = false
                }
            )
        }
    }
}

@Composable
fun LogoLimArgent(
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val imageId = if (isDark) {
        R.drawable.logo_blanc
    } else {
        R.drawable.logo_noir
    }

    Image(
        painter = painterResource(id = imageId),
        contentDescription = "Logo de l'application LimArgent",
        modifier = modifier
    )
}