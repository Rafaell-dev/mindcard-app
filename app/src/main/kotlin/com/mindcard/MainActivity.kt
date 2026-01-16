package com.mindcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mindcard.data.repository.AuthRepository
import com.mindcard.data.repository.MindcardRepository
import com.mindcard.navigation.NavGraph
import com.mindcard.ui.theme.MindCardTheme

class MainActivity : ComponentActivity() {
    private val authRepository = AuthRepository()
    private val mindcardRepository = MindcardRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        authRepository = authRepository,
                        mindcardRepository = mindcardRepository
                    )
                }
            }
        }
    }
}
