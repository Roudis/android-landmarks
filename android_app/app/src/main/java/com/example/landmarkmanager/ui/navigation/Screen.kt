package com.example.landmarkmanager.ui.navigation

sealed class Screen(val route: String) {
    object LandmarkList : Screen("landmarks")
    object LandmarkDetail : Screen("landmarks/{id}") {
        fun createRoute(id: Int) = "landmarks/$id"
    }
    object AddLandmark : Screen("add-landmark")
    object LandmarkMap : Screen("map/{latitude}/{longitude}/{title}") {
        fun createRoute(latitude: Double, longitude: Double, title: String) = 
            "map/$latitude/$longitude/${title.replace("/", "_")}"
    }
} 