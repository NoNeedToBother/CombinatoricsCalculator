package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula

data class MainScreenState(
    val formula: MutableState<Formula>,
    val allowRepetitions: MutableState<Boolean>,
    val n: MutableState<Int?> = mutableStateOf(null),
    val k: MutableState<Int?> = mutableStateOf(null),
    val m: MutableState<Int?> = mutableStateOf(null),
    val r: MutableState<Int?> = mutableStateOf(null)
) {
    fun cleatVariables() {
        n.value = null
        k.value = null
        m.value = null
        r.value = null
    }
}