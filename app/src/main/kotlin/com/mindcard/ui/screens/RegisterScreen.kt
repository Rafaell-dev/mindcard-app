package com.mindcard.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindcard.ui.components.MindCardTextField
import com.mindcard.ui.components.PrimaryButton
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun RegisterScreen(
    onRegister: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    errorMessage: String? = null,
    onDismissError: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }

    val translatedMessage = when (errorMessage) {
        "EMAIL_ALREADY_EXISTS" -> "Este e-mail já está em uso."
        "INVALID_EMAIL" -> "E-mail inválido."
        null -> null
        else -> errorMessage
    }

    LaunchedEffect(translatedMessage) {
        translatedMessage?.let {
            snackbarHostState.showSnackbar(it)
            onDismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MindCardColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { keyboardController?.hide() }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.mindcard.R.drawable.logo),
            contentDescription = "Logo MindCard",
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 32.dp)
        )

        Text("Criar conta", style = MindCardTypography.Heading2)
        Text(
            "Preencha os dados abaixo para começar",
            style = MindCardTypography.Caption,
            color = MindCardColors.MutedForeground
        )
        Spacer(Modifier.height(32.dp))

        MindCardTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "Nome completo"
        )
        Spacer(Modifier.height(16.dp))
        MindCardTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "E-mail"
        )
        Spacer(Modifier.height(16.dp))
        MindCardTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Senha",
            isPassword = true
        )
        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Cadastrar",
            onClick = {
                keyboardController?.hide()
                onRegister(name, email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        )

        Spacer(Modifier.height(24.dp))

        Row {
            Text("Já tem uma conta? ", style = MindCardTypography.Caption)
            Text(
                "Entrar",
                style = MindCardTypography.Caption.copy(fontWeight = FontWeight.Bold),
                color = MindCardColors.Primary,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}
}
