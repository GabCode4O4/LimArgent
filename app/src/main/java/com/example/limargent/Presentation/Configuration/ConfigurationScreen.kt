package com.example.limargent.Presentation.Configuration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(viewModel: ConfigurationViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Catégories", "Préférences")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configuration") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.padding(16.dp)) {
                when (selectedTabIndex) {
                    0 -> CategoriesSection(viewModel)
                    1 -> PreferencesSection(viewModel)
                }
            }
        }
    }
}

@Composable
fun PreferencesSection(viewModel: ConfigurationViewModel) {
    val theme by viewModel.theme.collectAsState()
    val isDarkTheme = theme == "dark"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Thème sombre")
        Switch(
            checked = isDarkTheme,
            onCheckedChange = {
                viewModel.setThemePreference(if (it) "dark" else "light")
            }
        )
    }
}

@Composable
fun CategoriesSection(viewModel: ConfigurationViewModel) {
    val categories by viewModel.categories.collectAsState()
    var newCategoryName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("Nouvelle catégorie") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.addCategorie(newCategoryName)
                newCategoryName = ""
            }) {
                Text("Ajouter")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(categories) { category ->
                CategoryItem(
                    categoryName = category,
                    onDelete = { viewModel.delCategorie(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(categoryName: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = categoryName, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer la catégorie")
            }
        }
    }
}