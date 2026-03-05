package com.example.limargent.Presentation.Configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppleBlue = Color(0xFF007AFF)
private val AppleRed = Color(0xFFFF3B30)
private val DividerColor = Color.Gray.copy(alpha = 0.2f)

@Composable
fun ConfigurationScreen(viewModel: ConfigurationViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
        ) {
            item {
                Text(
                    text = "Réglages",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                IOSSegmentedControl(
                    items = listOf("Préférences", "Catégories"),
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { selectedTabIndex = it }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (selectedTabIndex == 0) {
                item { PreferenceSection(viewModel) }
            } else {
                item { CategorieSection(viewModel) }
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

    regCard {
        regRow(label = "Mode sombre") {
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.setThemePreference(if (it) "dark" else "light") },
                colors = SwitchDefaults.colors(checkedTrackColor = AppleBlue)
            )
        }

        HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 16.dp))

        regRow(label = "Alertes de budget") {
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
                regRow(label = "Seuil d'alerte (€)") {
                    BasicTextField(
                        value = seuilInput,
                        onValueChange = {
                            seuilInput = it
                            it.toDoubleOrNull()?.let { value -> viewModel.setSeuilBudget(value) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 17.sp,
                            textAlign = TextAlign.End
                        ),
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

@Composable
fun CategorieSection(viewModel: ConfigurationViewModel) {
    val categories by viewModel.categories.collectAsState()
    var newCat by remember { mutableStateOf("") }

    regCard {
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

    Spacer(modifier = Modifier.height(24.dp))

    if (categories.isNotEmpty()) {
        regCard {
            categories.forEachIndexed { index, category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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

@Composable
fun regCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun regRow(label: String, trailingContent: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        trailingContent()
    }
}

@Composable
fun IOSSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEachIndexed { index, text ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onItemSelected(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}