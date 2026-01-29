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
import com.mindcard.data.local.SessionManager

class MainActivity : ComponentActivity() {
    private val authService = AuthService()
    private lateinit var sessionManager: SessionManager
    private lateinit var mindcardRepository: MindcardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        mindcardRepository = MindcardRepository(sessionManager = sessionManager)

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
