package com.example.fitnessapp.domain.model

enum class SubscriptionType { MONTHLY, QUARTERLY, ANNUAL }

data class Subscription(
    val id: String,
    val clientId: String,
    val type: SubscriptionType,
    val startDate: String,
    val endDate: String,
    val isFrozen: Boolean,
    val price: String
)
