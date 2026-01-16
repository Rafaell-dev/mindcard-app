package com.mindcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindcard.ui.components.PrimaryButton
import com.mindcard.ui.components.SecondaryButton
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun ResultScreen(
    correctCount: Int,
    totalCount: Int,
    onClose: () -> Unit,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Resultado", style = MindCardTypography.Heading1)
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MindCardColors.SmokyWhite)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$correctCount / $totalCount",
                    style = MindCardTypography.Heading1,
                    color = MindCardColors.Primary
                )
                Text(
                    text = "Cards dominados",
                    style = MindCardTypography.Body,
                    color = MindCardColors.MutedForeground
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        PrimaryButton(
            text = "Praticar novamente",
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        SecondaryButton(
            text = "Fechar",
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
