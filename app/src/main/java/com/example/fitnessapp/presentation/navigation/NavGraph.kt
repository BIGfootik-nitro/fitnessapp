package com.example.fitnessapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.data.repository.AuthEvent
import com.example.fitnessapp.data.repository.AuthEventBus
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
import com.example.fitnessapp.presentation.client.sessions.BrowseSessionsScreen
import com.example.fitnessapp.presentation.sessions.CreateEditSessionScreen
import com.example.fitnessapp.presentation.sessions.SessionDetailScreen
import com.example.fitnessapp.presentation.sessions.SessionListScreen
import com.example.fitnessapp.presentation.subscription.EditSubscriptionScreen
import com.example.fitnessapp.presentation.subscription.SubscriptionScreen
import com.example.fitnessapp.presentation.visit.VisitLogScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        AuthEventBus.events.collect { event ->
            if (event is AuthEvent.Unauthorized) {
                scope.launch { ServiceLocator.authRepository.logout() }
                navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
            }
        }
    }

    val startDestination = remember {
        val loggedIn = runBlocking { ServiceLocator.authRepository.isLoggedIn() }
        if (!loggedIn) Screen.Login.route
        else {
            val role = runBlocking { ServiceLocator.authRepository.getRole() } ?: "TRAINER"
            if (role == "CLIENT") Screen.ClientHome.route else Screen.ClientList.route
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
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
                    if (role == "CLIENT") {
                        navController.navigate(Screen.ClientProfile.create(isNew = true)) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.ClientList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ClientList.route) {
            ClientListScreen(
                onClientClick = { id -> navController.navigate(Screen.ClientDetail.create(id)) },
                onAddClient = { navController.navigate(Screen.ClientForm.create()) },
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onBookings = { navController.navigate(Screen.TrainerBookings.route) },
                onSessions = { navController.navigate(Screen.SessionList.route) }
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
            SubscriptionScreen(clientId = clientId, onBack = { navController.popBackStack() },
                onEditSub = { navController.navigate(Screen.EditSubscription.create(it)) })
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

        composable(Screen.SessionList.route) {
            val role = runBlocking { ServiceLocator.authRepository.getRole() } ?: "TRAINER"
            SessionListScreen(
                onSessionClick = { navController.navigate(Screen.SessionDetail.create(it)) },
                onCreateSession = { navController.navigate(Screen.CreateEditSession.create()) },
                onBack = { navController.popBackStack() },
                isAdmin = role == "ADMIN"
            )
        }

        composable(
            route = Screen.CreateEditSession.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { entry ->
            val id = entry.arguments?.getString("id")
            CreateEditSessionScreen(
                sessionId = id,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            val role = runBlocking { ServiceLocator.authRepository.getRole() } ?: "TRAINER"
            SessionDetailScreen(
                sessionId = id,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.CreateEditSession.edit(it)) },
                isAdmin = role == "ADMIN"
            )
        }

        composable(
            route = Screen.EditSubscription.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            EditSubscriptionScreen(
                subscriptionId = id,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.ClientHome.route) {
            ClientHomeScreen(
                onSubscriptionsClick = { navController.navigate(Screen.MySubs.route) },
                onSessionsClick = { navController.navigate(Screen.BrowseSessions.route) },
                onBookClick = { navController.navigate(Screen.MyBookings.route) },
                onVisitsClick = { navController.navigate(Screen.MyVisits.route) },
                onNotificationsClick = { navController.navigate(Screen.MyNotifications.route) },
                onProfileClick = { navController.navigate(Screen.ClientProfile.create()) }
            )
        }

        composable(Screen.MySubs.route) { MySubsScreen() }
        composable(Screen.MyBookings.route) { MyBookingsScreen() }
        composable(Screen.BrowseSessions.route) {
            BrowseSessionsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MyVisits.route) { MyVisitsScreen() }
        composable(Screen.MyNotifications.route) { MyNotificationsScreen() }

        composable(
            route = Screen.ClientProfile.route,
            arguments = listOf(navArgument("isNew") {
                type = NavType.BoolType; defaultValue = false
            })
        ) { entry ->
            val isNew = entry.arguments?.getBoolean("isNew") ?: false
            ClientProfileScreen(
                isNewClient = isNew,
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onProfileSaved = {
                    navController.navigate(Screen.ClientHome.route) {
                        popUpTo(Screen.ClientProfile.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
