package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.TokenStorage
import com.example.fitnessapp.data.remote.dto.AuthResponse
import com.example.fitnessapp.data.remote.dto.LoginRequest
import com.example.fitnessapp.data.remote.dto.RegisterRequest
import com.example.fitnessapp.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Unit> {
        return runCatching {
            val response = client.post("/auth/login") {
                setBody(LoginRequest(username, password))
            }
            if (response.status != HttpStatusCode.OK) {
                throw RuntimeException("Неверный логин или пароль")
            }
            val auth: AuthResponse = response.body()
            tokenStorage.saveToken(auth.token)
        }
    }

    override suspend fun register(username: String, password: String, role: String): Result<Unit> {
        return runCatching {
            val response = client.post("/auth/register") {
                setBody(RegisterRequest(username, password, role))
            }
            when (response.status) {
                HttpStatusCode.Created -> Unit
                HttpStatusCode.Conflict -> throw RuntimeException("Пользователь уже существует")
                else -> throw RuntimeException("Не удалось зарегистрироваться")
            }
            login(username, password).getOrThrow()
        }
    }

    override suspend fun logout() {
        tokenStorage.clear()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }

    override suspend fun getRole(): String? {
        return tokenStorage.getRole()
    }
}
