package com.example.fitnessapp.presentation.subscription

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.components.DatePickerField
import com.example.fitnessapp.ui.components.FitnessButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditSubscriptionViewModel = viewModel()
) {
    LaunchedEffect(subscriptionId) { viewModel.load(subscriptionId) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Редактировать абонемент") },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                } })
        }
    ) { padding ->
        when {
            viewModel.loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                .verticalScroll(rememberScrollState())) {

                Text("Тип абонемента", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                val types = listOf("MONTHLY" to "Месячный", "QUARTERLY" to "Квартальный", "ANNUAL" to "Годовой")
                types.forEach { (value, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = viewModel.type == value,
                            onClick = { viewModel.type = value })
                        Text(label)
                    }
                }
                Spacer(Modifier.height(8.dp))

                DatePickerField("Начало", viewModel.startDate, { viewModel.startDate = it },
                    Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                DatePickerField("Конец", viewModel.endDate, { viewModel.endDate = it },
                    Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = viewModel.price, onValueChange = { viewModel.price = it },
                    label = { Text("Стоимость (руб.)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(16.dp))

                viewModel.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitnessButton("Сохранить", onClick = { viewModel.save(subscriptionId) { onSaved() } },
                        modifier = Modifier.weight(1f))
                    FitnessButton("Удалить", onClick = { viewModel.delete(subscriptionId) { onBack() } },
                        modifier = Modifier.weight(1f), outlined = true, destructive = true)
                }
            }
        }
    }
}
