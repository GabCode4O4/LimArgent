package com.example.limargent.presentation.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limargent.domain.dataClasse.MoneyMovement
import com.example.limargent.domain.dataClasse.MovType
import com.example.limargent.presentation.main.LogoLimArgent
import com.example.limargent.ui.theme.AppleGreen
import com.example.limargent.ui.theme.AppleRed
import java.time.format.DateTimeFormatter
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val userName by viewModel.userName.collectAsState()
    val budget = viewModel.budget
    val mouvements = viewModel.filtrage
    val context = LocalContext.current
    val mouvementEnEdition = viewModel.mouvementEnEdition

    if (mouvementEnEdition != null) {
        AddMouvement(
            mouvementToEdit = mouvementEnEdition,
            categories = viewModel.categories,
            onDismiss = { viewModel.onDismissMouvement() },
            onConfirm = { mouvement ->
                viewModel.saveMouvement(mouvement)
            },
            onDelete = { mouvement ->
                viewModel.delMouvement(mouvement)
                viewModel.onDismissMouvement()
            }
        )
    }


    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
    }

    var lesFiltres by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {LogoLimArgent(modifier = Modifier.fillMaxWidth(0.4f).aspectRatio(1f)
                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bonjour, $userName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )


                BalanceCard(budget = budget)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mes mouvements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = { lesFiltres = !lesFiltres }) {
                        Icon(
                            imageVector = if (lesFiltres) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = "Filtres",
                            tint = if (lesFiltres || viewModel.recherche.isNotBlank() || viewModel.moisFiltre != null)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = lesFiltres,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        OutlinedTextField(
                            value = viewModel.recherche,
                            onValueChange = { viewModel.recherche = it },
                            placeholder = { Text("Rechercher un mouvement...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (viewModel.recherche.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.recherche = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Filtrer(
                                modifier = Modifier.weight(1f),
                                label = "Type",
                                options = listOf("Tous", "Entrée", "Sortie"),
                                selectedOption = when(viewModel.typeFiltre) {
                                    MovType.entree -> "Entrée"
                                    MovType.sortie -> "Sortie"
                                    null -> "Tous"
                                },
                                onOptionSelected = {
                                    viewModel.typeFiltre = when(it) {
                                        "Entrée" -> MovType.entree
                                        "Sortie" -> MovType.sortie
                                        else -> null
                                    }
                                }
                            )

                            val categoryOptions = listOf("Toutes") + viewModel.categories
                            Filtrer(
                                modifier = Modifier.weight(1f),
                                label = "Catégorie",
                                options = categoryOptions,
                                selectedOption = viewModel.catFiltre ?: "Toutes",
                                onOptionSelected = {
                                    viewModel.catFiltre = if (it == "Toutes") null else it
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val months = listOf("Tous") + Month.entries.map { it.getDisplayName(TextStyle.FULL, Locale.FRANCE).replaceFirstChar { char -> char.uppercase() } }
                            Filtrer(
                                modifier = Modifier.weight(1f),
                                label = "Mois",
                                options = months,
                                selectedOption = viewModel.moisFiltre?.let { months[it] } ?: "Tous",
                                onOptionSelected = {
                                    viewModel.moisFiltre = if (it == "Tous") null else months.indexOf(it)
                                }
                            )

                            val availableYears = listOf("Toutes") + viewModel.mouvements.map { it.date.year }.distinct().sortedDescending().map { it.toString() }
                            Filtrer(
                                modifier = Modifier.weight(1f),
                                label = "Année",
                                options = availableYears,
                                selectedOption = viewModel.anneeFiltre?.toString() ?: "Toutes",
                                onOptionSelected = {
                                    viewModel.anneeFiltre = if (it == "Toutes") null else it.toIntOrNull()
                                }
                            )
                        }

                        if (viewModel.typeFiltre != null || viewModel.catFiltre != null || viewModel.moisFiltre != null || viewModel.anneeFiltre != null || viewModel.recherche.isNotBlank()) {
                            TextButton(
                                onClick = { viewModel.resetFilters() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Réinitialiser les filtres", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            if (mouvements.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (viewModel.mouvements.isEmpty()) "Aucun mouvement" else "Aucun résultat pour ces filtres",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(
                    items = mouvements,
                    key = { it.id ?: it.hashCode() }
                ) { mouvement ->
                    MouvementItem(
                        mouvement = mouvement,
                        onClick = { viewModel.onEditMouvement(mouvement) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filtrer(
    modifier: Modifier = Modifier,
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun BalanceCard(budget: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Solde Total",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${"%.2f".format(budget)} €",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MouvementItem(mouvement: MoneyMovement, onClick: () -> Unit) {
    val isIncome = mouvement.type == MovType.entree
    val amountColor = if (isIncome) AppleGreen else AppleRed
    val formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mouvement.titre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${mouvement.categorie} • ${mouvement.date.format(formatDate)}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${if (isIncome) "+" else "-"}${mouvement.montant} €",
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = amountColor
        )
    }
}