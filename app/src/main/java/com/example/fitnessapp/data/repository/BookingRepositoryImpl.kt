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
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        val list: List<BookingResponse> = response.body()
        list.map { Booking(it.id, it.clientId, it.clientName, it.scheduledAt, BookingStatus.valueOf(it.status), it.note) }
    }

    override suspend fun getAll(): Result<List<Booking>> = runCatching {
        val response = client.get("/bookings")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        val list: List<BookingResponse> = response.body()
        list.map { Booking(it.id, it.clientId, it.clientName, it.scheduledAt, BookingStatus.valueOf(it.status), it.note) }
    }

    override suspend fun create(scheduledAtIso: String, note: String?): Result<Unit> = runCatching {
        val response = client.post("/me/bookings") {
            setBody(BookingRequest(scheduledAtIso, note))
        }
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось записаться")
    }

    override suspend fun cancelMine(id: String): Result<Unit> = runCatching {
        val response = client.patch("/me/bookings/$id/cancel")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось отменить")
    }

    override suspend fun changeStatus(id: String, status: String): Result<Unit> = runCatching {
        val response = client.patch("/bookings/$id/status") {
            setBody(BookingStatusUpdate(status))
        }
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось изменить статус")
    }
}
