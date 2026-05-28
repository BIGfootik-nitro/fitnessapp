package com.example.fitnessapp.presentation.sessions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class CreateEditSessionViewModel : ViewModel() {
    private val repo = ServiceLocator.sessionRepository

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var date by mutableStateOf("")
    var time by mutableStateOf("10:00")
    var durationMin by mutableStateOf("60")
    var maxCapacity by mutableStateOf("10")
    var error by mutableStateOf<String?>(null)

    fun loadSession(id: String) {
        viewModelScope.launch {
            repo.getById(id).onSuccess { s ->
                title = s.title
                description = s.description ?: ""
                date = s.scheduledAt.substringBefore('T')
                time = s.scheduledAt.substring(11, 16)
                durationMin = s.durationMin.toString()
                maxCapacity = s.maxCapacity.toString()
            }
        }
    }

    fun save(existingId: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            error = null
            val isoTime = try {
                LocalDateTime.parse("${date}T${time}:00")
                    .toInstant(ZoneOffset.UTC).toString()
            } catch (e: Exception) {
                error = "Неверный формат даты/времени"; return@launch
            }
            val dur = durationMin.toIntOrNull() ?: 60
            val cap = maxCapacity.toIntOrNull() ?: 10

            val result = if (existingId == null)
                repo.create(title, description.ifBlank { null }, isoTime, dur, cap)
            else
                repo.update(existingId, title, description.ifBlank { null }, isoTime, dur, cap)
                    .map { existingId }

            result.fold(onSuccess = { onDone() }, onFailure = { error = it.message })
        }
    }
}
