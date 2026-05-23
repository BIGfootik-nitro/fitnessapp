package com.example.fitnessapp.data.local

import android.util.Base64
import org.json.JSONObject

object JwtUtils {

    fun extractRole(token: String?): String? = parsePayload(token)?.optString("role")?.takeIf { it.isNotEmpty() }

    fun extractUserId(token: String?): String? = parsePayload(token)?.optString("userId")?.takeIf { it.isNotEmpty() }

    private fun parsePayload(token: String?): JSONObject? {
        if (token.isNullOrBlank()) return null
        val parts = token.split(".")
        if (parts.size < 2) return null
        return runCatching {
            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            JSONObject(String(payloadBytes, Charsets.UTF_8))
        }.getOrNull()
    }
}
