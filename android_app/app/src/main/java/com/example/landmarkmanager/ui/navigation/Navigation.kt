package com.example.landmarkmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.landmarkmanager.ui.screens.add.AddLandmarkScreen
import com.example.landmarkmanager.ui.screens.detail.LandmarkDetailScreen
import com.example.landmarkmanager.ui.screens.list.LandmarkListScreen
import com.example.landmarkmanager.ui.screens.login.LoginScreen
import com.example.landmarkmanager.data.repository.AuthRepository

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object LandmarkList : Screen("landmark_list")
    object AddLandmark : Screen("add_landmark")
    object LandmarkDetail : Screen("landmark_detail/{landmarkId}") {
        fun createRoute(landmarkId: Int) = "landmark_detail/$landmarkId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    val startDestination = if (authRepository.isLoggedIn()) {
        Screen.LandmarkList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.LandmarkList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LandmarkList.route) {
            LandmarkListScreen(
                onNavigateToDetail = { landmarkId ->
                    navController.navigate(Screen.LandmarkDetail.createRoute(landmarkId))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddLandmark.route)
                }
            )
        }

        composable(Screen.AddLandmark.route) {
            AddLandmarkScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LandmarkDetail.route,
            arguments = listOf(
                navArgument("landmarkId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val landmarkId = backStackEntry.arguments?.getInt("landmarkId") ?: return@composable
            LandmarkDetailScreen(
                landmarkId = landmarkId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 