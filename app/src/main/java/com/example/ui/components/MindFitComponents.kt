package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MindFitCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceDark,
    borderColor: Color = DividerSlate,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun MindFitPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryTeal,
            contentColor = OnPrimaryWhite,
            disabledContainerColor = DividerSlate,
            disabledContentColor = SubtextGrey
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(50.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MindFitSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = DividerSlate,
    testTag: String = ""
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = OnSurfaceText
        ),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(50.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MindFitMetricCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    MindFitCard(
        modifier = modifier,
        backgroundColor = SurfaceDark,
        borderColor = DividerSlate
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DividerSlate),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtextGrey
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = OnBackgroundText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MindFitMoodSelector(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = listOf(
        "Feliz" to "😊",
        "Calmo" to "😌",
        "Ansioso" to "😰",
        "Cansado" to "😴",
        "Focado" to "🎯"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        moods.forEach { (name, emoji) ->
            val isSelected = selectedMood == name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DividerSlate else Color.Transparent)
                    .clickable { onMoodSelected(name) }
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(emoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) PrimaryTeal else SubtextGrey,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MindFitSuggestionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, DividerSlate, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MindFitSafetyAlert(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TertiaryRose)
            ) {
                Text("Entendido", color = OnPrimaryWhite)
            }
        },
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = TertiaryRose)
        },
        title = {
            Text(
                "Apoio e Suporte Emocional",
                style = MaterialTheme.typography.titleLarge,
                color = OnBackgroundText,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                "Sua saúde mental é nossa maior preocupação. Lembre-se que o MindFit Coach NÃO substitui acompanhamento médico, psicoterapia ou psiquiatria.\n\nSe você está em sofrimento interno agudo, por favor entre em contato com o Centro de Valorização da Vida (CVV) ligando 188 de forma anônima e gratuita, ou procure o pronto-socorro mais próximo.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceText,
                lineHeight = 22.sp
            )
        },
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    )
}

@Composable
fun MindFitEmptyState(
    emoji: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OnBackgroundText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = SubtextGrey,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun MindFitTimerCircle(
    progress: Float, // 0.0f a 1.0f
    timeLeftString: String,
    isRunning: Boolean,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            // Draw background track
            drawCircle(
                color = DividerSlate,
                radius = size.minDimension / 2,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw active progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(SecondaryIndigo, PrimaryTeal, SecondaryIndigo)
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeLeftString,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackgroundText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onToggleClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) TertiaryRose else PrimaryTeal
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (isRunning) "Pausar" else "Iniciar",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryWhite
                )
            }
        }
    }
}
