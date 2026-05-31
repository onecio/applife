package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MindFitViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MindFitViewModel,
    onNavigateToTab: (Int) -> Unit, // Callback to switch active tabs in BottomNavigation
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val latestPlan by viewModel.latestDailyPlan.collectAsState()
    val checkins by viewModel.moodCheckins.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()

    // Temp Check-in state
    var selectedMood by remember { mutableStateOf("Calmo") }
    var userNotes by remember { mutableStateOf("") }
    var anxietyLevel by remember { mutableFloatStateOf(2.0f) }
    var energyLevel by remember { mutableFloatStateOf(3.0f) }
    var focusLevel by remember { mutableFloatStateOf(4.0f) }
    var showCheckinSuccessAlert by remember { mutableStateOf(false) }

    // Greet user dynamically based on time of day
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
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
        // 1. HEADER WELCOME
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, ${userProfile.name}!",
                        style = MaterialTheme.typography.displaySmall,
                        color = OnBackgroundText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Foco principal: ${userProfile.mainGoal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtextGrey
                    )
                }

                // Streak Flame icon indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .border(1.dp, DividerSlate, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${focusSessions.size + checkins.size} Dias",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumGold
                        )
                    }
                }
            }
        }

        // Premium Badge Header banner
        if (!userProfile.isPremium) {
            item {
                Card(
                    onClick = { onNavigateToTab(4) }, // Go to Perfil (Tab 4) containing Subscribe options
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PremiumGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Experimente o MindFit Premium",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PremiumGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Desbloqueie IA ilimitada, mais relatórios",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceText
                                )
                            }
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = "Upgrade", tint = PremiumGold)
                    }
                }
            }
        }

        // 2. IA COACH DAILY RECOMMENDATION
        item {
            MindFitCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SecondaryIndigo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Orientação do seu Coach",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBackgroundText
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = latestPlan.coachRecommendation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceText,
                    lineHeight = 22.sp
                )
            }
        }

        // 3. EMOTIONAL CHECK-IN CARD
        item {
            MindFitCard {
                Text(
                    text = "Como você se sente agora?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackgroundText
                )
                Spacer(modifier = Modifier.height(12.dp))

                MindFitMoodSelector(
                    selectedMood = selectedMood,
                    onMoodSelected = { selectedMood = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Energy slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nível de Foco", fontSize = 12.sp, color = SubtextGrey)
                    Text("${focusLevel.toInt()}/5", fontSize = 12.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = focusLevel,
                    onValueChange = { focusLevel = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(thumbColor = PrimaryTeal, activeTrackColor = PrimaryTeal)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nível de Ansiedade", fontSize = 12.sp, color = SubtextGrey)
                    Text("${anxietyLevel.toInt()}/5", fontSize = 12.sp, color = TertiaryRose, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = anxietyLevel,
                    onValueChange = { anxietyLevel = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(thumbColor = TertiaryRose, activeTrackColor = TertiaryRose)
                )

                OutlinedTextField(
                    value = userNotes,
                    onValueChange = { userNotes = it },
                    placeholder = { Text("Alguma observação opcional? Ex: Muito trabalho...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = DividerSlate,
                        focusedTextColor = OnBackgroundText,
                        unfocusedTextColor = OnBackgroundText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                MindFitPrimaryButton(
                    text = "Registrar Humor",
                    onClick = {
                        viewModel.submitMoodCheckin(
                            mood = selectedMood,
                            focus = focusLevel.toInt(),
                            anxiety = anxietyLevel.toInt(),
                            energy = energyLevel.toInt(),
                            notes = userNotes
                        )
                        userNotes = ""
                        showCheckinSuccessAlert = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. ACTIVE TARGETS / INTEGRATED ACTION PLAN
        item {
            MindFitCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Metas de Ação de Hoje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBackgroundText
                    )
                    Text(
                        text = "Plano Diário",
                        fontSize = 11.sp,
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToTab(2) } // Redirect to Foco (Tab 2)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Task 1
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = latestPlan.task1Done,
                            onCheckedChange = { viewModel.togglePlanTask(1, it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal)
                        )
                        Text(
                            text = latestPlan.task1,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (latestPlan.task1Done) SubtextGrey else OnSurfaceText,
                            textDecoration = if (latestPlan.task1Done) TextDecoration.LineThrough else null
                        )
                    }
                    // Task 2
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = latestPlan.task2Done,
                            onCheckedChange = { viewModel.togglePlanTask(2, it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal)
                        )
                        Text(
                            text = latestPlan.task2,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (latestPlan.task2Done) SubtextGrey else OnSurfaceText,
                            textDecoration = if (latestPlan.task2Done) TextDecoration.LineThrough else null
                        )
                    }
                    // Task 3
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = latestPlan.task3Done,
                            onCheckedChange = { viewModel.togglePlanTask(3, it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal)
                        )
                        Text(
                            text = latestPlan.task3,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (latestPlan.task3Done) SubtextGrey else OnSurfaceText,
                            textDecoration = if (latestPlan.task3Done) TextDecoration.LineThrough else null
                        )
                    }
                }
            }
        }

        // 5. QUICK ACTIONS FOR RESPIRAÇÃO & FOCO
        item {
            Text(
                "Ferramentas Rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackgroundText,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Focus session quick action
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, DividerSlate, RoundedCornerShape(14.dp)),
                    onClick = { onNavigateToTab(2) }, // Go to Foco View (Tab 2)
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("🎯", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Sessão de Foco",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackgroundText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Acione o Timer Pomodoro",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SubtextGrey
                        )
                    }
                }

                // Breathing exercise quick action
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, DividerSlate, RoundedCornerShape(14.dp)),
                    onClick = { onNavigateToTab(2) }, // Breathing and focus live in same tab 2
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("🌬️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Respiração",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackgroundText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Respire contra ansiedade",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SubtextGrey
                        )
                    }
                }
            }
        }
    }

    // Success alert logic
    if (showCheckinSuccessAlert) {
        AlertDialog(
            onDismissRequest = { showCheckinSuccessAlert = false },
            confirmButton = {
                Button(
                    onClick = { showCheckinSuccessAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Ótimo", color = OnPrimaryWhite)
                }
            },
            title = { Text("Acompanhamento Concluído", color = OnBackgroundText, fontWeight = FontWeight.Bold) },
            text = { Text("Seu check-in diário foi salvo com sucesso. Seu plano de hábitos com IA Coach já assimilou feedback e se adaptou!", color = OnSurfaceText) },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
