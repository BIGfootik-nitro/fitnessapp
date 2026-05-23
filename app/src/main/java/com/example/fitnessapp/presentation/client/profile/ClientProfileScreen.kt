package com.example.fitnessapp.presentation.client.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    onLogout: () -> Unit,
    viewModel: ClientProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(state) {
        if (state is ProfileUiState.Saved) viewModel.load()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Профиль") }) }) { padding ->
        when (state) {
            is ProfileUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ProfileUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is ProfileUiState.Loaded -> {
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                    .verticalScroll(rememberScrollState())) {
                    Text("Логин: ${state.profile.username}", style = MaterialTheme.typography.titleMedium)
                    Text("Роль: клиент")
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(value = viewModel.fullName, onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("ФИО") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.phone, onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("Телефон") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.email, onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.birthDate, onValueChange = { viewModel.onBirthDateChange(it) },
                        label = { Text("Дата рождения (ГГГГ-ММ-ДД)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))

                    Button(onClick = { viewModel.save() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Сохранить")
                    }
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(onClick = { viewModel.logout(); onLogout() },
                        modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )) { Text("Выйти") }
                }
            }
            is ProfileUiState.Saved -> {}
        }
    }
}
