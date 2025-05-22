package com.haru.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.haru.todo.ui.screen.HomeRootScreen
import com.haru.todo.ui.screen.SettingsScreen
import com.haru.todo.ui.theme.HaruTheme
import com.haru.todo.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaruTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, mainViewModel = viewModel)
            }
        }
    }
}

object Screen {
    const val MAIN = "main"
    const val SETTINGS = "settings"
}

@Composable
fun NavGraph(navController: NavHostController, mainViewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = Screen.MAIN) {
        composable(Screen.MAIN) {
            HomeRootScreen(
                viewModel = mainViewModel,
                onNavigateToSettings = { navController.navigate(Screen.SETTINGS) }
            )
        }
        composable(Screen.SETTINGS) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}