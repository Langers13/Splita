package com.example.splita.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splita.model.User
import com.example.splita.ui.UserViewModel

@Composable
fun UsersScreen(viewModel: UserViewModel) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addUser() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(users) { user ->
                UserCard(
                    user = user,
                    onUserChange = { updatedUser -> viewModel.updateUser(updatedUser) },
                    onDelete = { viewModel.deleteUser(user) },
                    onSelect = { viewModel.toggleUserSelection(user) }
                )
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onUserChange: (User) -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = user.name,
                    onValueChange = { onUserChange(user.copy(name = it)) },
                    label = { Text("Name") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.width(24.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Checkbox(checked = user.isSelected, onCheckedChange = { onSelect() })
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete User")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = if (user.age == 0) "" else user.age.toString(),
                    onValueChange = { onUserChange(user.copy(age = it.toIntOrNull() ?: 0)) },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    suffix = { Text("(yrs)") }
                )
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    value = if (user.height == 0) "" else user.height.toString(),
                    onValueChange = { onUserChange(user.copy(height = it.toIntOrNull() ?: 0)) },
                    label = { Text("Height") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    suffix = { Text("(cm)") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = if (user.weight == 0f) "" else user.weight.toString(),
                    onValueChange = { onUserChange(user.copy(weight = it.toFloatOrNull() ?: 0f)) },
                    label = { Text("Weight") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    suffix = { Text("(kg)") }
                )
                Spacer(modifier = Modifier.width(24.dp))
                Box(modifier = Modifier.weight(1f),
                    ) {
                    SexSelector(sex = user.sex) { sex ->
                        onUserChange(user.copy(sex = sex))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = if (user.activityCalories == 0) "0" else user.activityCalories.toString(),
                    onValueChange = {
                        onUserChange(
                            user.copy(
                                activityCalories = it.toIntOrNull() ?: 0
                            )
                        )
                    },
                    label = { Text("Xtra Cals") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    suffix = { Text("(cal)") }
                )
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    value = "${user.totalCalories}",
                    onValueChange = {},
                    label = { Text("Total") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    suffix = { Text("(cal)") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SexSelector(sex: String, onSexChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = sex,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sex") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            shape = MaterialTheme.shapes.medium
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("M") },
                onClick = {
                    onSexChange("M")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("F") },
                onClick = {
                    onSexChange("F")
                    expanded = false
                }
            )
        }
    }
}
