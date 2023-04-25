package com.example.soltest1.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soltest1.screens.MainScreen

@Composable
fun SolNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.MainScreen.name ) {

//        composable(Screens.MainScreen.name) {
//            MainScreen()
//        }
    }
}
