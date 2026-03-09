package com.example.limargent.presentation.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limargent.domain.model.MoneyMovement
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.example.limargent.ui.theme.AppleRed
import com.example.limargent.ui.theme.AppleBlue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.limargent.presentation.util.LaTopAppBar
import com.example.limargent.presentation.util.SelectOnglet
import com.example.limargent.presentation.util.toDateFormat

private val DividerColor = Color.Gray.copy(alpha = 0.2f)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(viewModel: ConfigurationViewModel, onLogout: () -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val mouvementsRecurrents by viewModel.mouvementRec.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                LaTopAppBar("Configuration")
            }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SelectOnglet(
                    items = listOf("Préférences", "Catégories", "Récurrences"),
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { selectedTabIndex = it }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            when (selectedTabIndex) {
                // on affiche la section sélectionnée
                0 -> item { PreferenceSection(viewModel, onLogout) }
                1 -> item { CategorieSection(viewModel, categories) }
                2 -> {
                    if (mouvementsRecurrents.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                                Text("Aucun mouvement récurrent actif.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(mouvementsRecurrents) { mouvement ->
                            RecurrenceItem(
                                mouvement = mouvement,
                                onStopRecurrence = { viewModel.stopRecurrence(mouvement) },
                                onUpdateDate = { newDate -> viewModel.updateRecurrenceDate(mouvement, newDate) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceItem(
    mouvement: MoneyMovement,
    onStopRecurrence: () -> Unit,
    onUpdateDate: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = mouvement.nextDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        onUpdateDate(newDate)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = mouvement.titre, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${mouvement.montant} € • ${mouvement.frequence.libelle}", fontSize = 14.sp, color = AppleBlue, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Prochaine exécution : ${mouvement.nextDate?.toDateFormat() ?: "Inconnue"}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { showDatePicker = true }) {
            Icon(Icons.Default.DateRange, contentDescription = "Changer la date", tint = MaterialTheme.colorScheme.primary)
        }

        IconButton(onClick = onStopRecurrence) {
            Icon(Icons.Default.Block, contentDescription = "Arrêter la récurrence", tint = AppleRed)
        }
    }
}

@Composable
fun PreferenceSection(viewModel: ConfigurationViewModel, onLogout: () -> Unit) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()

    // on récupère le paramètre de thème du système du tél au cas où l'utilisateur choisit pas de thème
    val systemIsDark = isSystemInDarkTheme()
    val isDarkTheme = when (theme) {
        "dark" -> true
        "light" -> false
        else -> systemIsDark
    }
    val seuilBudget by viewModel.seuilBudget.collectAsStateWithLifecycle()
    val notificationActive by viewModel.notificationActive.collectAsStateWithLifecycle()
    var seuilInput by remember(seuilBudget) { mutableStateOf(seuilBudget.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Mode sombre", fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.setThemePreference(if (it) "dark" else "light") },
                    colors = SwitchDefaults.colors(checkedTrackColor = AppleBlue)
                )
            }

            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Alertes de budget", fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = notificationActive,
                    onCheckedChange = { viewModel.setNotificationActive(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = AppleBlue)
                )
            }

            AnimatedVisibility(
                visible = notificationActive,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Seuil d'alerte (€)", fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                        BasicTextField(
                            value = seuilInput,
                            onValueChange = {
                                seuilInput = it
                                it.toDoubleOrNull()?.let { value -> viewModel.setSeuilBudget(value) }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 17.sp, textAlign = TextAlign.End),
                            cursorBrush = SolidColor(AppleBlue),
                            modifier = Modifier.width(100.dp),
                            decorationBox = { innerTextField ->
                                if (seuilInput.isEmpty()) {
                                    Text("0.0", color = DividerColor, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    IconButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Se déconnecter",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun CategorieSection(viewModel: ConfigurationViewModel, categories: List<String>) {
    var newCat by remember { mutableStateOf("") }


    // Partie pour ajouter une nouvelle catégorie
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = newCat,
                    onValueChange = { newCat = it },
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp),
                    cursorBrush = SolidColor(AppleBlue),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (newCat.isEmpty()) {
                            Text("Nouvelle catégorie...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 17.sp)
                        }
                        innerTextField()
                    }
                )

                Text(
                    text = "Ajouter",
                    color = if (newCat.isNotBlank()) AppleBlue else DividerColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .clickable(enabled = newCat.isNotBlank()) {
                            viewModel.addCategorie(newCat)
                            newCat = ""
                        }
                        .padding(start = 16.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Liste des catégories existantes
    if (categories.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column {
                categories.forEachIndexed { index, category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = category, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = AppleRed,
                            modifier = Modifier.clickable { viewModel.delCategorie(category) }
                        )
                    }
                    if (index < categories.size - 1) {
                        HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
        }
    }
}
