package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.intents

import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.GetResultRequest

sealed interface MainScreenIntent {
    data class OnGetResult(val req: GetResultRequest): MainScreenIntent
}