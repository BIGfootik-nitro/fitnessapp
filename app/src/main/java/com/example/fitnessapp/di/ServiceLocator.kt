package com.example.fitnessapp.di

import android.content.Context
import com.example.fitnessapp.data.local.TokenStorage
import com.example.fitnessapp.data.remote.api.createHttpClient
import com.example.fitnessapp.data.repository.AuthRepositoryImpl
import com.example.fitnessapp.data.repository.ClientRepositoryImpl
import com.example.fitnessapp.data.repository.SubscriptionRepositoryImpl
import com.example.fitnessapp.data.repository.VisitRepositoryImpl
import com.example.fitnessapp.domain.repository.AuthRepository
import com.example.fitnessapp.domain.repository.ClientRepository
import com.example.fitnessapp.domain.repository.SubscriptionRepository
import com.example.fitnessapp.domain.repository.VisitRepository
import io.ktor.client.HttpClient

object ServiceLocator {

    private lateinit var appContext: Context
    private var httpClient: HttpClient? = null
    private var tokenStorage: TokenStorage? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun getTokenStorage(): TokenStorage {
        if (tokenStorage == null) tokenStorage = TokenStorage(appContext)
        return tokenStorage!!
    }

    private fun getHttpClient(): HttpClient {
        if (httpClient == null) httpClient = createHttpClient(getTokenStorage())
        return httpClient!!
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(getHttpClient(), getTokenStorage())
    }

    val clientRepository: ClientRepository by lazy {
        ClientRepositoryImpl(getHttpClient())
    }

    val subscriptionRepository: SubscriptionRepository by lazy {
        SubscriptionRepositoryImpl(getHttpClient())
    }

    val visitRepository: VisitRepository by lazy {
        VisitRepositoryImpl(getHttpClient())
    }
}
