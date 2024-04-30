package presentation.screen.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.ToDoTask

const val DEFAULT_TITLE = "Enter the Title"
const val DEFAULT_DESCRIPTION = "Add som description"

@OptIn(ExperimentalMaterial3Api::class)
class TaskScreen(val task: ToDoTask? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<TaskViewModel>()
        var currentTitle by remember {
            mutableStateOf(task?.title ?: DEFAULT_TITLE)
        }
        var currentDescription by remember {
            mutableStateOf(task?.description ?: DEFAULT_DESCRIPTION)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        BasicTextField(
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            ),
                            singleLine = true,
                            value = currentTitle,
                            onValueChange = { currentTitle = it }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back Icon")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentTitle.isNotEmpty() && currentDescription.isNotEmpty()) {
                    FloatingActionButton(onClick = {
                        if (task != null) {
                            viewModel.setAction(TaskAction.Update(ToDoTask().apply {
                                _id = task._id
                                title = currentTitle
                                description = currentDescription
                            }))
                        } else {
                            viewModel.setAction(TaskAction.Add(ToDoTask().apply {
                                title = currentTitle
                                description = currentDescription
                            }))
                        }
                        navigator.pop()
                    }, shape = RoundedCornerShape(size = 12.dp)) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Checkmark Icon")
                    }
                }
            }
        ) { padding ->
            BasicTextField(
                modifier = Modifier.fillMaxSize().padding(all = 24.dp)
                    .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                ),
                singleLine = true,
                value = currentDescription,
                onValueChange = { currentDescription = it }
            )
        }
    }
}