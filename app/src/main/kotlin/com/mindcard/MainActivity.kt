package com.mindcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mindcard.data.service.AuthService
import com.mindcard.data.repository.MindcardRepository
import com.mindcard.navigation.NavGraph
import com.mindcard.ui.theme.MindCardTheme

class MainActivity : ComponentActivity() {
    private val authService = AuthService()
    private val mindcardRepository = MindcardRepository()
    private lateinit var sessionManager: com.mindcard.data.local.SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = com.mindcard.data.local.SessionManager(this)

        setContent {
            MindCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        authService = authService,
                        mindcardRepository = mindcardRepository,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}
