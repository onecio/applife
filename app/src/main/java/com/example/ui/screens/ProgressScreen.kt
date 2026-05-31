package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MindFitCard
import com.example.ui.components.MindFitMetricCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MindFitViewModel

@Composable
fun ProgressScreen(
    viewModel: MindFitViewModel,
    modifier: Modifier = Modifier
) {
    val checkins by viewModel.moodCheckins.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()
    val breathingSessions by viewModel.breathingSessions.collectAsState()

    // Metrics aggregates
    val totalFocusMins = remember(focusSessions) {
        focusSessions.sumOf { it.durationMinutes }
    }

    val moodCounts = remember(checkins) {
        val counts = mutableMapOf<String, Int>()
        checkins.forEach {
            counts[it.mood] = (counts[it.mood] ?: 0) + 1
        }
        counts
    }

    val dominantMood = remember(moodCounts) {
        if (moodCounts.isEmpty()) "Calmo" 
        else moodCounts.maxByOrNull { it.value }?.key ?: "Calmo"
    }

    // Dynamic AI Insight generator based on current statistics
    val weeklyAiInsight = remember(focusSessions, checkins) {
        if (focusSessions.isEmpty() && checkins.isEmpty()) {
            "Realize seu primeiro check-in de humor diário e inicie uma sessão de foco Pomodoro para calibrar nossos algoritmos de IA e receber seu relatório de bem-estar."
        } else {
            val totalMins = focusSessions.sumOf { it.durationMinutes }
            val focusGrade = if (totalMins > 60) "Excelente" else "Moderado"
            "Análise do Coach: Seus hábitos de foco estão com engajamento $focusGrade ($totalMins minutos ativos). O humor dominante desta semana foi '$dominantMood'. Percebemos que sua produtividade aumentou nos períodos pós-respiração guiada. Continue combinando o timer com as pausas conscientes."
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
    ) {
        // TOP GENERAL STATS ROW
        item {
            Text(
                "Métricas Globais",
                style = MaterialTheme.typography.displaySmall,
                color = OnBackgroundText,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Acompanhe metas comportamentais geradas por IA",
                style = MaterialTheme.typography.bodyMedium,
                color = SubtextGrey
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MindFitMetricCard(
                    label = "Foco Geral",
                    value = "${totalFocusMins}m",
                    icon = "⏱️",
                    modifier = Modifier.weight(1f)
                )

                MindFitMetricCard(
                    label = "Sessões",
                    value = "${focusSessions.size + breathingSessions.size}",
                    icon = "🔥",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // CANVAS FOCUS CHARTS (Semanas)
        item {
            MindFitCard {
                Text(
                    text = "Acompanhamento Semanal de Foco",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackgroundText
                )
                Text(
                    text = "Minutos de atividade por dia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtextGrey
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful, hand-drawn vector bar chart inside a Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val weeksDays = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom")
                        val focusMinutesMock = listOf(25f, 45f, 0f, 60f, 25f, 15f, 30f) // Simulated heights
                        val maxMinsValue = 70f

                        val spacingX = size.width / (weeksDays.size)
                        val barWidth = 24.dp.toPx()

                        // Draw background horizontal calibration lines
                        val stepY = size.height / 3.0f
                        for (i in 0..3) {
                            drawLine(
                                color = DividerSlate,
                                start = Offset(0f, i * stepY),
                                end = Offset(size.width, i * stepY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Draw bars
                        focusMinutesMock.forEachIndexed { idx, value ->
                            val barHeight = (value / maxMinsValue) * size.height
                            val posX = (idx * spacingX) + (spacingX / 2) - (barWidth / 2)
                            val posY = size.height - barHeight

                            drawRoundRect(
                                color = if (value > 30f) PrimaryTeal else SecondaryIndigo,
                                topLeft = Offset(posX, posY),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }
                }

                // Days Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom")
                    days.forEach { d ->
                        Text(
                            text = d,
                            fontSize = 11.sp,
                            color = SubtextGrey,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // MOOD DISTRIBUTION CHART
        item {
            MindFitCard {
                Text(
                    text = "Frequência de Humores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackgroundText
                )
                Text(
                    text = "Distribuição baseada em registros",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtextGrey
                )
                Spacer(modifier = Modifier.height(16.dp))

                val moods = listOf(
                    "Calmo" to 0.5f,
                    "Focado" to 0.3f,
                    "Ansioso" to 0.1f,
                    "Cansado" to 0.1f
                )

                // Beautiful structured progress rows to simulate charts cleanly
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    moods.forEach { (mood, percentage) ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mood, fontSize = 12.sp, color = OnSurfaceText, fontWeight = FontWeight.Bold)
                                Text("${(percentage * 100).toInt()}%", fontSize = 11.sp, color = SubtextGrey)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Progress bar indicator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DividerSlate)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(percentage)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (mood == "Calmo") PrimaryTeal
                                            else if (mood == "Ansioso") TertiaryRose
                                            else SecondaryIndigo
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. WEEKLY REPORT GENERATED BY IA
        item {
            MindFitCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PremiumGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📜", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Relatório de Desempenho Mental",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBackgroundText
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = weeklyAiInsight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceText,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
