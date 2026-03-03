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
        }
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

@Composable
fun LineChartsTab(viewModel: AnalyticsViewModel) {
    Text("Graphique en courbe (67)")
    // TODO https://medium.com/@yilmazvolkan/kotlinlinecharts-c2a730226ff1
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
