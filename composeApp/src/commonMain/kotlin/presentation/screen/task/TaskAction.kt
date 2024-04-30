package presentation.screen.task

import domain.ToDoTask

sealed class TaskAction {
    data class Add(val task: ToDoTask) : TaskAction()
    data class Update(val task: ToDoTask) : TaskAction()
}
