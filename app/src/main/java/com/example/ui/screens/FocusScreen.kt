package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MindFitCard
import com.example.ui.components.MindFitEmptyState
import com.example.ui.components.MindFitPrimaryButton
import com.example.ui.components.MindFitTimerCircle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MindFitViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: MindFitViewModel,
    modifier: Modifier = Modifier
) {
    // Top selector state: 0 for Foquing Session, 1 for Breathing Exercise
    var activeSubTab by remember { mutableIntStateOf(0) }

    // Focus State Flows
    val focusTimeLeft by viewModel.focusTimeLeft.collectAsState()
    val focusTargetMinutes by viewModel.focusTargetTime.collectAsState()
    val focusIsRunning by viewModel.focusIsRunning.collectAsState()
    val focusObjective by viewModel.focusObjective.collectAsState()
    val showFocusDialog by viewModel.showFocusCompletionDialog.collectAsState()
    val completedSessions by viewModel.focusSessions.collectAsState()

    // Breathing State Flows
    val breathingIsRunning by viewModel.breathingIsRunning.collectAsState()
    val breathingCycle by viewModel.breathingCycleState.collectAsState()
    val breathingScale by viewModel.breathingCircleScale.collectAsState()
    val breathingTimeLeft by viewModel.breathingTimeLeft.collectAsState()
    val showBreathingDialog by viewModel.showBreathingCompletionDialog.collectAsState()
    val breathingSessions by viewModel.breathingSessions.collectAsState()

    var customProductivityRating by remember { mutableFloatStateOf(4.0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // TOP TAB CHIPS SELECTOR BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, DividerSlate, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == 0) PrimaryTeal else Color.Transparent)
                    .clickable { activeSubTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Sessão de Foco 🎯",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 0) OnPrimaryWhite else OnSurfaceText
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == 1) PrimaryTeal else Color.Transparent)
                    .clickable { activeSubTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Respiração Guiada 🌬️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 1) OnPrimaryWhite else OnSurfaceText
                )
            }
        }

        // SCROLLABLE VIEWS
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            if (activeSubTab == 0) {
                // Focus session sub view
                item {
                    MindFitCard {
                        Text(
                            text = "Meta do seu Período de Atividade",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnBackgroundText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = focusObjective,
                            onValueChange = { viewModel.focusObjective.value = it },
                            placeholder = { Text("O que você vai focar agora?") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = DividerSlate,
                                focusedTextColor = OnBackgroundText,
                                unfocusedTextColor = OnBackgroundText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Interactive circular timer
                        val progress = if (focusTargetMinutes > 0) {
                            focusTimeLeft / (focusTargetMinutes * 60f)
                        } else 1.0f

                        val minutes = focusTimeLeft / 60
                        val seconds = focusTimeLeft % 60
                        val rawTimeStr = String.format("%02d:%02d", minutes, seconds)

                        MindFitTimerCircle(
                            progress = progress,
                            timeLeftString = rawTimeStr,
                            isRunning = focusIsRunning,
                            onToggleClick = { viewModel.toggleFocusTimer() },
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Duration Preset Chips (15m, 25m, 45m, 60m)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(15, 25, 45, 60).forEach { mins ->
                                val isSelected = focusTargetMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PrimaryTeal else SurfaceDark)
                                        .border(1.dp, DividerSlate, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (!focusIsRunning) {
                                                viewModel.selectFocusDuration(mins)
                                            }
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${mins}min",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) OnPrimaryWhite else OnSurfaceText
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Histórico de Foco Recente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBackgroundText,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                if (completedSessions.isEmpty()) {
                    item {
                        MindFitEmptyState(
                            emoji = "⏱️",
                            title = "Nenhuma sessão ainda",
                            description = "Seus blocos Pomodoro concluídos de foco intensivo aparecerão aqui com insights."
                        )
                    }
                } else {
                    items(completedSessions) { session ->
                        val dateString = remember(session.timestamp) {
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(session.timestamp))
                        }
                        MindFitCard(modifier = Modifier.padding(bottom = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = session.objective,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnBackgroundText,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$dateString • ${session.durationMinutes} minutos focados",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SubtextGrey
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryTeal.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "⭐ ${session.perceivedProductivity}/5",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryTeal
                                    )
                                }
                            }
                        }
                    }
                }

            } else {
                // Breathing Exercise sub view
                item {
                    MindFitCard {
                        Text(
                            text = "Técnica Quadrada de Respiração (4-4-4)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnBackgroundText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Excelente para baixar o estresse imediato de reuniões ou ansiedade acumulada. Inspire (4s), segure cheio (4s), expire (4s) e segure vazio (4s).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SubtextGrey,
                            lineHeight = 20.sp
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // GORGEOUS GROWING EXPANSIBLE CIRCLE CONTAINER FOR GUIDANCE
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal.copy(alpha = 0.05f))
                                .border(1.dp, DividerSlate, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Expansible dynamic circle animation
                            Box(
                                modifier = Modifier
                                    .size((80 * breathingScale).dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (breathingCycle == "Inspirar") PrimaryTeal
                                        else if (breathingCycle == "Expirar") SecondaryIndigo
                                        else SecondaryIndigo.copy(alpha = 0.40f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Pulsing icon
                                Text(
                                    text = if (breathingCycle == "Inspirar") "🌊" else "🍃",
                                    fontSize = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Breath instructions text
                        Text(
                            text = breathingCycle,
                            style = MaterialTheme.typography.displaySmall,
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val bMinutes = breathingTimeLeft / 60
                        val bSeconds = breathingTimeLeft % 60
                        val bRawStr = String.format("%02d:%02d", bMinutes, bSeconds)

                        Text(
                            text = bRawStr,
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackgroundText,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.toggleBreathing() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (breathingIsRunning) TertiaryRose else PrimaryTeal
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp).width(180.dp)
                        ) {
                            Text(
                                text = if (breathingIsRunning) "Pausar Sessão" else "Parar / Iniciar",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnPrimaryWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Práticas de Respiração Recentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBackgroundText,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                if (breathingSessions.isEmpty()) {
                    item {
                        MindFitEmptyState(
                            emoji = "🌬️",
                            title = "Nenhuma prática concluída",
                            description = "Suas atividades relaxantes completas com o medidor de pulmão aparecerão aqui."
                        )
                    }
                } else {
                    items(breathingSessions) { s ->
                        val dateString = remember(s.timestamp) {
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(s.timestamp))
                        }
                        MindFitCard(modifier = Modifier.padding(bottom = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = s.type,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnBackgroundText,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$dateString • ${s.durationSeconds} segundos respirados",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SubtextGrey
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecondaryIndigo.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "🌬️ Concluído",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryIndigo
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal popup completion Dialogs
    // 1. For Focus timer completes
    if (showFocusDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showFocusCompletionDialog.value = false },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveCompletedFocusSession(customProductivityRating.toInt()) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Salvar Sessão", color = OnPrimaryWhite)
                }
            },
            title = {
                Text(
                    "Sessão de Foco Concluída!",
                    color = OnBackgroundText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Fim do timer! Ótimo empenho em resgatar clareza mental.", color = OnSurfaceText, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Classifique seu foco (1 a 5 estrelas):", fontSize = 12.sp, color = SubtextGrey)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${customProductivityRating.toInt()} Estrelas", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Slider(
                        value = customProductivityRating,
                        onValueChange = { customProductivityRating = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(activeTrackColor = PrimaryTeal, thumbColor = PrimaryTeal)
                    )
                }
            },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 2. For Breathing completes
    if (showBreathingDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showBreathingCompletionDialog.value = false },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveCompletedBreathingSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Registrar Prática", color = OnPrimaryWhite)
                }
            },
            title = {
                Text(
                    "Excelente Sessão Concluída!",
                    color = OnBackgroundText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "Seus batimentos cardíacos devem ter desacelerado. Registrar essa sessão ajuda a calibrar as indicações comportamentais do seu Coach mental diário com IA.",
                    color = OnSurfaceText,
                    textAlign = TextAlign.Center
                )
            },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
