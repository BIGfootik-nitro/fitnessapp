package com.example.fitnessapp.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(state) {
        if (state is LoginUiState.Success) onLoginSuccess()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Вход") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Учёт клиентов", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = {
                    viewModel.onUsernameChange(it)
                    viewModel.resetError()
                },
                label = { Text("Логин") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                    viewModel.resetError()
                },
                label = { Text("Пароль") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.login() },
                enabled = state !is LoginUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Войти")
                }
            }

            if (state is LoginUiState.Error) {
                Spacer(Modifier.height(12.dp))
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
