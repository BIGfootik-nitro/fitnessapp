package com.example.fitnessapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.presentation.auth.LoginScreen
import com.example.fitnessapp.presentation.auth.RegisterScreen
import com.example.fitnessapp.presentation.bookings.BookingsScreen
import com.example.fitnessapp.presentation.client.bookings.MyBookingsScreen
import com.example.fitnessapp.presentation.client.home.ClientHomeScreen
import com.example.fitnessapp.presentation.client.notifications.MyNotificationsScreen
import com.example.fitnessapp.presentation.client.profile.ClientProfileScreen
import com.example.fitnessapp.presentation.client.subs.MySubsScreen
import com.example.fitnessapp.presentation.client.visits.MyVisitsScreen
import com.example.fitnessapp.presentation.clients.detail.ClientDetailScreen
import com.example.fitnessapp.presentation.clients.form.ClientFormScreen
import com.example.fitnessapp.presentation.clients.list.ClientListScreen
import com.example.fitnessapp.presentation.subscription.SubscriptionScreen
import com.example.fitnessapp.presentation.visit.VisitLogScreen
import kotlinx.coroutines.runBlocking

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val startDestination = remember {
        val loggedIn = runBlocking { ServiceLocator.authRepository.isLoggedIn() }
        if (!loggedIn) Screen.Login.route
        else {
            val role = runBlocking { ServiceLocator.authRepository.getRole() } ?: "TRAINER"
            if (role == "CLIENT") Screen.ClientHome.route else Screen.ClientList.route
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // === Auth ===
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val role = runBlocking { ServiceLocator.authRepository.getRole() } ?: "TRAINER"
                    val dest = if (role == "CLIENT") Screen.ClientHome.route else Screen.ClientList.route
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onSuccess = {
                    val role = runBlocking { ServiceLocator.authRepository.getRole() } ?: "TRAINER"
                    val dest = if (role == "CLIENT") Screen.ClientHome.route else Screen.ClientList.route
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // === Trainer / Admin ===
        composable(Screen.ClientList.route) {
            ClientListScreen(
                onClientClick = { id -> navController.navigate(Screen.ClientDetail.create(id)) },
                onAddClient = { navController.navigate(Screen.ClientForm.create()) },
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onBookings = { navController.navigate(Screen.TrainerBookings.route) }
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
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { entry ->
            val id = entry.arguments?.getString("id")
            ClientFormScreen(clientId = id, onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() })
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

        composable(Screen.TrainerBookings.route) {
            BookingsScreen()
        }

        // === Client ===
        composable(Screen.ClientHome.route) {
            ClientHomeScreen(
                onSubscriptionsClick = { navController.navigate(Screen.MySubs.route) },
                onBookClick = { navController.navigate(Screen.MyBookings.route) },
                onVisitsClick = { navController.navigate(Screen.MyVisits.route) },
                onNotificationsClick = { navController.navigate(Screen.MyNotifications.route) },
                onProfileClick = { navController.navigate(Screen.ClientProfile.route) }
            )
        }

        composable(Screen.MySubs.route) { MySubsScreen() }
        composable(Screen.MyBookings.route) { MyBookingsScreen() }
        composable(Screen.MyVisits.route) { MyVisitsScreen() }
        composable(Screen.MyNotifications.route) { MyNotificationsScreen() }

        composable(Screen.ClientProfile.route) {
            ClientProfileScreen(
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }
            )
        }
    }
}
