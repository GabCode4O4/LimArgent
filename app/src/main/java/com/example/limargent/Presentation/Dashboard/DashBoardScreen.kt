package com.example.limargent.Presentation.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limargent.Domain.MoneyMovement
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen(viewModel: DashBoardViewModel) {
    val budget = viewModel.budget
    val mouvements = viewModel.mouvements.reversed()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LimArgent", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            BudgetViewer(budget = budget)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mouvements récents",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (mouvements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun mouvement récent", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(mouvements) { mouvement ->
                        MouvementList(mouvement = mouvement)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetViewer(budget: Double) {
    val isPositive = budget >= 0
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Solde Total",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${"%.2f".format(budget)} €",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun MouvementList(mouvement: MoneyMovement) {
    val isIncome = mouvement.montant >= BigDecimal.ZERO
    val amountColor = if (isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)
    val iconText = if (isIncome) "+" else "-"
    val iconBackgroundColor = amountColor.copy(alpha = 0.1f)
    val formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconText,
                color = amountColor,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mouvement.titre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${mouvement.categorie} • ${mouvement.date.format(formatDate)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${if (isIncome) "+" else ""}${mouvement.montant} €",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = amountColor
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashBoardPreview() {
    val testViewModel = DashBoardViewModel()
    testViewModel.manager.addMov(MoneyMovement("Courses E.Leclerc", BigDecimal("-54.20"), "Alimentation", java.time.LocalDate.now()))
    testViewModel.manager.addMov(MoneyMovement("Salaire Octobre", BigDecimal("2450.00"), "Revenus", java.time.LocalDate.now().minusDays(5)))
    testViewModel.loadData()

    MaterialTheme {
        DashBoardScreen(viewModel = testViewModel)
    }
}