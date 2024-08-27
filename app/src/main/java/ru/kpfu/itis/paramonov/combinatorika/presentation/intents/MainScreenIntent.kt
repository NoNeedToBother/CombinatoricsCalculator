package ru.kpfu.itis.paramonov.combinatorika.presentation.intents

import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula

sealed interface MainScreenIntent {
    data class OnFormulaChosen(val formula: Formula): MainScreenIntent
}