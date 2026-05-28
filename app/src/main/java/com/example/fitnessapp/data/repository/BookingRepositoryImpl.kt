package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.BookingRequest
import com.example.fitnessapp.data.remote.dto.BookingResponse
import com.example.fitnessapp.data.remote.dto.BookingStatusUpdate
import com.example.fitnessapp.domain.model.Booking
import com.example.fitnessapp.domain.model.BookingStatus
import com.example.fitnessapp.domain.repository.BookingRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class BookingRepositoryImpl(private val client: HttpClient) : BookingRepository {

    override suspend fun getMine(): Result<List<Booking>> = runCatching {
        val response = client.get("/me/bookings")
        ensureSuccess(response.status)
        val list: List<BookingResponse> = response.body()
        list.map { Booking(it.id, it.clientId, it.clientName, it.scheduledAt, BookingStatus.valueOf(it.status), it.note) }
    }

    override suspend fun getAll(): Result<List<Booking>> = runCatching {
        val response = client.get("/bookings")
        ensureSuccess(response.status)
        val list: List<BookingResponse> = response.body()
        list.map { Booking(it.id, it.clientId, it.clientName, it.scheduledAt, BookingStatus.valueOf(it.status), it.note) }
    }

    override suspend fun create(scheduledAtIso: String, note: String?): Result<Unit> = runCatching {
        val response = client.post("/me/bookings") {
            setBody(BookingRequest(scheduledAtIso, note))
        }
        ensureSuccess(response.status)
    }

    override suspend fun cancelMine(id: String): Result<Unit> = runCatching {
        val response = client.patch("/me/bookings/$id/cancel")
        ensureSuccess(response.status)
    }

    override suspend fun changeStatus(id: String, status: String): Result<Unit> = runCatching {
        val response = client.patch("/bookings/$id/status") {
            setBody(BookingStatusUpdate(status))
        }
        ensureSuccess(response.status)
    }

    private fun ensureSuccess(status: HttpStatusCode) {
        when {
            status == HttpStatusCode.Unauthorized -> throw UnauthorizedException()
            status == HttpStatusCode.Forbidden -> throw RuntimeException("Нет доступа (403)")
            !status.isSuccess() -> throw RuntimeException("Ошибка сервера: ${status.value}")
        }
    }
}
