package com.example.limargent.Presentation.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limargent.Domain.MovType
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
object maDate : SelectableDates {
    override fun isSelectableDate(time: Long): Boolean {
        return time <= System.currentTimeMillis()
    }

    override fun isSelectableYear(annee: Int): Boolean {
        return annee <= LocalDate.now().year
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMouvement(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (titre: String, montant: BigDecimal, categorie: String, date: LocalDate, type: MovType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        AddMouvementContent(
            categories = categories,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMouvementContent(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (titre: String, montant: BigDecimal, categorie: String, date: LocalDate, type: MovType) -> Unit
) {
    var titre by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MovType.sortie) }
    var menuCategorieOuvert by remember { mutableStateOf(false) }
    var categorieSelectionnee by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    var dateSelectionnee by remember { mutableStateOf(LocalDate.now()) }
    var afficherSelecteurDate by remember { mutableStateOf(false) }

    if (afficherSelecteurDate) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateSelectionnee.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            selectableDates = maDate
        )
        DatePickerDialog(
            onDismissRequest = { afficherSelecteurDate = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dateSelectionnee = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    afficherSelecteurDate = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { afficherSelecteurDate = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nouveau Mouvement",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Fermer")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabButton(
                text = "Dépense",
                isSelected = type == MovType.sortie,
                modifier = Modifier.weight(1f),
                onClick = { type = MovType.sortie }
            )
            TabButton(
                text = "Revenu",
                isSelected = type == MovType.entree,
                modifier = Modifier.weight(1f),
                onClick = { type = MovType.entree }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = titre,
            onValueChange = { titre = it },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = montant,
            onValueChange = { 
                if (it.all { char -> char.isDigit() || char == '.' }) {
                    montant = it
                }
            },
            label = { Text("Montant (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = menuCategorieOuvert,
            onExpandedChange = { menuCategorieOuvert = !menuCategorieOuvert }
        ) {
            OutlinedTextField(
                value = categorieSelectionnee,
                onValueChange = {},
                readOnly = true,
                label = { Text("Catégorie") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategorieOuvert) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = menuCategorieOuvert,
                onDismissRequest = { menuCategorieOuvert = false }
            ) {
                categories.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            categorieSelectionnee = selectionOption
                            menuCategorieOuvert = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dateSelectionnee.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            trailingIcon = {
                IconButton(onClick = { afficherSelecteurDate = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Sélectionner une date")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { afficherSelecteurDate = true },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val montantDecimal = montant.toBigDecimalOrNull()
                if (titre.isNotBlank() && montantDecimal != null && categorieSelectionnee.isNotBlank()) {
                    onConfirm(titre, montantDecimal, categorieSelectionnee, dateSelectionnee, type)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Ajouter",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}