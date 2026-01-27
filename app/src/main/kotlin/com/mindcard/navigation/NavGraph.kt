package com.mindcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindcard.data.service.AuthService
import com.mindcard.data.repository.MindcardRepository
import com.mindcard.ui.screens.*
import com.mindcard.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Practice : Screen("practice")
    object Result : Screen("result")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authService: AuthService,
    mindcardRepository: MindcardRepository,
    sessionManager: com.mindcard.data.local.SessionManager
) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authService, sessionManager))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(mindcardRepository))
    val practiceViewModel: PracticeViewModel = viewModel(factory = PracticeViewModelFactory(mindcardRepository))

    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
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

        composable(
            route = Screen.Practice.route + "/{mindcardId}",
            arguments = listOf(androidx.navigation.navArgument("mindcardId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val mindcardId = backStackEntry.arguments?.getString("mindcardId") ?: return@composable
            
            LaunchedEffect(mindcardId) {
                practiceViewModel.loadMindcard(mindcardId)
            }

            val uiState by practiceViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isFinished) {
                if (uiState.isFinished) {
                    navController.navigate(Screen.Result.route + "/${uiState.correctCount}/${uiState.mindcard?.items?.size ?: 0}") {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }

            PracticeScreen(
                item = uiState.mindcard?.items?.getOrNull(uiState.currentIndex),
                currentIndex = uiState.currentIndex,
                totalItems = uiState.mindcard?.items?.size ?: 0,
                isAnswerVisible = uiState.isAnswerVisible,
                onRevealAnswer = { practiceViewModel.revealAnswer() },
                onCorrect = { practiceViewModel.markCorrect() },
                onIncorrect = { practiceViewModel.markIncorrect() },
                onSkip = { practiceViewModel.skip() },
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Result.route + "/{correct}/{total}",
            arguments = listOf(
                androidx.navigation.navArgument("correct") { type = androidx.navigation.NavType.IntType },
                androidx.navigation.navArgument("total") { type = androidx.navigation.NavType.IntType }
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
                }
            )
        }
    }
}

class AuthViewModelFactory(
    private val authService: AuthService,
    private val sessionManager: com.mindcard.data.local.SessionManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authService, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class HomeViewModelFactory(private val mindcardRepository: MindcardRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(mindcardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class PracticeViewModelFactory(private val mindcardRepository: MindcardRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticeViewModel::class.java)) {
            return PracticeViewModel(mindcardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
