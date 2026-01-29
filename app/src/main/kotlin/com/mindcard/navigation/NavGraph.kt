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
import com.mindcard.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Practice : Screen("practice")
    object Result : Screen("result")
    object AddFlashcard : Screen("add_flashcard")
    object EditDeck : Screen("edit_deck")
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
    val addFlashcardViewModel: AddFlashcardViewModel = viewModel(factory = AddFlashcardViewModelFactory(mindcardRepository))
    val editDeckViewModel: EditDeckViewModel = viewModel(factory = EditDeckViewModelFactory(mindcardRepository))

    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            val authState by authViewModel.authState.collectAsState()
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    authViewModel.resetAuthState()
                }
            }
            LoginScreen(
                onLogin = { e, p -> authViewModel.login(e, p) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                errorMessage = (authState as? AuthState.Error)?.message,
                onDismissError = { authViewModel.resetAuthState() }
            )
        }

        composable(Screen.Register.route) {
            val authState by authViewModel.authState.collectAsState()
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    authViewModel.resetAuthState()
                }
            }
            RegisterScreen(
                onRegister = { n, e, p -> authViewModel.register(n, e, p) },
                onLoginClick = { navController.popBackStack() },
                errorMessage = (authState as? AuthState.Error)?.message,
                onDismissError = { authViewModel.resetAuthState() }
            )
        }

        composable(Screen.Home.route) {
            val mindcards by homeViewModel.mindcards.collectAsState()
            LaunchedEffect(Unit) { homeViewModel.loadMindcards() }
            
            HomeScreen(
                userName = currentUser?.nome ?: "Usuário",
                mindcards = mindcards,
                onMindcardClick = { m -> navController.navigate(Screen.EditDeck.route + "/${m.id}") },
                onAddClick = {
                    addFlashcardViewModel.reset()
                    navController.navigate(Screen.AddFlashcard.route)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onRefreshClick = { homeViewModel.loadMindcards() },
            )
        }

        composable(Screen.AddFlashcard.route) {
            AddFlashcardScreen(
                viewModel = addFlashcardViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { 
                    navController.popBackStack()
                    homeViewModel.loadMindcards()
                }
            )
        }
        
        composable(
            route = Screen.EditDeck.route + "/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
            LaunchedEffect(deckId) { editDeckViewModel.loadDeck(deckId) }
            
            EditDeckScreen(
                viewModel = editDeckViewModel,
                onBackClick = { navController.popBackStack() },
                onPracticeClick = { 
                    navController.navigate(Screen.Practice.route + "/$deckId")
                },
                onDeleteSuccess = {
                    navController.popBackStack()
                    homeViewModel.loadMindcards()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                    homeViewModel.loadMindcards()
                }
            )
        }

        composable(
            route = Screen.Practice.route + "/{mindcardId}",
            arguments = listOf(navArgument("mindcardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mindcardId = backStackEntry.arguments?.getString("mindcardId") ?: return@composable
            LaunchedEffect(mindcardId) { practiceViewModel.loadMindcard(mindcardId) }
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

            PracticeScreen(
                item = uiState.mindcard?.items?.getOrNull(uiState.currentIndex),
                currentIndex = uiState.currentIndex,
                totalItems = uiState.mindcard?.items?.size ?: 0,
                isAnswerVisible = uiState.isAnswerVisible,
                timerText = "%02d:%02d".format(uiState.elapsedTimeSeconds / 60, uiState.elapsedTimeSeconds % 60),
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
                navArgument("correct") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            ResultScreen(
                correctCount = backStackEntry.arguments?.getInt("correct") ?: 0,
                totalCount = backStackEntry.arguments?.getInt("total") ?: 0,
                onClose = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onRestart = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
            )
        }
    }
}

// Factories
class AuthViewModelFactory(private val authService: AuthService, private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(authService, sessionManager) as T
}

class HomeViewModelFactory(private val repository: MindcardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
}

class PracticeViewModelFactory(private val repository: MindcardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PracticeViewModel(repository) as T
}

class AddFlashcardViewModelFactory(private val repository: MindcardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AddFlashcardViewModel(repository) as T
}

class EditDeckViewModelFactory(private val repository: MindcardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditDeckViewModel(repository) as T
}
