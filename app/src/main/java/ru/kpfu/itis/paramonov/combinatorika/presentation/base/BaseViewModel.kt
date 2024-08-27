package ru.kpfu.itis.paramonov.combinatorika.presentation.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<I>: ViewModel() {

    abstract fun onIntent(intent: I)
}