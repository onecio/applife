package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MindFitCard
import com.example.ui.components.MindFitPrimaryButton
import com.example.ui.components.MindFitSecondaryButton
import com.example.ui.components.MindFitSafetyAlert
import com.example.ui.theme.*
import com.example.ui.viewmodel.MindFitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MindFitViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()

    var showPremiumSuccessDialog by remember { mutableStateOf(false) }
    var showCrisisAlert by remember { mutableStateOf(false) }

    var darkThemeEnabled by remember { mutableStateOf(true) }
    var remindersEnabled by remember { mutableStateOf(true) }

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
    ) {
        // TOP AVATAR HEADER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (profile.isPremium) PremiumGold.copy(alpha = 0.15f) else PrimaryTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (profile.isPremium) "👑" else "👤", fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = OnBackgroundText,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtextGrey
                )

                if (profile.isPremium) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PremiumGold)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Assinante Premium",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BackgroundDark
                        )
                    }
                }
            }
        }

        // MONETIZATION MODULE PREMIUM CARD
        item {
            MindFitCard(
                borderColor = if (profile.isPremium) PremiumGold else DividerSlate
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (profile.isPremium) "Seu Plano Premium Está Ativo" else "Upgrade para o MindFit Premium",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.isPremium) PremiumGold else OnBackgroundText
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Acesso ilimitado ao IA Coach, feedback contextual integrado ao humor, geração autônoma de cronogramas diários de ação e relatórios semanais com insights reais.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceText,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (!profile.isPremium) {
                    MindFitPrimaryButton(
                        text = "Ativar Assinatura Premium",
                        onClick = {
                            viewModel.togglePremiumStatus(true)
                            showPremiumSuccessDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    MindFitSecondaryButton(
                        text = "Cancelar Assinatura (Simulado)",
                        onClick = {
                            viewModel.togglePremiumStatus(false)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = DividerSlate
                    )
                }
            }
        }

        // SETTINGS TOGGLES
        item {
            Text(
                "Configurações Gerais",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackgroundText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            MindFitCard {
                // Topic 1: Dark theme toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Tema Escuro Permanente", color = OnSurfaceText, style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = { darkThemeEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryTeal, checkedTrackColor = PrimaryTeal.copy(alpha = 0.5f))
                    )
                }

                Divider(color = DividerSlate, modifier = Modifier.padding(vertical = 12.dp))

                // Topic 2: Reminders toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Lembretes Notificações Diárias", color = OnSurfaceText, style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = remindersEnabled,
                        onCheckedChange = { remindersEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryTeal, checkedTrackColor = PrimaryTeal.copy(alpha = 0.5f))
                    )
                }
            }
        }

        // EMERGENCY CRISIS / HELP SUPPORT HELPLINE TRIGGERS
        item {
            Card(
                onClick = { showCrisisAlert = true },
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TertiaryRose, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🆘", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Canal de Ajuda e Suporte",
                                style = MaterialTheme.typography.titleMedium,
                                color = TertiaryRose,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Contatos úteis urgentes de saúde mental",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceText
                            )
                        }
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = "Emergência", tint = TertiaryRose)
                }
            }
        }

        // ACTIONS AND STATUTE COMPLIANCE LIST
        item {
            Text(
                "Privacidade e Termos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackgroundText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            MindFitCard {
                // Option 1: Terms
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTermsDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Termos de Uso do MindFit", color = OnSurfaceText, style = MaterialTheme.typography.bodyLarge)
                    Icon(Icons.Default.ArrowForward, contentDescription = "Visualizar", tint = SubtextGrey)
                }

                Divider(color = DividerSlate, modifier = Modifier.padding(vertical = 8.dp))

                // Option 2: Privacy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Diretriz de Segurança e Privacidade", color = OnSurfaceText, style = MaterialTheme.typography.bodyLarge)
                    Icon(Icons.Default.ArrowForward, contentDescription = "Visualizar", tint = SubtextGrey)
                }

                Divider(color = DividerSlate, modifier = Modifier.padding(vertical = 8.dp))

                // Option 3: Export account
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Simulate Export success */ }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Exportar Relatório JSON de Dados", color = OnSurfaceText, style = MaterialTheme.typography.bodyLarge)
                    Icon(Icons.Default.Share, contentDescription = "Exportar", tint = PrimaryTeal)
                }

                Divider(color = DividerSlate, modifier = Modifier.padding(vertical = 8.dp))

                // Option 4: Delete Account
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.deleteUserAccount() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Excluir Conta Permanentemente", color = TertiaryRose, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = TertiaryRose)
                }
            }
        }
    }

    // Modal dialog overlays
    if (showPremiumSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showPremiumSuccessDialog = false },
            confirmButton = {
                Button(onClick = { showPremiumSuccessDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)) {
                    Text("Ótimo", color = OnPrimaryWhite)
                }
            },
            title = { Text("👑 Assinatura MindFit Ativa!", color = PremiumGold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = { Text("Parabéns! Você acaba de desbloquear todos os recursos premium do aplicativo. Desfrute da comunicação ilimitada com IA, planos detalhados adaptáveis e gráficos avançados.", color = OnSurfaceText, textAlign = TextAlign.Center) },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showCrisisAlert) {
        MindFitSafetyAlert(onDismiss = { showCrisisAlert = false })
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            confirmButton = { TextButton(onClick = { showTermsDialog = false }) { Text("Fechar", color = PrimaryTeal) } },
            title = { Text("Termos de Uso", color = OnBackgroundText, fontWeight = FontWeight.Bold) },
            text = { Text("O MindFit destina-se puramente ao desenvolvimento pessoal, controle organizacional do tempo e meditação. Sob hipótese alguma o aplicativo ou a inteligência artificial Coach se qualifica como psicólogo profissional habilitado ou conselheiro de crise severa. Ao prosseguir, o usuário aceita que concorda com estes preceitos éticos.", color = OnSurfaceText) },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            confirmButton = { TextButton(onClick = { showPrivacyDialog = false }) { Text("Fechar", color = PrimaryTeal) } },
            title = { Text("Segurança de Dados", color = OnBackgroundText, fontWeight = FontWeight.Bold) },
            text = { Text("Seguimos a legislação de proteção de dados. Seus check-ins emocionais, sessões Pomodoro gravadas e históricos de bate-papo com o IA Coach são mantidos estritamente confidenciais e armazenados nos mecanismos seguros SQLite locais da sua própria aplicação de forma privativa.", color = OnSurfaceText) },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
