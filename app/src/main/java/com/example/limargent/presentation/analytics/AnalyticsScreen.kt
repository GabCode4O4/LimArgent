package com.example.limargent.presentation.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limargent.domain.dataClasse.MovType
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.max

enum class AnalyticsMode { DEPENSES, RECETTES }

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    var mode by remember { mutableStateOf(AnalyticsMode.DEPENSES) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analyses",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            BudgetChartsTab(
                viewModel = viewModel,
                mode = mode,
                onModeChange = { mode = it },
                selectedCategory = selectedCategory,
                onCategoryClick = { selectedCategory = it },
                onBackClick = { selectedCategory = null }
            )

        }
    }
}

@Composable
fun BudgetChartsTab(
    viewModel: AnalyticsViewModel,
    mode: AnalyticsMode,
    onModeChange: (AnalyticsMode) -> Unit,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val mouvements by viewModel.mouvements.collectAsState()

    val data = remember(mouvements, mode, selectedCategory) {
        when {
            selectedCategory != null -> viewModel.getCategorieParCat(selectedCategory)
            mode == AnalyticsMode.DEPENSES -> viewModel.getCategorieParType(MovType.sortie)
            else -> viewModel.getCategorieParType(MovType.entree)
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            if (selectedCategory == null) {
                ChartCard {
                    Text(
                        text = "Évolution du solde",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LineChart(viewModel.getBudgetByDateList())
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onBackClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Retour", color = Color(0xFF007AFF), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(selectedCategory, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        item{
            if (selectedCategory == null) {
                Spacer(modifier = Modifier.height(32.dp))
                IOSSegmentedControl(
                    items = listOf("Dépenses", "Recettes"),
                    selectedIndex = if (mode == AnalyticsMode.DEPENSES) 0 else 1,
                    onItemSelected = { onModeChange(if (it == 0) AnalyticsMode.DEPENSES else AnalyticsMode.RECETTES) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            // Carte Donut Chart
            ChartCard {
                Text(
                    text = "Répartition",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedDonutChart(data = data)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        val colors = getPremiumColors(data.size)

        items(data.size) { index ->
            val entry = data.entries.elementAt(index)
            CleanCategoryRow(
                name = entry.key,
                amount = entry.value,
                color = colors[index % colors.size],
                onClick = { if (selectedCategory == null) onCategoryClick(entry.key) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}




@Composable
fun ChartCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp) // Flat design
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
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

@Composable
fun CleanCategoryRow(name: String, amount: Float, color: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Text(
            text = "${String.format("%.2f", amount)} €",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun AnimatedDonutChart(data: Map<String, Float>) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }

    val total = data.values.sum()
    val colors = getPremiumColors(data.size)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val thickness = 40f
            var startAngle = -90f
            val gap = if (data.size > 1) 3f else 0f

            data.entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / total) * 360f * progress.value
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle + (gap / 2),
                    sweepAngle = max(1f, sweepAngle - gap),
                    useCenter = false,
                    style = Stroke(width = thickness, cap = StrokeCap.Round)
                )
                startAngle += (entry.value / total) * 360f
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Text(
                text = "${total.toInt()} €",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LineChart(data: List<Pair<LocalDate, Float>>) {
    if (data.isEmpty()) {
        Text("Pas assez de données", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    val primaryColor = Color(0xFF007AFF) // Bleu Apple

    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val w = size.width
        val h = size.height
        val maxAbsValue = (data.maxOfOrNull { abs(it.second) } ?: 1f).takeIf { it > 0f } ?: 1f
        val scaleY = (h * 0.8f) / (maxAbsValue * 2)
        val centerY = h / 2f
        val startDate = data.first().first
        val endDate = data.last().first
        val totalDays = max(ChronoUnit.DAYS.between(startDate, endDate).toFloat(), 1f)

        val points = data.map { (date, value) ->
            val daysFromStart = ChronoUnit.DAYS.between(startDate, date).toFloat()
            val x = (daysFromStart / totalDays) * w
            val y = centerY - (value * scaleY)
            Offset(x, y)
        }

        // Ligne de zéro subtile
        drawLine(
            color = Color.LightGray.copy(alpha = 0.5f),
            start = Offset(0f, centerY),
            end = Offset(w, centerY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    quadraticTo(p0.x, p0.y, (p0.x + p1.x) / 2, (p0.y + p1.y) / 2)
                }
                lineTo(points.last().x, points.last().y)
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )
    }
}

// Génère une palette de couleurs douces et modernes (style iOS)
fun getPremiumColors(length: Int): List<Color> {
    val premiumPalette = listOf(
        Color(0xFF007AFF), // Blue
        Color(0xFF34C759), // Green
        Color(0xFFFF9500), // Orange
        Color(0xFFAF52DE), // Purple
        Color(0xFFFF2D55), // Pink
        Color(0xFF5856D6), // Indigo
        Color(0xFF5AC8FA)  // Teal
    )
    return List(length) { i -> premiumPalette[i % premiumPalette.size] }
}