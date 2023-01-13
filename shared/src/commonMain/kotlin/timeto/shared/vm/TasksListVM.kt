package timeto.shared.vm

import kotlinx.coroutines.flow.*
import timeto.shared.*
import timeto.shared.db.TaskFolderModel
import timeto.shared.db.TaskModel

class TasksListVM(
    val folder: TaskFolderModel,
) : __VM<TasksListVM.State>() {

    class UiTask(
        val task: TaskModel,
    ) {
        val listText: String
        val triggers: List<Trigger>

        init {
            val textFeatures = TextFeatures.parse(task.text)
            listText = textFeatures.textUI()
            triggers = textFeatures.triggers
        }

        fun start(
            onStarted: () -> Unit,
            needSheet: () -> Unit, // todo data for sheet
        ) {
            val autostartData = taskAutostartData(task) ?: return needSheet()
            launchExDefault {
                task.startInterval(
                    deadline = autostartData.second,
                    activity = autostartData.first,
                )
                onStarted()
            }
        }

        fun upFolder(newFolder: TaskFolderModel) {
            launchExDefault {
                task.upFolder(newFolder)
            }
        }

        // - By manual removing;
        // - After adding to calendar;
        // - By starting from activity sheet;
        fun delete() {
            launchExDefault {
                task.delete()
            }
        }
    }

    data class State(
        val uiTasks: List<UiTask>,
    )

    override val state = MutableStateFlow(
        State(
            uiTasks = DI.tasks.toUiList()
        )
    )

    override fun onAppear() {
        TaskModel.getAscFlow()
            .onEachExIn(scopeVM()) { list ->
                state.update { it.copy(uiTasks = list.toUiList()) }
            }
    }

    private fun List<TaskModel>.toUiList() = this
        .filter { it.folder_id == folder.id }
        .sortedByDescending { it.id }
        .map { UiTask(it) }
}
