package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.presentation.base.BaseViewModel
import ru.kpfu.itis.paramonov.combinatorika.presentation.exceptions.ComputationException
import ru.kpfu.itis.paramonov.combinatorika.presentation.exceptions.InvalidVariablesException
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.CombinationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PermutationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PlacementsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.UrnSchemeRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.intents.MainScreenIntent
import ru.kpfu.itis.paramonov.combinatorika.util.MathHelper
import ru.kpfu.itis.paramonov.combinatorika.util.ResourceManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resourceManager: ResourceManager,
    private val math: MathHelper
): BaseViewModel<MainScreenIntent>() {

    private val _formulaResultFlow = MutableStateFlow<FormulaResult?>(null)
    val formulaResultFlow: StateFlow<FormulaResult?> get() = _formulaResultFlow

    override fun onIntent(intent: MainScreenIntent) {
        when (intent) {
            is MainScreenIntent.OnGetResult -> onGetResultIntent(intent)
            is MainScreenIntent.OnClearResult -> _formulaResultFlow.value = null
        }
    }

    private val formulaResultNegativeVariablesFailure =
        FormulaResult.Failure(InvalidVariablesException(resourceManager.getString(R.string.negative_vars)))
    private val formulaResultInvalidVariablesFailure =
        FormulaResult.Failure(InvalidVariablesException(resourceManager.getString(R.string.invalid_arguments)))


    private fun onGetResultIntent(intent: MainScreenIntent.OnGetResult) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (intent.req) {
                    is PlacementsRequest -> handlePlacements(intent.req)
                    is PermutationsRequest -> handlePermutations(intent.req)
                    is CombinationsRequest -> handleCombinations(intent.req)
                    is UrnSchemeRequest -> handleUrnScheme(intent.req)
                }
            }
        }
    }

    private fun getExceptionFromComputations(ex: Throwable): Throwable {
        return if (ex is ComputationException && ex.message != null) ex
        else ComputationException(resourceManager.getString(R.string.computations_fail))
    }

    private fun handlePlacements(req: PlacementsRequest) {
        with(req) {
            if (n < 0 || k < 0) _formulaResultFlow.value = formulaResultNegativeVariablesFailure
            else if (n < k && !allowRepetitions) _formulaResultFlow.value = formulaResultInvalidVariablesFailure
            else {
                try {
                    val result = math.placements(n, k, allowRepetitions)
                    _formulaResultFlow.value = FormulaResult.Success(result)
                } catch (ex: Throwable) {
                    _formulaResultFlow.value = FormulaResult.Failure(getExceptionFromComputations(ex))
                    _formulaResultFlow.value = null
                }
            }
        }
    }

    private fun handlePermutations(req: PermutationsRequest) {
        with(req) {
            val shouldCheckVars = req.allowRepetitions && nVars != null
            if (n < 0 || (shouldCheckVars && nVars?.indexOfFirst { n -> n < 0 } != -1))
                _formulaResultFlow.value = formulaResultNegativeVariablesFailure
            else if (shouldCheckVars && nVars?.sum() != n)
                _formulaResultFlow.value = formulaResultInvalidVariablesFailure
            else {
                try {
                    val result = math.permutations(n, allowRepetitions, nVars)
                    _formulaResultFlow.value = FormulaResult.Success(result)
                } catch (ex: Throwable) {
                    _formulaResultFlow.value = FormulaResult.Failure(getExceptionFromComputations(ex))
                    _formulaResultFlow.value = null
                }
            }
        }
    }

    private fun handleCombinations(req: CombinationsRequest) {
        with(req) {
            if (n < 0 || k < 0) _formulaResultFlow.value = formulaResultNegativeVariablesFailure
            else if (n < k && !allowRepetitions) _formulaResultFlow.value = formulaResultInvalidVariablesFailure
            else {
                try {
                    val result = math.combinations(n, k, allowRepetitions)
                    _formulaResultFlow.value = FormulaResult.Success(result)
                } catch (ex: Throwable) {
                    _formulaResultFlow.value = FormulaResult.Failure(getExceptionFromComputations(ex))
                    _formulaResultFlow.value = null
                }
            }
        }
    }

    private fun handleUrnScheme(req: UrnSchemeRequest) {
        with(req) {
            if (n < 0 || m < 0 || k < 0 || (r != null && r < 0)) _formulaResultFlow.value = formulaResultNegativeVariablesFailure
            else if (k >= m || m >= n || (r != null && r >= k)) _formulaResultFlow.value = formulaResultInvalidVariablesFailure
            else {
                try {
                    val result = r?.let { math.urnScheme(n = n, k = k, m = m, r = it) }
                        ?: math.urnScheme(n = n, k = k, m = m)
                    _formulaResultFlow.value = FormulaResult.Success(result)
                } catch (ex: Throwable) {
                    _formulaResultFlow.value = FormulaResult.Failure(getExceptionFromComputations(ex))
                    _formulaResultFlow.value = null
                }
            }
        }
    }

    sealed interface FormulaResult: Result {
        class Success(private val result: Number): FormulaResult, Result.Success<Number> {
            override fun getValue(): Number = result
        }
        class Failure(private val ex: Throwable): FormulaResult, Result.Failure {
            override fun getException(): Throwable = ex
        }
    }
}