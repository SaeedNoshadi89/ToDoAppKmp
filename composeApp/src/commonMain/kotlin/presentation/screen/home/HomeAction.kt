package presentation.screen.home

import domain.ToDoTask

sealed class HomeAction {
    data class Delete(val task: ToDoTask) : HomeAction()
    data class SetCompleted(val task: ToDoTask, val completed: Boolean) : HomeAction()
    data class SetFavorite(val task: ToDoTask, val isFavorite: Boolean) : HomeAction()
}
