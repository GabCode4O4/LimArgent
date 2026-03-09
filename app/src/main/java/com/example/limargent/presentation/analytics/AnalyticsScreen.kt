package com.example.limargent.presentation.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.limargent.domain.model.MovType
import com.example.limargent.presentation.util.LaTopAppBar
import com.example.limargent.presentation.util.SelectOnglet
import com.example.limargent.presentation.util.toArgent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

// --- IMPORTS VICO ---
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

enum class ModeAnalyse { DEPENSES, RECETTES }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    var mode by remember { mutableStateOf(ModeAnalyse.DEPENSES) }
    var selectedCategorie by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LaTopAppBar("Analyse")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            OngletGraphBudget(
                viewModel = viewModel,
                mode = mode,
                auChangementDeMode = { mode = it },
                selectedCategorie = selectedCategorie,
                auClicCategorie = { selectedCategorie = it },
                auClicRetour = { selectedCategorie = null }
            )

        }
    }
}

@Composable
fun OngletGraphBudget(
    viewModel: AnalyticsViewModel,
    mode: ModeAnalyse,
    auChangementDeMode: (ModeAnalyse) -> Unit,
    selectedCategorie: String?,
    auClicCategorie: (String) -> Unit,
    auClicRetour: () -> Unit
) {
    val mouvements by viewModel.mouvements.collectAsStateWithLifecycle()

    val donnees = remember(mouvements, mode, selectedCategorie) {
        when {
            selectedCategorie != null -> viewModel.getCategorieParCat(selectedCategorie)
            mode == ModeAnalyse.DEPENSES -> viewModel.getCategorieParType(MovType.sortie)
            else -> viewModel.getCategorieParType(MovType.entree)
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            if (selectedCategorie == null) {
                GraphCard {
                    Text(
                        text = "Évolution du solde",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GraphLineaire(donnees = viewModel.getBudgetByDateList())
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { auClicRetour() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Retour", color = Color(0xFF007AFF), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(selectedCategorie, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        item {
            if (selectedCategorie == null) {
                Spacer(modifier = Modifier.height(32.dp))
                SelectOnglet(
                    items = listOf("Dépenses", "Recettes"),
                    selectedIndex = if (mode == ModeAnalyse.DEPENSES) 0 else 1,
                    onItemSelected = { auChangementDeMode(if (it == 0) ModeAnalyse.DEPENSES else ModeAnalyse.RECETTES) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            GraphCard {
                Text(
                    text = "Répartition",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                donut(donnees = donnees)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        val couleurs = getCouleur(donnees.size)

        items(donnees.size) { index ->
            val entree = donnees.entries.elementAt(index)
            categorie(
                nom = entree.key,
                montant = entree.value,
                couleur = couleurs[index % couleurs.size],
                auClic = { if (selectedCategorie == null) auClicCategorie(entree.key) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun GraphCard(contenu: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = contenu
        )
    }
}


@Composable
fun categorie(nom: String, montant: Float, couleur: Color, auClic: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .clickable { auClic() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(couleur, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = nom,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Text(
            text = montant.toArgent(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun donut(donnees: Map<String, Float>) {
    val progression = remember { Animatable(0f) }
    LaunchedEffect(donnees) {
        progression.snapTo(0f)
        progression.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }

    val total = donnees.values.sum()
    val couleurs = getCouleur(donnees.size)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val epaisseur = 40f
            var angleDepart = -90f
            val ecart = if (donnees.size > 1) 3f else 0f

            donnees.entries.forEachIndexed { index, entree ->
                val angleBalayage = (entree.value / total) * 360f * progression.value
                drawArc(
                    color = couleurs[index % couleurs.size],
                    startAngle = angleDepart + (ecart / 2),
                    sweepAngle = max(1f, angleBalayage - ecart),
                    useCenter = false,
                    style = Stroke(width = epaisseur, cap = StrokeCap.Round)
                )
                angleDepart += (entree.value / total) * 360f
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
fun GraphLineaire(donnees: List<Pair<LocalDate, Float>>) {
    if (donnees.isEmpty()) {
        Text("Pas assez de données", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    val entreGraph = donnees.mapIndexed { index, pair ->
        entryOf(x = index.toFloat(), y = pair.second)
    }
    val modelGraph = entryModelOf(entreGraph)

    val formatDate = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { valeur, _ ->
        val index = valeur.toInt()
        if (index >= 0 && index < donnees.size) {
            donnees[index].first.format(DateTimeFormatter.ofPattern("dd/MM"))
        } else {
            ""
        }
    }
    val couleurTexteAxe = MaterialTheme.colorScheme.onSurfaceVariant
    val couleurLigneAxe = MaterialTheme.colorScheme.surfaceVariant

    ProvideChartStyle(m3ChartStyle()) {
        Chart(
            chart = lineChart(),
            model = modelGraph,
            startAxis = rememberStartAxis(
                label = axisLabelComponent(color = couleurTexteAxe),
                axis = lineComponent(color = couleurTexteAxe, thickness = 1.dp),
                guideline = lineComponent(color = couleurLigneAxe, thickness = 1.dp)
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = formatDate,
                label = axisLabelComponent(color = couleurTexteAxe),
                axis = lineComponent(color = couleurTexteAxe, thickness = 1.dp),
                guideline = null
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
fun getCouleur(taille: Int): List<Color> {
    val palette = listOf(
        Color(0xFF007AFF),
        Color(0xFF34C759),
        Color(0xFFFF9500),
        Color(0xFFAF52DE),
        Color(0xFFFF2D55),
        Color(0xFF5856D6),
        Color(0xFF5AC8FA)
    )
    return List(taille) { i -> palette[i % palette.size] }
}