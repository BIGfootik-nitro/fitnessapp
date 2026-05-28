package com.example.fitnessapp.presentation.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.components.DatePickerField
import com.example.fitnessapp.ui.components.FitnessButton
import com.example.fitnessapp.ui.components.TimePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditSessionScreen(
    sessionId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateEditSessionViewModel = viewModel()
) {
    LaunchedEffect(sessionId) { if (sessionId != null) viewModel.loadSession(sessionId) }

    val isEdit = sessionId != null

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEdit) "Редактировать" else "Новая тренировка") },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, null)
                } })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
            .verticalScroll(rememberScrollState())) {

            OutlinedTextField(value = viewModel.title, onValueChange = { viewModel.title = it },
                label = { Text("Название") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = viewModel.description, onValueChange = { viewModel.description = it },
                label = { Text("Описание (необязательно)") }, maxLines = 3, modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(8.dp))

            DatePickerField("Дата", viewModel.date, { viewModel.date = it }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            TimePickerField("Время", viewModel.time, { viewModel.time = it }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = viewModel.durationMin, onValueChange = { viewModel.durationMin = it },
                label = { Text("Длительность (мин)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = viewModel.maxCapacity, onValueChange = { viewModel.maxCapacity = it },
                label = { Text("Макс. участников") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(16.dp))

            viewModel.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            FitnessButton(
                if (isEdit) "Сохранить" else "Создать",
                onClick = {
                    viewModel.save(sessionId) { onSaved() }
                },
                enabled = viewModel.title.isNotBlank() && viewModel.date.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
