package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.IdResponse
import com.example.fitnessapp.data.remote.dto.SubscriptionRequest
import com.example.fitnessapp.data.remote.dto.SubscriptionResponse
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import com.example.fitnessapp.domain.repository.SubscriptionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

class SubscriptionRepositoryImpl(private val client: HttpClient) : SubscriptionRepository {

    override suspend fun getByClient(clientId: String): Result<List<Subscription>> = runCatching {
        val response = client.get("/clients/$clientId/subscriptions")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        val list: List<SubscriptionResponse> = response.body()
        list.map {
            Subscription(
                id = it.id,
                clientId = it.clientId,
                type = SubscriptionType.valueOf(it.type),
                startDate = it.startDate,
                endDate = it.endDate,
                isFrozen = it.isFrozen,
                price = it.price
            )
        }
    }

    override suspend fun create(clientId: String, type: String, startDate: String, endDate: String, price: String): Result<String> = runCatching {
        val response = client.post("/clients/$clientId/subscriptions") {
            setBody(SubscriptionRequest(type, startDate, endDate, price))
        }
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось оформить абонемент")
        response.body<IdResponse>().id
    }

    override suspend fun getMine(): Result<List<Subscription>> = runCatching {
        val response = client.get("/me/subscriptions")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        val list: List<SubscriptionResponse> = response.body()
        list.map {
            Subscription(it.id, it.clientId, SubscriptionType.valueOf(it.type),
                it.startDate, it.endDate, it.isFrozen, it.price)
        }
    }

    override suspend fun buyMine(type: String, startDate: String, endDate: String, price: String): Result<Unit> = runCatching {
        val response = client.post("/me/subscriptions") {
            setBody(SubscriptionRequest(type, startDate, endDate, price))
        }
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось оформить абонемент")
    }

    override suspend fun toggleFreeze(subscriptionId: String): Result<Boolean> = runCatching {
        val response = client.patch("/subscriptions/$subscriptionId/freeze")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось изменить статус")
        response.body<FreezeResponse>().isFrozen
    }

    @Serializable
    private data class FreezeResponse(val isFrozen: Boolean)
}
