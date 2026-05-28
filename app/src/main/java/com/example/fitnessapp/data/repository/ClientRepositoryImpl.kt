package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.ClientRequest
import com.example.fitnessapp.data.remote.dto.ClientResponse
import com.example.fitnessapp.data.remote.dto.IdResponse
import com.example.fitnessapp.domain.model.Client
import com.example.fitnessapp.domain.repository.ClientRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class ClientRepositoryImpl(private val client: HttpClient) : ClientRepository {

    override suspend fun getAll(search: String): Result<List<Client>> = runCatching {
        val response = client.get("/clients") {
            if (search.isNotBlank()) parameter("search", search)
        }
        ensureSuccess(response.status)
        val list: List<ClientResponse> = response.body()
        list.map { it.toDomain() }
    }

    override suspend fun getById(id: String): Result<Client> = runCatching {
        val response = client.get("/clients/$id")
        ensureSuccess(response.status)
        if (!response.status.isSuccess()) throw RuntimeException("Клиент не найден")
        val dto: ClientResponse = response.body()
        dto.toDomain()
    }

    override suspend fun create(fullName: String, phone: String?, email: String?, birthDate: String?): Result<String> = runCatching {
        val response = client.post("/clients") {
            setBody(ClientRequest(fullName, phone, email, birthDate))
        }
        ensureSuccess(response.status)
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось создать клиента")
        response.body<IdResponse>().id
    }

    override suspend fun update(id: String, fullName: String, phone: String?, email: String?, birthDate: String?): Result<Unit> = runCatching {
        val response = client.put("/clients/$id") {
            setBody(ClientRequest(fullName, phone, email, birthDate))
        }
        ensureSuccess(response.status)
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось обновить")
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        val response = client.delete("/clients/$id")
        ensureSuccess(response.status)
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось удалить")
    }

    private fun ClientResponse.toDomain() = Client(id, fullName, phone, email, birthDate)

    private fun ensureSuccess(status: HttpStatusCode) {
        when {
            status == HttpStatusCode.Unauthorized -> throw UnauthorizedException()
            status == HttpStatusCode.Forbidden -> throw RuntimeException("Нет доступа (403)")
            !status.isSuccess() -> throw RuntimeException("Ошибка сервера: ${status.value}")
        }
    }
}

class UnauthorizedException : RuntimeException("Не авторизован")
