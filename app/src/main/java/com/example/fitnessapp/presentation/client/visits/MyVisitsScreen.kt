package com.example.fitnessapp.presentation.client.visits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVisitsScreen(
    viewModel: MyVisitsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    Scaffold(topBar = { TopAppBar(title = { Text("История посещений") }) }) { padding ->
        when (state) {
            is MyVisitsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is MyVisitsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is MyVisitsUiState.Loaded -> {
                if (state.visits.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center) { Text("Нет посещений") }
                } else {
                    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        state.visits.forEach { visit ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(visit.visitedAt.substringBefore("T"), fontWeight = FontWeight.SemiBold)
                                    visit.note?.let { Text(it) }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
