package com.example.limargent.Presentation.Analytics

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

enum class AnalyticsMode {
    DEPENSES,
    RECETTES
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {

    //var currentGraph by remember { mutableStateOf("categories") } TODO

    var mode by remember { mutableStateOf(AnalyticsMode.DEPENSES) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Récupération des données selon le mode et catégorie
    val data = when {
        selectedCategory != null -> viewModel.getCategoryData(selectedCategory!!)
        mode == AnalyticsMode.DEPENSES -> viewModel.getCategoriesData(MovType.sortie)
        else -> viewModel.getCategoriesData(MovType.entree)
    }

    Scaffold(
        topBar = {
            if (selectedCategory == null) {
                Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    TopAppBar(title = { Text("Analytics") })
                    AnalyticsSegmentedButtons(
                        mode = mode,
                        onModeChange = { mode = it }
                    )
                }
            }
        },
        /*bottomBar = {
            BottomNavigationBar(
                currentGraph = currentGraph,
                onGraphChange = { currentGraph = it }
            )
        }*/
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            BoutonRetour(
                selectedCategory = selectedCategory,
                onClick = { selectedCategory = null },
                modifier = Modifier.align(Alignment.Start)
            )

            selectedCategory?.let { Text(text = it) }

            Spacer(modifier = Modifier.height(32.dp))

            // PieChart en haut
            PieChart(data = data)


            Spacer(modifier = Modifier.height(32.dp))

            // Liste des catégories avec LazyColumn

            val colors=getChartColors(data.size)

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(data.size) { index ->
                    val entry = data.entries.elementAt(index)

                    CategoryItemCard(
                        name = entry.key,
                        color = colors[index % colors.size],
                        onClick = {
                            // action au clic
                            selectedCategory = entry.key
                        }
                    )
                }
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
fun PieChart(data: Map<String, Float>) {
    Canvas(modifier = Modifier.size(200.dp)
    ) {


        var startAngle = -90f
        val total = data.values.sum()
        val colors = getChartColors(data.size)

        val thickness = size.minDimension / 8

        data.entries.forEachIndexed { index, entry ->
            val sweepAngle = (entry.value / total) * 360f

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness)
            )

            startAngle += sweepAngle
        }
    }
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
