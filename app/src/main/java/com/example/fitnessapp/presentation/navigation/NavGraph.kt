package com.example.fitnessapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.presentation.auth.LoginScreen
import com.example.fitnessapp.presentation.clients.detail.ClientDetailScreen
import com.example.fitnessapp.presentation.clients.form.ClientFormScreen
import com.example.fitnessapp.presentation.clients.list.ClientListScreen
import com.example.fitnessapp.presentation.subscription.SubscriptionScreen
import com.example.fitnessapp.presentation.visit.VisitLogScreen
import kotlinx.coroutines.runBlocking

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // стартовый экран зависит от того есть ли токен
    val startDestination = remember {
        val loggedIn = runBlocking { ServiceLocator.authRepository.isLoggedIn() }
        if (loggedIn) Screen.ClientList.route else Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ClientList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ClientList.route) {
            ClientListScreen(
                onClientClick = { id -> navController.navigate(Screen.ClientDetail.create(id)) },
                onAddClient = { navController.navigate(Screen.ClientForm.create()) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ClientDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            ClientDetailScreen(
                clientId = id,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.ClientForm.edit(it)) },
                onSubscriptions = { navController.navigate(Screen.Subscription.create(it)) },
                onVisits = { navController.navigate(Screen.VisitLog.create(it)) }
            )
        }

        composable(
            route = Screen.ClientForm.route,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { entry ->
            val id = entry.arguments?.getString("id")
            ClientFormScreen(
                clientId = id,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Subscription.route,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType })
        ) { entry ->
            val clientId = entry.arguments?.getString("clientId") ?: return@composable
            SubscriptionScreen(clientId = clientId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.VisitLog.route,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType })
        ) { entry ->
            val clientId = entry.arguments?.getString("clientId") ?: return@composable
            VisitLogScreen(clientId = clientId, onBack = { navController.popBackStack() })
        }
    }
}
