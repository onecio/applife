package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Send
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
import com.example.ui.components.MindFitSuggestionChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.MindFitViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScreen(
    viewModel: MindFitViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.coachMessages.collectAsState()
    val isTyping by viewModel.isCoachTyping.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val messagesLeft by viewModel.messagesLeftInFreePlan.collectAsState()
    val isCrisis by viewModel.isEmotionalCrisis.collectAsState()

    val chatListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var textInput by remember { mutableStateOf("") }

    // Scroll to latest message on size change
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            chatListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // TOP COACH HEADER CHAT BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(1.dp, DividerSlate, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "IA MindFit Coach",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnBackgroundText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isTyping) "Formulando insights..." else "Online 24/7",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isTyping) PrimaryTeal else SubtextGrey,
                        fontWeight = if (isTyping) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Right header controls (Clear history & convert to action plan)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Convert To Action Plan button
                IconButton(
                    onClick = { viewModel.convertConversationsToDailyPlan() },
                    tooltip = "Gerar Plano de Ação"
                ) {
                    Icon(
                        Icons.Default.AssignmentTurnedIn,
                        contentDescription = "Gerar Plano",
                        tint = PrimaryTeal
                    )
                }

                IconButton(
                    onClick = { viewModel.clearChat() }
                ) {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = "Limpar Conversa",
                        tint = SubtextGrey
                    )
                }
            }
        }

        // CRISIS WARNING TOP BANNER IF APPLICABLE
        AnimatedVisibility(visible = isCrisis) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TertiaryRose.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TertiaryRose, RoundedCornerShape(0.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚨 AJUDA HUMANIZADA IMEDIATA",
                        style = MaterialTheme.typography.titleMedium,
                        color = TertiaryRose,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seus sentimentos são válidos e há pessoas prontas para acolher você agora de forma gratuita, confidencial e segura.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackgroundText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* Call CVV or direct helper */ },
                        colors = ButtonDefaults.buttonColors(containerColor = TertiaryRose)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Ligar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ligar para o CVV (188)", color = OnPrimaryWhite)
                    }
                }
            }
        }

        // FREE TOKEN REMAINDER WRAPPER
        if (!userProfile.isPremium) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark.copy(alpha = 0.5f))
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mensagens gratuitas restantes hoje: $messagesLeft",
                    fontSize = 11.sp,
                    color = PremiumGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // CHAT MESSAGE LIST
        LazyColumn(
            state = chatListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🤖", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Olá, ${userProfile.name}!",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackgroundText,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Sou seu Coach pessoal de bem-estar. Converse comigo sobre sua energia atual, ansiedade ou desafios de foco. Posso estruturar tarefas práticas para ajudá-lo!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SubtextGrey,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(messages) { msg ->
                    val isUser = msg.sender == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isUser) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp, top = 4.dp)
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DividerSlate),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🧘", fontSize = 14.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 16.dp
                                    )
                                )
                                .background(if (isUser) SecondaryIndigo else SurfaceDark)
                                .border(
                                    1.dp,
                                    if (isUser) SecondaryIndigo else DividerSlate,
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 16.dp
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnBackgroundText
                            )
                        }
                    }
                }
            }

            // Typing feedback
            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DividerSlate),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💭", fontSize = 14.sp)
                        }
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                                .background(SurfaceDark)
                                .border(1.dp, DividerSlate, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Coach está formulando recomendações...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SubtextGrey
                                )
                            }
                        }
                    }
                }
            }
        }

        // SUGGESTIONS CHIPS HORIZONTAL ROW
        val microSuggestions = listOf(
            "Metas contra ansiedade",
            "Como focar em reuniões?",
            "Exercício respiratório rápido",
            "Dicas de organização eficiente"
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(microSuggestions) { suggestionText ->
                MindFitSuggestionChip(
                    text = suggestionText,
                    onClick = {
                        textInput = suggestionText
                        viewModel.sendMessageToCoach(suggestionText)
                        textInput = ""
                    }
                )
            }
        }

        // CHAT BOTTOM INPUT FIELD MODULE
        Surface(
            color = SurfaceDark,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DividerSlate, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Fale com o Coach...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = DividerSlate,
                        focusedTextColor = OnBackgroundText,
                        unfocusedTextColor = OnBackgroundText
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        if (textInput.trim().isNotEmpty()) {
                            viewModel.sendMessageToCoach(textInput)
                            textInput = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryTeal)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = OnPrimaryWhite)
                }
            }
        }
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    tooltip: String = "",
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
