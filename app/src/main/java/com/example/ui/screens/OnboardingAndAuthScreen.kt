package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.components.MindFitPrimaryButton
import com.example.ui.components.MindFitSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MindFitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingAndAuthScreen(
    viewModel: MindFitViewModel,
    modifier: Modifier = Modifier
) {
    val step by viewModel.onboardingStep.collectAsState()

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userGoal by remember { mutableStateOf("Foco & Clareza Mental") }
    var userNotificationTime by remember { mutableStateOf("09:00") }

    var isSignUpMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MINDFIT",
                style = MaterialTheme.typography.displaySmall,
                color = OnBackgroundText,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        // STEP CONTENT
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (step) {
                1 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🧠", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Clareza Mental Diária",
                            style = MaterialTheme.typography.displayMedium,
                            color = OnBackgroundText,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Desenvolva foco imbatível e controle a ansiedade leve através de check-ins estruturados, sessões de foco silenciosas e relatórios com IA.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SubtextGrey,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
                2 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🤖", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "IA Coach Conversacional",
                            style = MaterialTheme.typography.displayMedium,
                            color = OnBackgroundText,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Seu mentor inteligente de bem-estar disponível 24 horas por dia. Converse plenamente, crie metas práticas e organize seus planos com base no seu humor.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SubtextGrey,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
                3 -> {
                    // Collect preferences & Auth Form combined
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isSignUpMode) "Criar sua Conta Premium" else "Entrar no MindFit",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackgroundText,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Seu Nome") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = DividerSlate,
                                focusedTextColor = OnBackgroundText,
                                unfocusedTextColor = OnBackgroundText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = userEmail,
                            onValueChange = { userEmail = it },
                            label = { Text("E-mail") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = DividerSlate,
                                focusedTextColor = OnBackgroundText,
                                unfocusedTextColor = OnBackgroundText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = userPassword,
                            onValueChange = { userPassword = it },
                            label = { Text("Senha") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = DividerSlate,
                                focusedTextColor = OnBackgroundText,
                                unfocusedTextColor = OnBackgroundText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Goal picker
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Seu Objetivo Principal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SubtextGrey,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            val goals = listOf("Foco & Clareza", "Reduzir Ansiedade", "Auto-Organização")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                goals.forEach { goalOption ->
                                    val isSelected = userGoal == goalOption
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryTeal else SurfaceDark)
                                            .border(1.dp, DividerSlate, RoundedCornerShape(8.dp))
                                            .clickable { userGoal = goalOption }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = goalOption,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) OnPrimaryWhite else OnSurfaceText
                                        )
                                    }
                                }
                            }
                        }

                        // Local MVP Entry indicator
                        Text(
                            text = if (isSignUpMode) "Já tem conta? Entrar" else "Não tem conta? Cadastrar-se",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryIndigo,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { isSignUpMode = !isSignUpMode }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        // FOOTER CONTROLS
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // STEP DOT INDICATORS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (step == i) 12.dp else 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (step == i) PrimaryTeal else DividerSlate)
                    )
                }
            }

            if (step < 3) {
                MindFitPrimaryButton(
                    text = "Continuar",
                    onClick = { viewModel.onboardingStep.value = step + 1 },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_continue"
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MindFitSecondaryButton(
                        text = "Convidado (Offline)",
                        onClick = {
                            viewModel.updateOnboardingInfo(
                                name = if (userName.isEmpty()) "Convidado" else userName,
                                email = if (userEmail.isEmpty()) "offline@mindfit.com" else userEmail,
                                mainGoal = userGoal,
                                notificationTime = userNotificationTime
                            )
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "onboarding_guest"
                    )

                    MindFitPrimaryButton(
                        text = "Salvar e Iniciar",
                        onClick = {
                            viewModel.updateOnboardingInfo(
                                name = if (userName.isEmpty()) "Usuário" else userName,
                                email = if (userEmail.isEmpty()) "user@mindfit.com" else userEmail,
                                mainGoal = userGoal,
                                notificationTime = userNotificationTime
                            )
                        },
                        modifier = Modifier.weight(1.5f),
                        testTag = "onboarding_submit"
                    )
                }
            }
        }
    }
}
