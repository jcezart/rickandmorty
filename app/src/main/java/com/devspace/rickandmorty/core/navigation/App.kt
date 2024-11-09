// App.kt
package com.devspace.rickandmorty

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devspace.rickandmorty.presentation.*

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "character") {
        composable("character") {
            val application = LocalContext.current.applicationContext as MyApplication
            HomeScreen(navController = navController, application = application)
        }
        composable(
            route = "detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0
            DetailScreen(navController = navController, characterId = characterId)
        }
        composable("favorites") {
            val application = LocalContext.current.applicationContext as MyApplication
            FavoritesScreen(navController = navController, application = application)
        }
    }
}
