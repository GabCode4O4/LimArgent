package com.example.limargent.presentation.objectif

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.limargent.domain.dataClasse.ObjectifEpargne
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectifScreen(
    viewModel: ObjectifViewModel = hiltViewModel()
) {
    val objectifs by viewModel.objectifs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = viewModel) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Objectifs d'épargne", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un objectif")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (objectifs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun objectif d'épargne défini.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    items(objectifs) { objectif ->
                        ObjectifItem(
                            objectif = objectif,
                            onUpdateEpargne = { montant ->
                                viewModel.addProgress(objectif.id, montant)
                            },
                            onDelete = {
                                viewModel.delete(objectif.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddObjectifDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nom, montant, date, taux ->
                val newObjectif = ObjectifEpargne(
                    id = UUID.randomUUID().toString(),
                    nom = nom,
                    montantCible = montant,
                    dateEcheance = date,
                    tauxInteret = taux
                )
                viewModel.saveObjectif(newObjectif)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ObjectifItem(
    objectif: ObjectifEpargne,
    onUpdateEpargne: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var showUpdateDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = objectif.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { objectif.progression },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (objectif.estAtteint) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            )
            if (objectif.tauxInteret > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Simulation : + ${"%.2f".format(objectif.estimation)} €/an (à ${objectif.tauxInteret}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${"%.2f".format(objectif.montantActuel)} € / ${"%.2f".format(objectif.montantCible)} €",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${(objectif.progression * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            objectif.dateEcheance?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Échéance : ${it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showUpdateDialog = true },
                modifier = Modifier.align(Alignment.End),
                enabled = !objectif.estAtteint
            ) {
                Text("Ajouter épargne")
            }
        }
    }

    if (showUpdateDialog) {
        UpdateEpargneDialog(
            onDismiss = { showUpdateDialog = false },
            onConfirm = { montant ->
                onUpdateEpargne(montant)
                showUpdateDialog = false
            }
        )
    }
}

@Composable
fun AddObjectifDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LocalDate?, Double) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf("") }
    var taux by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel objectif") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom de l'objectif") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = montant,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) montant = it },
                    label = { Text("Montant cible (€)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = taux,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) taux = it },
                    label = { Text("Taux d'intérêt annuel (%)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        nom,
                        montant.toDoubleOrNull() ?: 0.0,
                        null,
                        taux.toDoubleOrNull() ?: 0.0
                    )
                }
            ) { Text("Ajouter") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun UpdateEpargneDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var montant by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter à l'épargne") },
        text = {
            OutlinedTextField(
                value = montant,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) montant = it },
                label = { Text("Montant à ajouter (€)") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(montant.toDoubleOrNull() ?: 0.0)
                }
            ) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}