package com.mindcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindcard.ui.components.OutlineButton
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun LoginScreen(onGoogleSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Entrar", style = MindCardTypography.Heading2)
        Text(
            "Escolha como deseja acessar sua conta",
            style = MindCardTypography.Caption,
            color = MindCardColors.MutedForeground
        )
        Spacer(Modifier.height(24.dp))
        OutlineButton(
            text = "Entrar com Google",
            icon = { Text("G", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
