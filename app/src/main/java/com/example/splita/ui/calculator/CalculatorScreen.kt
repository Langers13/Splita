package com.example.splita.ui.calculator

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.splita.ui.UserViewModel
import net.objecthunter.exp4j.ExpressionBuilder

@Composable
fun CalculatorScreen(viewModel: UserViewModel) {
    val users by viewModel.users.collectAsState()
    val foodWeight by viewModel.foodWeight.collectAsState()
    val totalCalories = users.filter { it.isSelected }.sumOf { it.totalCalories }
    val calculatedFoodWeight = viewModel.calculateFoodWeight()

    var showCalculatorDialog by remember { mutableStateOf(false) }
    var isTwoMeals by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    if (interactionSource.collectIsPressedAsState().value) {
        showCalculatorDialog = true
    }

    if (showCalculatorDialog) {
        CalculatorDialog(
            currentValue = foodWeight,
            onDismiss = { showCalculatorDialog = false },
            onConfirm = {
                viewModel.updateFoodWeight(it)
                showCalculatorDialog = false
            }
        )
    }

    Column(modifier = Modifier.padding(24.dp)) {
        OutlinedTextField(
            value = foodWeight,
            onValueChange = {},
            readOnly = true,
            label = { Text("Food Weight (g)") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            interactionSource = interactionSource,
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (foodWeight.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateFoodWeight("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                    IconButton(onClick = { showCalculatorDialog = true }) {
                        Icon(Icons.Filled.Calculate, contentDescription = "Open Calculator")
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("1 Meal")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isTwoMeals,
                onCheckedChange = { isTwoMeals = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("2 Meals")
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(users.filter { it.isSelected }) {
                val baseShare = if (totalCalories > 0) {
                    (it.totalCalories.toDouble() / totalCalories) * calculatedFoodWeight
                } else {
                    0.0
                }
                val share = if (isTwoMeals) baseShare / 2 else baseShare

                Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = it.name,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "%.0f g".format(share),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun CalculatorDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var expression by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Calculator") },
        text = {
            Column {
                TextField(
                    value = expression,
                    onValueChange = { expression = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(16.dp))
                Column {
                    val buttonModifier = Modifier.weight(1f)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("1", buttonModifier) { expression += "1" }
                        CalculatorButton("2", buttonModifier) { expression += "2" }
                        CalculatorButton("3", buttonModifier) { expression += "3" }
                        CalculatorButton("/", buttonModifier) { expression += "/" }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("4", buttonModifier) { expression += "4" }
                        CalculatorButton("5", buttonModifier) { expression += "5" }
                        CalculatorButton("6", buttonModifier) { expression += "6" }
                        CalculatorButton("*", buttonModifier) { expression += "*" }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("7", buttonModifier) { expression += "7" }
                        CalculatorButton("8", buttonModifier) { expression += "8" }
                        CalculatorButton("9", buttonModifier) { expression += "9" }
                        CalculatorButton("-", buttonModifier) { expression += "-" }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("C", buttonModifier) { expression = "" }
                        CalculatorButton("0", buttonModifier) { expression += "0" }
                        CalculatorButton(".", buttonModifier) { expression += "." }
                        CalculatorButton("+", buttonModifier) { expression += "+" }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (expression.isNotEmpty()) {
                                    expression = expression.dropLast(1)
                                }
                            },
                            modifier = buttonModifier
                        ) { Text("DEL") }
                        Button(
                            onClick = {
                                try {
                                    val result = ExpressionBuilder(expression).build().evaluate()
                                    expression = result.toString()
                                } catch (_: Exception) {
                                    expression = "Error"
                                }
                            },
                            modifier = buttonModifier
                        ) { Text("=") }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Exit") }
                    Button(
                        onClick = { onConfirm(expression) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Use Value") }
                }
            }
        },
        dismissButton = {},
        confirmButton = {}
    )
}

@Composable
private fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text)
    }
}
