package com.example.fitnessapp.presentation.clients.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.components.AvatarInitials
import com.example.fitnessapp.ui.components.FitnessButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onSubscriptions: (String) -> Unit,
    onVisits: (String) -> Unit,
    viewModel: ClientDetailViewModel = viewModel()
) {
    LaunchedEffect(clientId) { viewModel.load(clientId) }
    LaunchedEffect(viewModel.deleted) { if (viewModel.deleted) onBack() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Карточка клиента") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (state is ClientDetailUiState.Success) {
                        IconButton(onClick = { onEdit(clientId) }) {
                            Icon(Icons.Default.Edit, "Редактировать")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is ClientDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ClientDetailUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center))
                is ClientDetailUiState.Success -> {
                    val c = state.client
                    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        AvatarInitials(c.fullName)

                        Spacer(Modifier.height(16.dp))
                        Text(c.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.W600)
                        Spacer(Modifier.height(20.dp))

                        // Info fields
                        Column(Modifier.fillMaxWidth()) {
                            InfoField("Телефон", c.phone ?: "—")
                            InfoField("Email", c.email ?: "—")
                            InfoField("Дата рождения", c.birthDate ?: "—")
                        }

                        Spacer(Modifier.height(24.dp))

                        FitnessButton("Абонементы", onClick = { onSubscriptions(clientId) },
                            modifier = Modifier.fillMaxWidth(), outlined = true)
                        Spacer(Modifier.height(8.dp))
                        FitnessButton("Посещения", onClick = { onVisits(clientId) },
                            modifier = Modifier.fillMaxWidth(), outlined = true)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text("Удалить клиента?") },
            text = { Text("Это действие нельзя отменить") },
            confirmButton = {
                FitnessButton("Удалить", onClick = { showDeleteDialog = false; viewModel.delete(clientId) }, destructive = true)
            },
            dismissButton = { FitnessButton("Отмена", onClick = { showDeleteDialog = false }, outlined = true) }
        )
    }
}

@Composable
private fun InfoField(label: String, value: String) {
    OutlinedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 16.sp)
        }
    }
}
