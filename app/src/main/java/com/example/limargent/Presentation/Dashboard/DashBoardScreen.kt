package com.example.limargent.Presentation.Dashboard

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen(viewModel: DashBoardViewModel) {
    val budget = viewModel.budget
    val mouvements = viewModel.mouvements

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Budget: $budget",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mouvements récents",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun test() {
    val test = DashBoardViewModel()
    MaterialTheme {
        DashBoardScreen(viewModel = test)
    }
}