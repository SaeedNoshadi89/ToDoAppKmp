package presentation.screen.home

import MongoDB
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.RequestState
import presentation.screen.task.TaskAction
import domain.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class HomeViewModel(private val mongoDb: MongoDB) : ScreenModel {

    private val _uiState = MutableStateFlow(HomeUiState(RequestState.Idle))
    val uiState = _uiState.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _uiState.value
    )

    init {
        _uiState.update { it.copy(activeTasks = RequestState.Loading, completedTasks = RequestState.Loading) }
        getActiveTasks()
        getCompletedTasks()
    }

     fun setAction(action: HomeAction){
         when(action){
             is HomeAction.Delete -> deleteTask(action.task)
             is HomeAction.SetCompleted -> setCompletedTask(action.task, action.completed)
             is HomeAction.SetFavorite -> setFavoriteTask(action.task, action.isFavorite)
         }
     }
    private fun getActiveTasks() {
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDb.readActiveTasks().collectLatest { result ->
                _uiState.update { it.copy(activeTasks = result) }

            }
        }
    }

    private fun getCompletedTasks() {
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDb.readCompletedTasks().collectLatest { result ->
                _uiState.update { it.copy(completedTasks = result) }
            }
        }
    }

    private fun setCompletedTask(task: ToDoTask, isCompleted: Boolean){
        screenModelScope.launch(Dispatchers.IO) {
            mongoDb.setCompletedTask(task, isCompleted)
        }
    }
    private fun setFavoriteTask(task: ToDoTask, isFavorite: Boolean){
        screenModelScope.launch(Dispatchers.IO) {
            mongoDb.setFavoriteTask(task, isFavorite)
        }
    }

    private fun deleteTask(task: ToDoTask){
        screenModelScope.launch(Dispatchers.IO) {
            mongoDb.deleteTask(task)
        }
    }
}

data class HomeUiState(
    val idle: RequestState<Boolean>,
    val activeTasks: RequestState<List<ToDoTask>> = RequestState.Idle,
    val completedTasks: RequestState<List<ToDoTask>> = RequestState.Idle,
)
