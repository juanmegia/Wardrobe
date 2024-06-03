package com.example.wardrobe.ui.navigation

import androidx.compose.runtime.Composable
import android.content.Context
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wardrobe.ui.screens.HomeScreen
import com.example.wardrobe.ui.screens.Screen
import com.example.wardrobe.ui.screens.auth.LoginScreen
import com.example.wardrobe.ui.screens.auth.SignUpScreen
import com.example.wardrobe.utils.AuthManager
import com.google.firebase.auth.FirebaseUser
@Composable
fun Navigation(context: Context, navController: NavHostController = rememberNavController()) {
    val authManager: AuthManager = AuthManager(context)

    val user: FirebaseUser? = authManager.getCurrentUser()

    Screen {
        NavHost(
            navController = navController,
            startDestination = if(user == null) Routes.Login.route else Routes.Home.route
        ) {
            composable(Routes.Login.route) {
                LoginScreen(
                    auth = authManager,
                    navigation = navController,
                )
            }
            composable(Routes.Home.route) {
                HomeScreen(
                    auth = authManager,
                    navigation = navController)
            }

            composable(Routes.SignUp.route) {
                SignUpScreen(
                    auth = authManager,
                    navigation = navController
                )
            }
        }
    }
}