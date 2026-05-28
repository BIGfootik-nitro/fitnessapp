package com.example.fitnessapp.data.local

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64

object JwtUtils {

    fun extractRole(token: String?): String? =
        parsePayload(token)?.get("role")?.jsonPrimitive?.content?.takeIf { it.isNotEmpty() }

    fun extractUserId(token: String?): String? =
        parsePayload(token)?.get("userId")?.jsonPrimitive?.content?.takeIf { it.isNotEmpty() }

    private fun parsePayload(token: String?): kotlinx.serialization.json.JsonObject? {
        if (token.isNullOrBlank()) return null
        val parts = token.split(".")
        if (parts.size < 2) return null
        return runCatching {
            val padded = parts[1].let { it + "=".repeat((4 - it.length % 4) % 4) }
            val bytes = Base64.getUrlDecoder().decode(padded)
            Json.parseToJsonElement(String(bytes, Charsets.UTF_8)).jsonObject
        }.getOrNull()
    }
}
