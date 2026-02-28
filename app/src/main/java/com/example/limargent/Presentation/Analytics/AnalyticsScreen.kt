package com.example.limargent.Presentation.Analytics

import androidx.compose.foundation.Canvas
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val entrees = viewModel.getCategoriesData(MovType.entree)
    val sorties = viewModel.getCategoriesData(MovType.sortie)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analytics") })
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            PieChart(data = sorties)

        }
    }
}

@Composable
fun PieChart(data: Map<String, Float>) {
    Canvas(modifier = Modifier.size(200.dp)) {

        var startAngle = 0f
        val total = data.values.sum().takeIf { it > 0f } ?: 1f // Pour éviter la division par 0 si y'a pas de valeur
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2

        val colors = listOf(
            Color(0xFFFFC1E3),
            Color(0xFFFFA3D1),
            Color(0xFFFF85BF),
            Color(0xFFFF66AC),
            Color(0xFFFF4E9A)
        )

        data.entries.forEachIndexed { index, entry ->
            val sweepAngle = (entry.value / total) * 360f

            // Dessiner l'arc
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )

            // Position du texte : milieu de l’arc
            val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val textX = centerX + (radius / 2) * kotlin.math.cos(angleRad).toFloat()
            val textY = centerY + (radius / 2) * kotlin.math.sin(angleRad).toFloat()

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${entry.key}\n${(entry.value / total * 100).toInt()}%",
                    textX,
                    textY,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 30f
                    }
                )
            }

            startAngle += sweepAngle
        }
    }
}

/*@Composable
fun PieChart(data: Map<String, Float>) {
    Canvas(modifier = Modifier.size(200.dp)) {

        var startAngle = 0f
        val total = data.values.sum()

        data.forEach { (_, value) ->
            val sweepAngle = (value / total) * 360f

            drawArc(
                color = /* une couleur */,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )

            startAngle += sweepAngle
        }
    }
}*/