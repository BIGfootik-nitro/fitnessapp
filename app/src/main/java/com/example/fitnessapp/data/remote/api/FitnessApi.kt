package com.example.fitnessapp.data.remote.api

import com.example.fitnessapp.data.local.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

object ApiConfig {
    const val BASE_URL = "https://fitness.springfamilychat.space"
}

fun createHttpClient(tokenStorage: TokenStorage): HttpClient {
    return HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenStorage.tokenFlow.first()
                    if (token != null) BearerTokens(token, "") else null
                }
            }
        }
        defaultRequest {
            url(ApiConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }
        expectSuccess = false
    }
}
