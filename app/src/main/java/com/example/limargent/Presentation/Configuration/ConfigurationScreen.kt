package com.example.limargent.Presentation.Configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(viewModel: ConfigurationViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Catégories", "Mouvements")

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
            // --- Système d'onglets ---
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // --- Contenu selon l'onglet sélectionné ---
            Box(modifier = Modifier.padding(16.dp)) {
                when (selectedTabIndex) {
                    0 -> CategoriesSection(viewModel)
                    1 -> MouvementsSection(viewModel)
                }
            }
        }
    }
}

@Composable
fun CategoriesSection(viewModel: ConfigurationViewModel) {
    val categories = viewModel.categories
    var newCategoryName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Formulaire d'ajout
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

        // Liste des catégories
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MouvementsSection(viewModel: ConfigurationViewModel) {
    val mouvements = viewModel.mouvement
    val categories = viewModel.categories

    // Variables d'état pour le formulaire
    var titre by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MovType.sortie) }

    // Variables pour le menu déroulant des catégories
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }

    // On sélectionne la première catégorie par défaut si disponible
    LaunchedEffect(categories) {
        if (selectedCategory.isEmpty() && categories.isNotEmpty()) {
            selectedCategory = categories[0]
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Formulaire d'ajout de mouvement ---
        OutlinedTextField(
            value = titre,
            onValueChange = { titre = it },
            label = { Text("Titre du mouvement") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = montant,
                onValueChange = { montant = it },
                label = { Text("Montant") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )

            // Bouton de type (Entrée / Sortie)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == MovType.entree,
                        onClick = { type = MovType.entree }
                    )
                    Text("Entrée")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == MovType.sortie,
                        onClick = { type = MovType.sortie }
                    )
                    Text("Sortie")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu déroulant pour la catégorie
        ExposedDropdownMenuBox(
            expanded = expandedCategoryMenu,
            onExpandedChange = { expandedCategoryMenu = !expandedCategoryMenu }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Catégorie") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryMenu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedCategoryMenu,
                onDismissRequest = { expandedCategoryMenu = false }
            ) {
                categories.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedCategory = selectionOption
                            expandedCategoryMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val montantDecimal = montant.toBigDecimalOrNull()
                if (titre.isNotBlank() && montantDecimal != null && selectedCategory.isNotBlank()) {

                    val finalMontant = montantDecimal

                    viewModel.addMouvement(
                        titre = titre,
                        montant = finalMontant,
                        categorie = selectedCategory,
                        date = LocalDate.now(),
                        type = type
                    )
                    titre = ""
                    montant = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter le mouvement")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))


        LazyColumn {
            items(mouvements) { mouvement ->
                MouvementConfigItem(
                    mouvement = mouvement,
                    onDelete = { viewModel.delMouvement(mouvement) }
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

@Composable
fun MouvementConfigItem(mouvement: MoneyMovement, onDelete: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mouvement.titre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${mouvement.montant} € - ${mouvement.categorie}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer le mouvement", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}