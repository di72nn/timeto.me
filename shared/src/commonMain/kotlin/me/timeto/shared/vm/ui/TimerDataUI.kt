package me.timeto.shared.vm.ui

import me.timeto.shared.*
import me.timeto.shared.db.ActivityDb
import me.timeto.shared.db.IntervalDb
import me.timeto.shared.db.TaskDb
import kotlin.math.absoluteValue

class TimerDataUI(
    interval: IntervalDb,
    todayTasks: List<TaskDb>,
    isPurple: Boolean,
) {

    val pomodoroIcon: PomodoroIcon
    val controlsColor: ColorRgba

    val note: String
    val noteColor: ColorRgba

    val timerText: String
    val timerColor: ColorRgba

    val prolongHints: List<ProlongHint> = prolongHintsLocal

    ///

    private val activity: ActivityDb = interval.getActivityDI()
    private val pausedTaskData: PausedTaskData? = run {
        if (!activity.isOther())
            return@run null
        val note: String = interval.note
            ?: return@run null
        val pausedTaskId: Int = note.textFeatures().pause?.pausedTaskId
            ?: return@run null
        val pausedTask: TaskDb = todayTasks.firstOrNull { it.id == pausedTaskId }
            ?: return@run null
        val pausedTaskTf: TextFeatures = pausedTask.text.textFeatures()
        val pausedTaskTimer: Int = pausedTaskTf.paused?.timer
            ?: return@run null
        val pausedActivityDb: ActivityDb = pausedTaskTf.activity
            ?: return@run null
        PausedTaskData(
            taskDb = pausedTask,
            taskTextTf = pausedTaskTf,
            activityDb = pausedActivityDb,
            timer = pausedTaskTimer,
        )
    }

    enum class PomodoroIcon {
        PAUSE, PLAY, FORWARD,
    }

    class ProlongHint(
        val timer: Int,
        val text: String,
    ) {

        fun prolong() {
            launchExDefault {
                val interval = IntervalDb.getLastOneOrNull()!!
                val newTf: TextFeatures = run {
                    val oldTf = (interval.note ?: "").textFeatures()
                    val prolonged = oldTf.prolonged ?: TextFeatures.Prolonged(interval.timer)
                    oldTf.copy(prolonged = prolonged)
                }
                val newTimer: Int = run {
                    val now = time()
                    val secondsToEnd = interval.id + interval.timer - now
                    if (secondsToEnd > 0)
                        interval.timer + timer
                    else
                        now + timer - interval.id
                }
                interval.up(
                    timer = newTimer,
                    note = newTf.textWithFeatures(),
                    activityDb = interval.getActivity(),
                )
            }
        }
    }

    init {

        val now = time()
        val secondsToEnd = interval.id + interval.timer - now

        pomodoroIcon = when {
            pausedTaskData == null -> PomodoroIcon.PAUSE
            pausedTaskData.taskTextTf.timer != null -> PomodoroIcon.FORWARD
            else -> PomodoroIcon.PLAY
        }

        timerText = secondsToString(if (isPurple) (now - interval.id) else secondsToEnd)
        timerColor = when {
            isPurple -> ColorRgba.purple
            secondsToEnd < 0 -> ColorRgba.red
            pausedTaskData != null -> ColorRgba.green
            else -> ColorRgba.white
        }

        note = run {
            val tf = (interval.note ?: activity.name)
            tf.textFeatures().textUi(
                withActivityEmoji = false,
                withTimer = false,
            )
        }
        noteColor = if (pausedTaskData != null) ColorRgba.green else timerColor

        controlsColor = when {
            isPurple -> ColorRgba.purple
            secondsToEnd < 0 -> ColorRgba.red
            pausedTaskData != null -> ColorRgba.green
            else -> defControlsColor
        }
    }

    fun togglePomodoro() {
        launchExDefault {
            if (pausedTaskData != null) {
                pausedTaskData.taskDb.startInterval(
                    pausedTaskData.timer,
                    pausedTaskData.activityDb,
                )
            } else {
                IntervalDb.pauseLastInterval()
            }
        }
    }
}

private val defControlsColor = ColorRgba(255, 255, 255, 180)

private val prolongHintsLocal: List<TimerDataUI.ProlongHint> = listOf(
    TimerDataUI.ProlongHint(5 * 60, "5"),
    TimerDataUI.ProlongHint(15 * 60, "15"),
    TimerDataUI.ProlongHint(30 * 60, "30"),
    TimerDataUI.ProlongHint(60 * 60, "1h"),
)

private fun secondsToString(seconds: Int): String {
    val hms = seconds.absoluteValue.toHms()
    val h = if (hms[0] > 0) "${hms[0]}:" else ""
    val m = hms[1].toString().padStart(2, '0') + ":"
    val s = hms[2].toString().padStart(2, '0')
    return "$h$m$s"
}

private data class PausedTaskData(
    val taskDb: TaskDb,
    val taskTextTf: TextFeatures,
    val activityDb: ActivityDb,
    val timer: Int,
)
