package com.example.fitnessapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
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
}
