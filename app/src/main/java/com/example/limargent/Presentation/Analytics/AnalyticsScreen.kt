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

enum class ModeAnalytique {
    DEPENSES,
    RECETTES
}

enum class OngletAnalytique {
    CAMEMBERT,
    COURBE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {

    var graphiqueActuel by remember { mutableStateOf(OngletAnalytique.CAMEMBERT) }

    // --- N'est nécessaire que pour l'onglet des piecharts je peux pas le mettre dans la fonction car j'en ai besoin pour la topbar
    var mode by remember { mutableStateOf(ModeAnalytique.DEPENSES) }
    var categorieSelectionnee by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            if (categorieSelectionnee == null && graphiqueActuel== OngletAnalytique.CAMEMBERT) {
                Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    TopAppBar(title = { Text("Analytique") })
                    BoutonsSegmentesAnalytique(
                        mode = mode,
                        onModeChange = { mode = it }
                    )
                }
            }
        },
        bottomBar = {
            BarreNavigationInferieure(
                graphiqueActuel = graphiqueActuel,
                onGraphChange = { graphiqueActuel = it }
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

            BoutonRetour(
                categorieSelectionnee = categorieSelectionnee,
                onClick = { categorieSelectionnee = null },
                modifier = Modifier.align(Alignment.Start)
            )
            categorieSelectionnee?.let { Text(text = it); Spacer(modifier = Modifier.height(16.dp)) }

            when (graphiqueActuel) {
                OngletAnalytique.CAMEMBERT -> OngletCamembert(viewModel=viewModel, mode=mode,categorieSelectionnee=categorieSelectionnee,onCategoryClick = { categorieSelectionnee = it })
                OngletAnalytique.COURBE -> OngletCourbe(viewModel)
            }

        }
    }
}

@Composable
fun OngletCamembert(
    viewModel: AnalyticsViewModel,
    mode: ModeAnalytique,
    categorieSelectionnee: String?,
    onCategoryClick: (String) -> Unit
) {
    val donnees = when {
        categorieSelectionnee != null -> viewModel.getCategoryData(categorieSelectionnee)
        mode == ModeAnalytique.DEPENSES -> viewModel.getCategoriesData(MovType.sortie)
        else -> viewModel.getCategoriesData(MovType.entree)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GraphiqueCamembertAnime(donnees = donnees)
        Spacer(modifier = Modifier.height(16.dp))

        val couleurs = obtenirCouleursGraphique(donnees.size)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(donnees.size) { index ->
                val entree = donnees.entries.elementAt(index)
                CarteItemCategorie(
                    nom = entree.key,
                    couleur = couleurs[index % couleurs.size],
                    onClick = { onCategoryClick(entree.key) }
                )
            }
        }
    }
}

/*@Composable
fun OngletCourbe(viewModel: AnalyticsViewModel) {
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
fun OngletCourbe(viewModel: AnalyticsViewModel) {
    Text("Graphique en courbe", color = Color.White)

    val donnees = viewModel.getBudgetByDateList()
    if (donnees.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp).offset(x=(-16).dp)) {
        val w = size.width
        val h = size.height
        val centreY = h / 2f

        val valeurAbsMax = donnees.maxOf { abs(it.second) }
        val echelleY = if (valeurAbsMax == 0f) 1f else (h / 2f) / valeurAbsMax

        val dateDebut = donnees.first().first
        val dateFin = donnees.last().first
        val joursTotal = max(ChronoUnit.DAYS.between(dateDebut, dateFin).toFloat(), 1f)

        val points = donnees.map { (date, valeur) ->
            val joursDepuisDebut = ChronoUnit.DAYS.between(dateDebut, date).toFloat()
            val x = (joursDepuisDebut / joursTotal) * w
            val y = centreY - valeur * echelleY
            Offset(x, y)
        }

        val chemin = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val milX = (p0.x + p1.x) / 2
                    val milY = (p0.y + p1.y) / 2
                    quadraticBezierTo(p0.x, p0.y, milX, milY)
                }
                lineTo(points.last().x, points.last().y)
            }
        }

        drawLine(
            color = Color.Gray,
            start = Offset(0f, centreY),
            end = Offset(w, centreY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        val derniereValeur = donnees.last().second
        drawPath(
            path = chemin,
            color = if (derniereValeur > 0f) Color.Green else Color.Red,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )

        val datesAxe = listOf(
            dateDebut,
            dateDebut.plusDays((joursTotal / 3f).toLong()),
            dateDebut.plusDays((2 * joursTotal / 3f).toLong()),
            dateFin
        )
        datesAxe.forEach { date ->
            val joursDepuisDebut = ChronoUnit.DAYS.between(dateDebut, date).toFloat()
            val x = if (joursDepuisDebut == 0f) (0.05*w).toFloat() else ((joursDepuisDebut / joursTotal) * w)
            val jour = date.dayOfMonth
            val mois = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "$jour $mois",
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
fun BoutonsSegmentesAnalytique(
    mode: ModeAnalytique,
    onModeChange: (ModeAnalytique) -> Unit
) {

    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = mode == ModeAnalytique.DEPENSES,
            onClick = { onModeChange(ModeAnalytique.DEPENSES) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("Dépenses")
        }

        SegmentedButton(
            selected = mode == ModeAnalytique.RECETTES,
            onClick = { onModeChange(ModeAnalytique.RECETTES) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Recettes")
        }
    }
}

@Composable
fun BarreNavigationInferieure(
    graphiqueActuel: OngletAnalytique,
    onGraphChange: (OngletAnalytique) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Camembert",
            color = if (graphiqueActuel == OngletAnalytique.CAMEMBERT) Color.Blue else Color.Black,
            modifier = Modifier.clickable { onGraphChange(OngletAnalytique.CAMEMBERT) }
        )
        Text(
            text = "Courbe",
            color = if (graphiqueActuel == OngletAnalytique.COURBE) Color.Blue else Color.Black,
            modifier = Modifier.clickable { onGraphChange(OngletAnalytique.COURBE) }
        )
    }
}

@Composable
fun BoutonRetour(
    categorieSelectionnee: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (categorieSelectionnee != null) {
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
fun GraphiqueCamembertAnime(donnees: Map<String, Float>) {

    Spacer(modifier = Modifier.height(32.dp))

    // Animation de progression de 0f à 1f
    val progression = remember { Animatable(0f) }
    LaunchedEffect(donnees) {
        progression.snapTo(0f)      // reset si les données changent
        progression.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val total = donnees.values.sum()
        val couleurs = obtenirCouleursGraphique(donnees.size)
        val epaisseur = size.minDimension / 8

        var angleDebut = -90f
        donnees.entries.forEachIndexed { index, entree ->
            val angleBalayage = (entree.value / total) * 360f * progression.value

            drawArc(
                color = couleurs[index % couleurs.size],
                startAngle = angleDebut,
                sweepAngle = angleBalayage,
                useCenter = false,
                style = Stroke(width = epaisseur)
            )

            angleDebut += (entree.value / total) * 360f
        }
    }

    Spacer(modifier = Modifier.height(48.dp))
}

@Composable
fun GraphiqueCamembertAnimeV2(donnees: Map<String, Float>) {

    Spacer(modifier = Modifier.height(32.dp))

    val progression = remember { Animatable(0f) }
    LaunchedEffect(donnees) {
        progression.snapTo(0f)
        progression.animateTo(
            1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val total = donnees.values.sum()
        val couleurs = obtenirCouleursGraphique(donnees.size)
        val epaisseur = size.minDimension / 8

        var angleDebut = -90f
        val ecart = 2f
        donnees.entries.forEachIndexed { index, entree ->
            val angleBalayage = (entree.value / total) * 360f * progression.value

            drawArc(
                color = couleurs[index % couleurs.size],
                startAngle = angleDebut,
                sweepAngle = angleBalayage - ecart,
                useCenter = false,
                style = Stroke(
                    width = epaisseur,
                    cap = StrokeCap.Round
                )
            )

            angleDebut += (entree.value / total) * 360f
        }
    }

    Spacer(modifier = Modifier.height(48.dp))
}

@Composable
fun CarteItemCategorie(
    nom: String,
    couleur: Color,
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
                    .background(couleur, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(nom)
        }
    }
}

fun obtenirCouleursGraphique(longueur:Int) : List<Color>{
    return List(longueur) { i ->
        val teinte = i * 360f / longueur       // couleurs bien séparées
        Color.hsv(teinte, 0.6f, 0.85f)         // pastel, pas néon
    }
}
