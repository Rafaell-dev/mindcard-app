package com.mindcard.ui.theme

import androidx.compose.ui.graphics.Color

object MindCardColors {
    // Cores principais
    val RoyalBlue = Color(0xFF4558C8)      // Primary - botões principais, destaques
    val JetBlack = Color(0xFF292B2D)        // Foreground - textos, sombras de botões
    val OldFlax = Color(0xFFB8EB6C)         // Success/Accent - categoria verde
    val SmokyWhite = Color(0xFFEFF1F7)      // Background secundário, cards
    val FoxGray = Color(0xFF8C8C8C)         // Muted - textos secundários
    val RocketOrange = Color(0xFFF7CD63)    // Accent - categoria laranja
    val TaskBlue = Color(0xFFA3C8F9)        // Accent - categoria azul claro

    // Semânticas
    val Background = Color.White
    val Foreground = JetBlack
    val Primary = RoyalBlue
    val PrimaryForeground = Color.White
    val Muted = Color(0xFFF7F7F7)
    val MutedForeground = FoxGray
    val Destructive = Color(0xFFC00021)
    val Border = Color(0xFFEBEBEB)
}
