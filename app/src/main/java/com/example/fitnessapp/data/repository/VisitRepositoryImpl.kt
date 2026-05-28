package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.IdResponse
import com.example.fitnessapp.data.remote.dto.VisitRequest
import com.example.fitnessapp.data.remote.dto.VisitResponse
import com.example.fitnessapp.domain.model.Visit
import com.example.fitnessapp.domain.repository.VisitRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class VisitRepositoryImpl(private val client: HttpClient) : VisitRepository {

    override suspend fun getByClient(clientId: String): Result<List<Visit>> = runCatching {
        val response = client.get("/clients/$clientId/visits")
        ensureSuccess(response.status)
        val list: List<VisitResponse> = response.body()
        list.map { Visit(it.id, it.clientId, it.visitedAt, it.note) }
    }

    override suspend fun getMine(): Result<List<Visit>> = runCatching {
        val response = client.get("/me/visits")
        ensureSuccess(response.status)
        val list: List<VisitResponse> = response.body()
        list.map { Visit(it.id, it.clientId, it.visitedAt, it.note) }
    }

    override suspend fun add(clientId: String, visitedAt: String, note: String?): Result<String> = runCatching {
        val response = client.post("/clients/$clientId/visits") {
            setBody(VisitRequest(visitedAt, note))
        }
        ensureSuccess(response.status)
        response.body<IdResponse>().id
    }

    private fun ensureSuccess(status: HttpStatusCode) {
        when {
            status == HttpStatusCode.Unauthorized -> throw UnauthorizedException()
            status == HttpStatusCode.Forbidden -> throw RuntimeException("Нет доступа (403)")
            !status.isSuccess() -> throw RuntimeException("Ошибка сервера: ${status.value}")
        }
    }
}
