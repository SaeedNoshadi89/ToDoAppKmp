package presentation.di

import MongoDB
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.home.HomeViewModel
import presentation.screen.task.TaskViewModel

val mongoModule = module {
    single { MongoDB() }
    factory { HomeViewModel(get()) }
    factory { TaskViewModel(get()) }
}

fun initializeKoin() {
    startKoin {
        modules(mongoModule)
    }
}
