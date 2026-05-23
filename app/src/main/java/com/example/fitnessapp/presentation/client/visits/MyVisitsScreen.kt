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
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Visit
import com.example.fitnessapp.ui.theme.PrimaryContainer
import com.example.fitnessapp.ui.theme.Primary

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
                            VisitCard(visit)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitCard(visit: Visit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = MaterialTheme.shapes.medium, color = PrimaryContainer,
                modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(visit.visitedAt.substring(8, 10), fontSize = 14.sp, fontWeight = FontWeight.W600, color = Primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(visit.visitedAt.substringBefore("T"), fontSize = 16.sp, fontWeight = FontWeight.W600)
                visit.note?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
