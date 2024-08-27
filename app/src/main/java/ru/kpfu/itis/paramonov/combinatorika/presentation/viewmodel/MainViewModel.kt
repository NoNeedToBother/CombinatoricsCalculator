package ru.kpfu.itis.paramonov.combinatorika.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kpfu.itis.paramonov.combinatorika.presentation.base.BaseViewModel
import ru.kpfu.itis.paramonov.combinatorika.presentation.intents.MainScreenIntent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): BaseViewModel<MainScreenIntent>() {
    override fun onIntent(intent: MainScreenIntent) {
        when (intent) {
            is MainScreenIntent.OnFormulaChosen -> println("hello!")
        }
    }
}