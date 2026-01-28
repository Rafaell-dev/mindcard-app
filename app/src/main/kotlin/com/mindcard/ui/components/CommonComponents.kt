package com.mindcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(modifier = modifier) {
        // Sombra sólida inferior
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .background(MindCardColors.JetBlack, CircleShape)
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MindCardColors.Primary,
                contentColor = Color.White,
                disabledContainerColor = MindCardColors.Muted,
                disabledContentColor = MindCardColors.MutedForeground
            )
        ) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(modifier = modifier) {
        // Sombra sólida inferior
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .background(Color(0xFFD1D5DB), CircleShape)
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MindCardColors.SmokyWhite,
                contentColor = MindCardColors.JetBlack,
                disabledContainerColor = MindCardColors.Muted,
                disabledContentColor = MindCardColors.MutedForeground
            )
        ) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Box(modifier = modifier) {
        // Sombra sólida inferior
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .background(MindCardColors.Border, RoundedCornerShape(16.dp))
        )
        Surface(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(2.dp, MindCardColors.Border, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    icon()
                    Spacer(Modifier.width(8.dp))
                }
                Text(text, fontWeight = FontWeight.Bold, color = MindCardColors.JetBlack)
            }
        }
    }
}

@Composable
fun MindcardCard(
    title: String,
    backgroundColor: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(12.dp))
                Text(text = title, style = MindCardTypography.Heading3)
        }
    }
}

@Composable
fun MindCardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MindCardColors.Primary,
            unfocusedBorderColor = MindCardColors.Border
        )
    )
}

@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .background(MindCardColors.Border, CircleShape)
        )
        Surface(
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier.border(2.dp, MindCardColors.Border, CircleShape)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MindCardTypography.Caption,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
