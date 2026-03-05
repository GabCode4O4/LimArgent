package com.example.limargent.Presentation.Configuration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
                    0 -> CategorieSection(viewModel)
                    1 -> PreferenceSection(viewModel)
                }
            }
        }
    }
}

@Composable
fun PreferenceSection(viewModel: ConfigurationViewModel) {
    val theme by viewModel.theme.collectAsState()
    val isDarkTheme = theme == "dark"
    val seuilBudget by viewModel.seuilBudget.collectAsState()
    val notificationActive by viewModel.notificationActive.collectAsState()
    var seuilInput by remember(seuilBudget) { mutableStateOf(seuilBudget.toString()) }

    Column(modifier = Modifier.fillMaxWidth()) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notification seuil budget")
            Switch(
                checked = notificationActive,
                onCheckedChange = {
                    viewModel.setNotificationActive(it)
                }
            )
        }

        if (notificationActive) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = seuilInput,
                onValueChange = {
                    seuilInput = it
                    it.toDoubleOrNull()?.let { value ->
                        viewModel.setSeuilBudget(value)
                    }
                },
                label = { Text("Seuil d'alerte (€)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}

@Composable
fun CategorieSection(viewModel: ConfigurationViewModel) {
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