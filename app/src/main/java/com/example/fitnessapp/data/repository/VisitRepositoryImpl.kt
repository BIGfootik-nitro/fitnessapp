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
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        val list: List<VisitResponse> = response.body()
        list.map { Visit(it.id, it.clientId, it.visitedAt, it.note) }
    }

    override suspend fun add(clientId: String, visitedAt: String, note: String?): Result<String> = runCatching {
        val response = client.post("/clients/$clientId/visits") {
            setBody(VisitRequest(visitedAt, note))
        }
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось записать посещение")
        response.body<IdResponse>().id
    }
}
