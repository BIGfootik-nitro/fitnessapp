package com.example.fitnessapp.presentation.client.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Profile
import com.example.fitnessapp.ui.components.AvatarInitials
import com.example.fitnessapp.ui.components.FitnessButton
import com.example.fitnessapp.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    onLogout: () -> Unit,
    onProfileSaved: () -> Unit = {},
    isNewClient: Boolean = false,
    viewModel: ClientProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(state) {
        if (state is ProfileUiState.Saved) {
            if (isNewClient) {
                onProfileSaved()
            } else {
                viewModel.load()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(if (isNewClient) "Заполните профиль" else "Профиль")
            })
        }
    ) { padding ->
        when (state) {
            is ProfileUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ProfileUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        FitnessButton("Повторить", onClick = { viewModel.load() })
                    }
                }
            is ProfileUiState.Loaded -> {
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    if (isNewClient) {
                        Text("Заполните данные чтобы продолжить", fontSize = 16.sp,
                            fontWeight = FontWeight.W500, color = Primary)
                        Spacer(Modifier.height(16.dp))
                    }

                    val name = viewModel.fullName.ifBlank { state.profile.username }
                    AvatarInitials(name)
                    Spacer(Modifier.height(12.dp))
                    Text("Логин: ${state.profile.username}", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(value = viewModel.fullName, onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("ФИО *") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.phone, onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("Телефон") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.email, onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text("Email") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.birthDate, onValueChange = { viewModel.onBirthDateChange(it) },
                        label = { Text("Дата рождения (ГГГГ-ММ-ДД)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(20.dp))

                    FitnessButton(
                        text = if (isNewClient) "Продолжить" else "Сохранить",
                        onClick = { viewModel.save() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = viewModel.fullName.isNotBlank()
                    )

                    if (!isNewClient) {
                        Spacer(Modifier.height(12.dp))
                        FitnessButton("Выйти", onClick = { viewModel.logout(); onLogout() },
                            modifier = Modifier.fillMaxWidth(), outlined = true, destructive = true)
                    }
                }
            }
            is ProfileUiState.Saved -> {}
        }
    }
}
