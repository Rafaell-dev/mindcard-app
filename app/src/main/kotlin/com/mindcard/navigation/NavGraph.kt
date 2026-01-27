package com.mindcard.navigation

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindcard.data.repository.AuthRepository
import com.mindcard.data.repository.MindcardRepository
import com.mindcard.ui.screens.*
import com.mindcard.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Practice : Screen("practice")
    object Result : Screen("result")
    object AddFlashcard : Screen("add_flashcard")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository,
    mindcardRepository: MindcardRepository
) {

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(mindcardRepository))
    val practiceViewModel: PracticeViewModel = viewModel()

    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

        // ROTA LOGIN
        composable(Screen.Login.route) {
            LoginScreen(onGoogleSignIn = {
                authViewModel.signIn {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            })
        }

        // ROTA HOME
        composable(Screen.Home.route) {
            val mindcards by homeViewModel.mindcards.collectAsState()
            HomeScreen(
                userName = currentUser?.name ?: "Usuário",
                mindcards = mindcards,
                onMindcardClick = { mindcard ->
                    practiceViewModel.startPractice(mindcard)
                    navController.navigate(Screen.Practice.route)
                },
                onAddClick = {
                    navController.navigate(Screen.AddFlashcard.route)
                }
            )
        }

        // ROTA PRACTICE
        composable(Screen.Practice.route) {
            val state by practiceViewModel.uiState.collectAsState()

            val minutes = state.elapsedTimeSeconds / 60
            val seconds = state.elapsedTimeSeconds % 60
            val formattedTime = "%02d:%02d".format(minutes, seconds)

            if (state.isFinished) {

                navController.navigate(Screen.Result.route) {
                    popUpTo(Screen.Practice.route) { inclusive = true }
                }
            } else {
                val currentItem = state.mindcard?.items?.getOrNull(state.currentIndex)
                PracticeScreen(
                    item = currentItem,
                    currentIndex = state.currentIndex,
                    totalItems = state.mindcard?.items?.size ?: 0,
                    isAnswerVisible = state.isAnswerVisible,
                    timerText = formattedTime,
                    onRevealAnswer = { practiceViewModel.revealAnswer() },
                    onCorrect = { practiceViewModel.markCorrect() },
                    onIncorrect = { practiceViewModel.markIncorrect() },
                    onSkip = { practiceViewModel.skip() },
                    onClose = { navController.popBackStack() }
                )
            }
        }

        // ROTA RESULT
        composable(Screen.Result.route) {
            val state by practiceViewModel.uiState.collectAsState()
            ResultScreen(
                correctCount = state.correctCount,
                totalCount = state.mindcard?.items?.size ?: 0,
                onClose = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onRestart = {
                    state.mindcard?.let { practiceViewModel.startPractice(it) }
                    navController.navigate(Screen.Practice.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.AddFlashcard.route) {

        }
    }
}

// --- FACTORIES (Movidas para fora da função NavGraph) ---

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class HomeViewModelFactory(private val mindcardRepository: MindcardRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(mindcardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}