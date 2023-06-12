package me.timeto.shared.vm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.timeto.shared.*
import me.timeto.shared.db.IntervalModel
import me.timeto.shared.vm.ui.TimerDataUI

class TimerTabProgressVM(
    isDayOrNight: Boolean,
) : __VM<TimerTabProgressVM.State>() {

    data class State(
        val lastInterval: IntervalModel,
        val isCountdown: Boolean,
        val isDayOrNight: Boolean,
        val idToUpdate: Int,
    ) {

        val timerData = TimerDataUI(
            interval = lastInterval,
            isCountdown = isCountdown,
            defColor = if (isDayOrNight) ColorNative.blue else ColorNative.white
        )

        val progressRatio: Float
        val progressColor: ColorNative

        init {
            val ratio = (timeMls() - lastInterval.id * 1000L).toFloat() / (lastInterval.deadline * 1000L).toFloat()
            if (ratio < 1) {
                progressRatio = ratio
                progressColor = ColorNative.blue
            } else {
                progressRatio = 1f
                progressColor = timerData.subtitleColor
            }
        }
    }

    override val state = MutableStateFlow(
        State(
            lastInterval = DI.lastInterval,
            isCountdown = true,
            isDayOrNight = isDayOrNight,
            idToUpdate = 0,
        )
    )

    override fun onAppear() {
        val scope = scopeVM()
        scope.launch {
            while (true) {
                state.update { it.copy(idToUpdate = it.idToUpdate + 1) }
                delay(1_000L)
            }
        }
        IntervalModel
            .getLastOneOrNullFlow()
            .filterNotNull()
            .onEachExIn(scope) { lastInterval ->
                val isCountdown = if (lastInterval.id == state.value.lastInterval.id)
                    state.value.isCountdown
                else true
                state.update {
                    it.copy(
                        lastInterval = lastInterval,
                        isCountdown = isCountdown,
                    )
                }
            }
    }

    fun toggleIsCountdown() {
        state.update { it.copy(isCountdown = !it.isCountdown) }
    }
}
