package com.example.landmarkmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.landmarkmanager.ui.navigation.Screen
import com.example.landmarkmanager.ui.screens.add.AddLandmarkScreen
import com.example.landmarkmanager.ui.screens.detail.LandmarkDetailScreen
import com.example.landmarkmanager.ui.screens.list.LandmarkListScreen
import com.example.landmarkmanager.ui.screens.map.LandmarkMapScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorScheme = if (isSystemInDarkTheme()) {
                darkColorScheme()
            } else {
                lightColorScheme()
            }

            MaterialTheme(
                colorScheme = colorScheme
            ) {
                Surface {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = Screen.LandmarkList.route) {
                        composable(Screen.LandmarkList.route) {
                            LandmarkListScreen(
                                onNavigateToDetail = { id ->
                                    navController.navigate(Screen.LandmarkDetail.createRoute(id))
                                },
                                onNavigateToAdd = {
                                    navController.navigate(Screen.AddLandmark.route)
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.LandmarkDetail.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                            LandmarkDetailScreen(
                                landmarkId = id,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToMap = { latitude, longitude, title ->
                                    navController.navigate(Screen.LandmarkMap.createRoute(latitude, longitude, title))
                                }
                            )
                        }
                        
                        composable(Screen.AddLandmark.route) {
                            AddLandmarkScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable(
                            route = Screen.LandmarkMap.route,
                            arguments = listOf(
                                navArgument("latitude") { type = NavType.FloatType },
                                navArgument("longitude") { type = NavType.FloatType },
                                navArgument("title") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
                            val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            
                            LandmarkMapScreen(
                                latitude = latitude,
                                longitude = longitude,
                                title = title,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
} 