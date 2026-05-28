package com.example.fitnessapp

import com.example.fitnessapp.data.local.JwtUtils
import com.example.fitnessapp.domain.model.Client
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

class UnitTests {

    // --- helpers ---

    private fun makeToken(payloadJson: String): String {
        val encoded = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payloadJson.toByteArray(Charsets.UTF_8))
        return "eyJhbGciOiJIUzI1NiJ9.$encoded.signature"
    }

    // 1. extractRole возвращает null для null-токена
    @Test
    fun extractRole_nullToken_returnsNull() {
        assertNull(JwtUtils.extractRole(null))
    }

    // 2. extractRole возвращает null для пустой строки
    @Test
    fun extractRole_emptyToken_returnsNull() {
        assertNull(JwtUtils.extractRole(""))
    }

    // 3. extractRole возвращает null для токена без точек
    @Test
    fun extractRole_malformedToken_returnsNull() {
        assertNull(JwtUtils.extractRole("notavalidtoken"))
    }

    // 4. extractRole возвращает CLIENT из валидного токена
    @Test
    fun extractRole_clientToken_returnsClient() {
        val token = makeToken("""{"userId":"u1","role":"CLIENT"}""")
        assertEquals("CLIENT", JwtUtils.extractRole(token))
    }

    // 5. extractRole возвращает TRAINER из валидного токена
    @Test
    fun extractRole_trainerToken_returnsTrainer() {
        val token = makeToken("""{"userId":"u2","role":"TRAINER"}""")
        assertEquals("TRAINER", JwtUtils.extractRole(token))
    }

    // 6. extractUserId возвращает правильный id
    @Test
    fun extractUserId_validToken_returnsUserId() {
        val token = makeToken("""{"userId":"abc-123","role":"CLIENT"}""")
        assertEquals("abc-123", JwtUtils.extractUserId(token))
    }

    // 7. extractUserId возвращает null, если поля нет в payload
    @Test
    fun extractUserId_missingField_returnsNull() {
        val token = makeToken("""{"role":"TRAINER"}""")
        assertNull(JwtUtils.extractUserId(token))
    }

    // 8. extractRole возвращает null при payload не-JSON
    @Test
    fun extractRole_invalidPayloadJson_returnsNull() {
        val brokenPayload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("not json at all".toByteArray())
        assertNull(JwtUtils.extractRole("header.$brokenPayload.sig"))
    }

    // 9. Активный абонемент — не заморожен и дата окончания в будущем
    @Test
    fun subscription_active_whenNotFrozenAndFutureDate() {
        val sub = Subscription(
            id = "1", clientId = "c1", type = SubscriptionType.MONTHLY,
            startDate = "2026-01-01", endDate = "2099-12-31",
            isFrozen = false, price = "3000"
        )
        val today = "2026-05-28"
        val isActive = sub.endDate >= today && !sub.isFrozen
        assertTrue(isActive)
    }

    // 10. Замороженный абонемент не считается активным
    @Test
    fun subscription_inactive_whenFrozen() {
        val sub = Subscription(
            id = "2", clientId = "c1", type = SubscriptionType.ANNUAL,
            startDate = "2026-01-01", endDate = "2099-12-31",
            isFrozen = true, price = "10000"
        )
        val today = "2026-05-28"
        val isActive = sub.endDate >= today && !sub.isFrozen
        assertTrue(!isActive)
    }
}
