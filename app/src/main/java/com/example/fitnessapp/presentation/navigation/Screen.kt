package com.example.fitnessapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")

    // тренер / админ
    data object ClientList : Screen("clients")
    data object ClientForm : Screen("client_form?id={id}") {
        fun create() = "client_form"
        fun edit(id: String) = "client_form?id=$id"
    }
    data object ClientDetail : Screen("client_detail/{id}") {
        fun create(id: String) = "client_detail/$id"
    }
    data object Subscription : Screen("subscription/{clientId}") {
        fun create(clientId: String) = "subscription/$clientId"
    }
    data object VisitLog : Screen("visits/{clientId}") {
        fun create(clientId: String) = "visits/$clientId"
    }
    data object TrainerBookings : Screen("trainer_bookings")

    // клиент
    data object ClientHome : Screen("client_home")
    data object MySubs : Screen("my_subs")
    data object MyBookings : Screen("my_bookings")
    data object MyVisits : Screen("my_visits")
    data object MyNotifications : Screen("my_notifications")
    data object ClientProfile : Screen("client_profile?isNew={isNew}") {
        fun create(isNew: Boolean = false) = "client_profile?isNew=$isNew"
    }
}
