package presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.RequestState
import domain.ToDoTask
import presentation.components.ErrorScreen
import presentation.components.LoadingScreen
import presentation.components.TaskRow
import presentation.screen.task.TaskAction
import presentation.screen.task.TaskScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text("Home") })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(TaskScreen()) },
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Icon")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                DisplayTasks(
                    modifier = Modifier.weight(1f),
                    tasks = state.activeTasks,
                    onSelect = { selectedTask ->
                        navigator.push(TaskScreen(selectedTask))
                    },
                    onFavorite = { task, isFavorite ->
                        viewModel.setAction(
                            action = HomeAction.SetFavorite(task, isFavorite)
                        )
                    },
                    onComplete = { task, completed ->
                        viewModel.setAction(
                            action = HomeAction.SetCompleted(task, completed)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                DisplayTasks(
                    modifier = Modifier.weight(1f),
                    tasks = state.completedTasks,
                    showActive = false,
                    onComplete = { task, completed ->
                        viewModel.setAction(
                            action = HomeAction.SetCompleted(task, completed)
                        )
                    },
                    onDelete = { task ->
                        viewModel.setAction(
                            action = HomeAction.Delete(task)
                        )
                    }
                )
            }

        }
    }

    @Composable
    fun DisplayTasks(
        modifier: Modifier = Modifier,
        tasks: RequestState<List<ToDoTask>>,
        showActive: Boolean = true,
        onSelect: ((ToDoTask) -> Unit)? = null,
        onFavorite: ((ToDoTask, Boolean) -> Unit)? = null,
        onComplete: (ToDoTask, Boolean) -> Unit,
        onDelete: ((ToDoTask) -> Unit)? = null
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var taskToDelete: ToDoTask? by remember { mutableStateOf(null) }

        if (showDialog) {
            AlertDialog(
                title = {
                    Text(text = "Delete", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                },
                text = {
                    Text(
                        text = "Are you sure you want to remove '${taskToDelete!!.title}' task?",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        onDelete?.invoke(taskToDelete!!)
                        showDialog = false
                        taskToDelete = null
                    }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            taskToDelete = null
                            showDialog = false
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                },
                onDismissRequest = {
                    taskToDelete = null
                    showDialog = false
                }
            )
        }

        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = if (showActive) "Active Tasks" else "Completed Tasks",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            tasks.DisplayResult(
                onLoading = { LoadingScreen() },
                onError = { ErrorScreen(message = it) },
                onSuccess = {
                    if (it.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.padding(horizontal = 24.dp)) {
                            items(
                                items = it,
                                key = { task -> task._id.toHexString() }
                            ) { task ->
                                TaskRow(
                                    showActive = showActive,
                                    task = task,
                                    onSelect = { onSelect?.invoke(task) },
                                    onComplete = { selectedTask, completed ->
                                        onComplete(selectedTask, completed)
                                    },
                                    onFavorite = { selectedTask, favorite ->
                                        onFavorite?.invoke(selectedTask, favorite)
                                    },
                                    onDelete = { selectedTask ->
                                        taskToDelete = selectedTask
                                        showDialog = true
                                    }
                                )
                            }
                        }
                    } else {
                        ErrorScreen()
                    }
                }
            )
        }
    }
}