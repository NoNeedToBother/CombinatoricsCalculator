package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.state

import androidx.compose.runtime.MutableState
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula

data class MainScreenState(
    val formula: MutableState<Formula>,
    var allowRepetitions: Boolean,
    var n: Int? = null,
    var k: Int? = null
)