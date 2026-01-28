package com.mindcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mindcard.data.local.SessionManager
import com.mindcard.data.repository.MindcardRepository
import com.mindcard.data.service.AuthService
import com.mindcard.ui.screens.*
import com.mindcard.ui.screens.HomeScreen // Explicit import just in case
import com.mindcard.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Practice : Screen("practice")
    object Result : Screen("result")
    object AddFlashcard : Screen("add_flashcard")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authService: AuthService,
    mindcardRepository: MindcardRepository,
    sessionManager: SessionManager
) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authService, sessionManager))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(mindcardRepository))
    val practiceViewModel: PracticeViewModel = viewModel(factory = PracticeViewModelFactory(mindcardRepository))

    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

        // ROTA LOGIN
        composable(Screen.Login.route) {
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Success -> {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        authViewModel.resetAuthState()
                    }
                    is AuthState.Error -> {
                        // O erro agora é lidado dentro da Screen através do errorMessage
                    }
                    else -> { /* Loading ou Idle */ }
                }
            }

            LoginScreen(
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                errorMessage = (authState as? AuthState.Error)?.message,
                onDismissError = { authViewModel.resetAuthState() }
            )
        }

        // ROTA REGISTER
        composable(Screen.Register.route) {
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Success -> {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        authViewModel.resetAuthState()
                    }
                    is AuthState.Error -> {
                        // O erro agora é lidado dentro da Screen através do errorMessage
                    }
                    else -> {}
                }
            }

            RegisterScreen(
                onRegister = { name, email, password ->
                    authViewModel.register(name, email, password)
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                errorMessage = (authState as? AuthState.Error)?.message,
                onDismissError = { authViewModel.resetAuthState() }
            )
        }

        // ROTA HOME
        composable(Screen.Home.route) {
            val mindcards by homeViewModel.mindcards.collectAsState()
            val userName = currentUser?.nome ?: "Usuário"

            // Carrega os mindcards quando entrar na tela
            LaunchedEffect(Unit) {
                homeViewModel.loadMindcards()
            }

            HomeScreen(
                userName = userName,
                mindcards = mindcards,
                onMindcardClick = { mindcard ->
                    navController.navigate(Screen.Practice.route + "/${mindcard.id}")
                },
                onAddClick = {
                    navController.navigate(Screen.AddFlashcard.route)
                }
            )
        }

        // ROTA PRACTICE
        composable(
            route = Screen.Practice.route + "/{mindcardId}",
            arguments = listOf(navArgument("mindcardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mindcardId = backStackEntry.arguments?.getString("mindcardId") ?: return@composable
            
            LaunchedEffect(mindcardId) {
                practiceViewModel.loadMindcard(mindcardId)
            }

            val uiState by practiceViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isFinished) {
                if (uiState.isFinished) {
                    val correct = uiState.correctCount
                    val total = uiState.mindcard?.items?.size ?: 0
                    navController.navigate(Screen.Result.route + "/$correct/$total") {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }

            val minutes = uiState.elapsedTimeSeconds / 60
            val seconds = uiState.elapsedTimeSeconds % 60
            val formattedTime = "%02d:%02d".format(minutes, seconds)

            PracticeScreen(
                item = uiState.mindcard?.items?.getOrNull(uiState.currentIndex),
                currentIndex = uiState.currentIndex,
                totalItems = uiState.mindcard?.items?.size ?: 0,
                isAnswerVisible = uiState.isAnswerVisible,
                timerText = formattedTime,
                onRevealAnswer = { practiceViewModel.revealAnswer() },
                onCorrect = { practiceViewModel.markCorrect() },
                onIncorrect = { practiceViewModel.markIncorrect() },
                onSkip = { practiceViewModel.skip() },
                onClose = { navController.popBackStack() }
            )
        }

        // ROTA RESULT
        composable(
            route = Screen.Result.route + "/{correct}/{total}",
            arguments = listOf(
                navArgument("correct") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val correctCount = backStackEntry.arguments?.getInt("correct") ?: 0
            val totalCount = backStackEntry.arguments?.getInt("total") ?: 0

            ResultScreen(
                correctCount = correctCount,
                totalCount = totalCount,
                onClose = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onRestart = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        // ROTA ADD FLASHCARD
        composable(Screen.AddFlashcard.route) {
             // Placeholder implementation if needed, checking existing code...
             // Assuming ViewModels or Screens are ready. If unused, better to leave empty or basic.
        }
    }
}


