package com.example.limargent.Presentation.Analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.limargent.Domain.MovType
import com.example.limargent.Presentation.Dashboard.DashBoardViewModel
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val entrees = viewModel.getCategoriesData(MovType.entree)
    val sorties = viewModel.getCategoriesData(MovType.sortie)

    var currentData = sorties

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analytics") })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // PieChart en haut
            PieChart(data = sorties)

            Spacer(modifier = Modifier.height(32.dp))

            // Liste des catégories avec LazyColumn

            val colors=getChartColors(currentData.size)

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(currentData.size) { index ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = colors[index],
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier=Modifier.width(8.dp))
                        Text(text = currentData.entries.elementAt(index).key)
                    }
                }
            }
        }
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

fun getChartColors(length:Int) : List<Color>{
    return List(length) { i ->
        val hue = i * 360f / length       // couleurs bien séparées
        Color.hsv(hue, 0.6f, 0.85f)         // pastel, pas néon
    }
}
