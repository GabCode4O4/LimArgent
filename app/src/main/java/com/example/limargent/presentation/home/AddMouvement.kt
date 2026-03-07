package com.example.limargent.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
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
import com.example.limargent.domain.model.Frequence
import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.model.MovType
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
    mouvementToEdit: MoneyMovement?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (MoneyMovement) -> Unit,
    onDelete: ((MoneyMovement) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        AddMouvementContent(
            mouvementToEdit = mouvementToEdit,
            categories = categories,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            onDelete = onDelete
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMouvementContent(
    mouvementToEdit: MoneyMovement?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (MoneyMovement) -> Unit,
    onDelete: ((MoneyMovement) -> Unit)?
) {
    val isEditing = mouvementToEdit != null
    val scrollState = rememberScrollState()

    var titre by remember { mutableStateOf(mouvementToEdit?.titre ?: "") }
    var montant by remember { mutableStateOf(mouvementToEdit?.montant?.toString() ?: "") }
    var type by remember { mutableStateOf(mouvementToEdit?.type ?: MovType.sortie) }
    var menuCategorieOuvert by remember { mutableStateOf(false) }
    var categorieSelectionnee by remember { mutableStateOf(mouvementToEdit?.categorie ?: categories.firstOrNull() ?: "") }
    var dateSelectionnee by remember { mutableStateOf(mouvementToEdit?.date ?: LocalDate.now()) }
    var afficherSelecteurDate by remember { mutableStateOf(false) }

    var isRecurrent by remember { mutableStateOf(mouvementToEdit?.isRecurrent ?: false) }
    var frequenceSelectionnee by remember { mutableStateOf(mouvementToEdit?.frequence ?: Frequence.MENSUEL) }
    var menuFrequenceOuvert by remember { mutableStateOf(false) }

    var dateTypeToSelect by remember { mutableStateOf(0) }
    var nextDate by remember { mutableStateOf(mouvementToEdit?.nextDate) }

    if (afficherSelecteurDate) {
        val dateInitiale = if (dateTypeToSelect == 0) dateSelectionnee else nextDate ?: LocalDate.now().plusDays(1)
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateInitiale.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            selectableDates = if (dateTypeToSelect == 0) maDate else object : SelectableDates {
                override fun isSelectableDate(time: Long): Boolean = time >= System.currentTimeMillis() - 86400000
            }
        )
        DatePickerDialog(
            onDismissRequest = { afficherSelecteurDate = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (dateTypeToSelect == 0) dateSelectionnee = selected else nextDate = selected
                    }
                    afficherSelecteurDate = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { afficherSelecteurDate = false }) { Text("Annuler") }
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
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEditing) "Modifier le Mouvement" else "Nouveau Mouvement",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Row {
                if (isEditing && onDelete != null) {
                    IconButton(onClick = {
                        mouvementToEdit?.let { onDelete(it) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
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
            onValueChange = { input ->
                val formatted = input.replace(',', '.')
                if (formatted.all { char -> char.isDigit() || char == '.' }) {
                    montant = formatted
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
                IconButton(onClick = { dateTypeToSelect = 0; afficherSelecteurDate = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Sélectionner une date")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateTypeToSelect = 0; afficherSelecteurDate = true },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mouvement récurrent",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Switch(checked = isRecurrent, onCheckedChange = { isRecurrent = it })
        }

        if (isRecurrent) {
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = menuFrequenceOuvert,
                onExpandedChange = { menuFrequenceOuvert = !menuFrequenceOuvert }
            ) {
                OutlinedTextField(
                    value = frequenceSelectionnee.libelle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fréquence") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuFrequenceOuvert) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = menuFrequenceOuvert,
                    onDismissRequest = { menuFrequenceOuvert = false }
                ) {
                    Frequence.entries.filter { it != Frequence.AUCUNE }.forEach { freq ->
                        DropdownMenuItem(
                            text = { Text(freq.libelle) },
                            onClick = {
                                frequenceSelectionnee = freq
                                menuFrequenceOuvert = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nextDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Sélectionner une date",
                onValueChange = {},
                readOnly = true,
                label = { Text("Prochaine exécution") },
                trailingIcon = {
                    IconButton(onClick = { dateTypeToSelect = 1; afficherSelecteurDate = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Sélectionner la date")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { dateTypeToSelect = 1; afficherSelecteurDate = true },
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val movement = MoneyMovement(
                    id = mouvementToEdit?.id,
                    titre = titre,
                    montant = montant.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    categorie = categorieSelectionnee,
                    date = dateSelectionnee,
                    type = type,
                    isRecurrent = isRecurrent,
                    frequence = if (isRecurrent) frequenceSelectionnee else Frequence.AUCUNE,
                    nextDate = if (isRecurrent) nextDate else null
                )
                onConfirm(movement)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isEditing) "Enregistrer" else "Ajouter",
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