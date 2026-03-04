package com.example.limargent.Presentation.Analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.limargent.Domain.MovType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

enum class AnalyticsMode {
    DEPENSES,
    RECETTES
}

enum class AnalyticsTab {
    PIECHARTS,
    LINECHARTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {

    var currentGraph by remember { mutableStateOf(AnalyticsTab.PIECHARTS) }

    // --- N'est nécessaire que pour l'onglet des piecharts je peux pas le mettre dans la fonction car j'en ai besoin pour la topbar
    var mode by remember { mutableStateOf(AnalyticsMode.DEPENSES) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    // ----------------------------------------------------------------------------

    Scaffold(
        topBar = {
            if (selectedCategory == null && currentGraph== AnalyticsTab.PIECHARTS) {
                Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    TopAppBar(title = { Text("Analytics") })
                    AnalyticsSegmentedButtons(
                        mode = mode,
                        onModeChange = { mode = it }
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentGraph = currentGraph,
                onGraphChange = { currentGraph = it }
            )
        },
        containerColor = Color(0xFF1A1D21)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- N'apparait que quand on est dans un graphique de catégorie
            BoutonRetour(
                selectedCategory = selectedCategory,
                onClick = { selectedCategory = null },
                modifier = Modifier.align(Alignment.Start)
            )
            selectedCategory?.let { Text(text = it); Spacer(modifier = Modifier.height(16.dp)) }
            // --------------------------------------------------------------

            // Affichage des graphique selon le type de graphe choisi en onglet
            when (currentGraph) {
                AnalyticsTab.PIECHARTS -> PieChartsTab(viewModel=viewModel, mode=mode,selectedCategory=selectedCategory,onCategoryClick = { selectedCategory = it })
                AnalyticsTab.LINECHARTS -> LineChartsTab(viewModel) // ton autre graphique
            }

        }
    }
}

@Composable
fun PieChartsTab(
    viewModel: AnalyticsViewModel,
    mode: AnalyticsMode,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    // Récupérer les données ici selon le mode et la catégorie
    val data = when {
        selectedCategory != null -> viewModel.getCategoryData(selectedCategory)
        mode == AnalyticsMode.DEPENSES -> viewModel.getCategoriesData(MovType.sortie)
        else -> viewModel.getCategoriesData(MovType.entree)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // PieChart
        AnimatedPieChart(data = data)

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des catégories
        val colors = getChartColors(data.size)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(data.size) { index ->
                val entry = data.entries.elementAt(index)
                CategoryItemCard(
                    name = entry.key,
                    color = colors[index % colors.size],
                    onClick = { onCategoryClick(entry.key) }
                )
            }
        }
    }
}

/*@Composable
fun LineChartsTab(viewModel: AnalyticsViewModel) {
    Text("Graphique en courbe (67)", color=Color.White)

    val data = viewModel.getBudgetByDateList()

    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        val w = size.width
        val h = size.height
        val centerY = h / 2f

        // Calcul de l'échelle verticale
        val maxAbsValue = data.maxOf { abs(it.second) }
        val scaleY = if (maxAbsValue == 0f) 1f else (h / 2f) / maxAbsValue

        // Dates
        val startDate = data.first().first
        val endDate = data.last().first
        val totalDays = max(ChronoUnit.DAYS.between(startDate, endDate).toFloat(), 1f)

        // Calcul des points
        val points = data.map { (date, value) ->
            val daysFromStart = ChronoUnit.DAYS.between(startDate, date).toFloat()
            val x = (daysFromStart / totalDays) * w
            val y = centerY - value * scaleY
            Offset(x, y)
        }

        // Chemin de la courbe
        val path = Path().apply {
            points.forEachIndexed { index, offset ->
                if (index == 0) moveTo(offset.x, offset.y)
                else lineTo(offset.x, offset.y)
            }
        }

        // Dessiner la barre du milieu (0)
        drawLine(
            color = Color.Gray,
            start = Offset(0f, centerY),
            end = Offset(w, centerY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        // Dessiner la courbe
        val lastValue = data.last().second
        drawPath(
            path = path,
            color = if (lastValue > 0f) Color.Green else Color.Red,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // Axe des X (4 dates)
        val axisDates = listOf(
            startDate,
            startDate.plusDays((totalDays / 3f).toLong()),
            startDate.plusDays((2 * totalDays / 3f).toLong()),
            endDate
        )
        axisDates.forEach { date ->
            val daysFromStart = ChronoUnit.DAYS.between(startDate, date).toFloat()
            val x = (daysFromStart / totalDays) * w
            val day = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "$day $month",
                    x,
                    h + 20f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 30f
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
}*/

@Composable
fun LineChartsTab(viewModel: AnalyticsViewModel) {
    Text("Graphique en courbe (67)", color = Color.White)

    val data = viewModel.getBudgetByDateList()
    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp).offset(x=(-16).dp)) {
        val w = size.width
        val h = size.height
        val centerY = h / 2f

        // Échelle verticale
        val maxAbsValue = data.maxOf { abs(it.second) }
        val scaleY = if (maxAbsValue == 0f) 1f else (h / 2f) / maxAbsValue

        // Dates
        val startDate = data.first().first
        val endDate = data.last().first
        val totalDays = max(ChronoUnit.DAYS.between(startDate, endDate).toFloat(), 1f)

        // Calcul des points
        val points = data.map { (date, value) ->
            val daysFromStart = ChronoUnit.DAYS.between(startDate, date).toFloat()
            val x = (daysFromStart / totalDays) * w
            val y = centerY - value * scaleY
            Offset(x, y)
        }

        // --- Path Bézier pour lisser la courbe ---
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val midX = (p0.x + p1.x) / 2
                    val midY = (p0.y + p1.y) / 2
                    quadraticBezierTo(p0.x, p0.y, midX, midY)
                }
                // relier le dernier point exactement
                lineTo(points.last().x, points.last().y)
            }
        }

        // Barre du milieu (0)
        drawLine(
            color = Color.Gray,
            start = Offset(0f, centerY),
            end = Offset(w, centerY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        // Dessiner la courbe avec cap arrondi
        val lastValue = data.last().second
        drawPath(
            path = path,
            color = if (lastValue > 0f) Color.Green else Color.Red,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )

        // Axe X avec 4 dates
        val axisDates = listOf(
            startDate,
            startDate.plusDays((totalDays / 3f).toLong()),
            startDate.plusDays((2 * totalDays / 3f).toLong()),
            endDate
        )
        axisDates.forEach { date ->
            val daysFromStart = ChronoUnit.DAYS.between(startDate, date).toFloat()
            val x = if (daysFromStart == 0f) (0.05*w).toFloat() else ((daysFromStart / totalDays) * w)
            val day = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "$day $month",
                    x,
                    h + 20f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 30f
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
}

@Composable
fun AnalyticsSegmentedButtons(
    mode: AnalyticsMode,
    onModeChange: (AnalyticsMode) -> Unit
) {

    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = mode == AnalyticsMode.DEPENSES,
            onClick = { onModeChange(AnalyticsMode.DEPENSES) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("Dépenses")
        }

        SegmentedButton(
            selected = mode == AnalyticsMode.RECETTES,
            onClick = { onModeChange(AnalyticsMode.RECETTES) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Recettes")
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentGraph: AnalyticsTab,
    onGraphChange: (AnalyticsTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Piecharts",
            color = if (currentGraph == AnalyticsTab.PIECHARTS) Color.Blue else Color.Black,
            modifier = Modifier.clickable { onGraphChange(AnalyticsTab.PIECHARTS) }
        )
        Text(
            text = "Linecharts",
            color = if (currentGraph == AnalyticsTab.LINECHARTS) Color.Blue else Color.Black,
            modifier = Modifier.clickable { onGraphChange(AnalyticsTab.LINECHARTS) }
        )
    }
}

@Composable
fun BoutonRetour(
    selectedCategory: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedCategory != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Retour",
            modifier = modifier.clickable { onClick() },
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AnimatedPieChart(data: Map<String, Float>) {

    Spacer(modifier = Modifier.height(32.dp))

    // Animation de progression de 0f à 1f
    val progress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        progress.snapTo(0f)      // reset si les données changent
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val total = data.values.sum()
        val colors = getChartColors(data.size)
        val thickness = size.minDimension / 8

        var startAngle = -90f
        data.entries.forEachIndexed { index, entry ->
            val sweepAngle = (entry.value / total) * 360f * progress.value

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness)
            )

            startAngle += (entry.value / total) * 360f
        }
    }

    Spacer(modifier = Modifier.height(48.dp))
}

@Composable
fun AnimatedPieChartV2(data: Map<String, Float>) {

    Spacer(modifier = Modifier.height(32.dp))

    val progress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        progress.snapTo(0f)
        progress.animateTo(
            1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val total = data.values.sum()
        val colors = getChartColors(data.size)
        val thickness = size.minDimension / 8

        var startAngle = -90f
        val gap = 2f
        data.entries.forEachIndexed { index, entry ->
            val sweepAngle = (entry.value / total) * 360f * progress.value

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle - gap,
                useCenter = false,
                style = Stroke(
                    width = thickness,
                    cap = StrokeCap.Round
                )
            )

            startAngle += (entry.value / total) * 360f
        }
    }

    Spacer(modifier = Modifier.height(48.dp))
}

@Composable
fun CategoryItemCard(
    name: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(name)
        }
    }
}

fun getChartColors(length:Int) : List<Color>{
    return List(length) { i ->
        val hue = i * 360f / length       // couleurs bien séparées
        Color.hsv(hue, 0.6f, 0.85f)         // pastel, pas néon
    }
}
