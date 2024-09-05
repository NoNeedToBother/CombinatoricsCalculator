package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.fragments

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.presentation.base.BaseFragment
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.CombinationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PermutationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PlacementsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.UrnSchemeRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components.BaseDropdownMenu
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components.InputSection
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components.Latex
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.intents.MainScreenIntent
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.state.MainScreenState
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.theme.CombinatoricsTheme
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.viewmodel.MainViewModel

@AndroidEntryPoint
class MainFragment: BaseFragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun composeView(): ComposeView {
        return ComposeView(requireContext()).apply {
            setContent {
                CombinatoricsTheme {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true, heightDp = 600, widthDp = 320)
    fun MainScreen(modifier: Modifier = Modifier) {
        val screenState by remember {
            mutableStateOf(MainScreenState(
                formula = mutableStateOf(Formula.PLACEMENTS),
                allowRepetitions = mutableStateOf(false),
                urnSchemeRItems = mutableStateOf(false)
            ))
        }

        LaunchedEffect(screenState.allowRepetitions.value, screenState.n.value,
            screenState.k.value, screenState.m.value, screenState.r.value,
            screenState.urnSchemeRItems.value) {

            viewModel.onIntent(MainScreenIntent.OnClearResult)
        }

        val result by viewModel.formulaResultFlow.collectAsState()

        LaunchedEffect(key1 = result) {
            if (result != null && result is MainViewModel.FormulaResult.Failure) {
                showToast(
                    (result as MainViewModel.FormulaResult.Failure).getException().message
                        ?: getString(R.string.default_err_message)
                )
            }
        }

        val formulas = listOf(
            Formula.PLACEMENTS, Formula.PERMUTATIONS, Formula.COMBINATIONS, Formula.URN_SCHEME
        )

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FormulasDropdown(
                items = formulas,
                screenState = screenState
            )
            Content(screenState = screenState)
            Button(onClick = { onGetResultClicked(screenState) },
                modifier = Modifier.padding(4.dp)) {
                Text(text = stringResource(id = R.string.get_res),
                    style = MaterialTheme.typography.headlineMedium)
            }
        }
    }

    private fun onGetResultClicked(screenState: MainScreenState) {
        when(screenState.formula.value) {
            Formula.PLACEMENTS -> handlePlacements(screenState)
            Formula.PERMUTATIONS -> handlePermutations(screenState)
            Formula.COMBINATIONS -> handleCombinations(screenState)
            Formula.URN_SCHEME -> handleUrnScheme(screenState)
        }
    }

    private fun handlePlacements(screenState: MainScreenState) {
        screenState.n.value?.let { n ->
            screenState.k.value?.let { k ->
                viewModel.onIntent(MainScreenIntent.OnGetResult(
                    PlacementsRequest(n = n, k = k,
                        allowRepetitions = screenState.allowRepetitions.value)
                ))
            }
        }
    }

    private fun handlePermutations(screenState: MainScreenState) {
        screenState.n.value?.let { n ->
            viewModel.onIntent(MainScreenIntent.OnGetResult(
                PermutationsRequest(n = n, nVars = screenState.nVars.value,
                    allowRepetitions = screenState.allowRepetitions.value)
            ))
        }
    }

    private fun handleCombinations(screenState: MainScreenState) {
        screenState.n.value?.let { n ->
            screenState.k.value?.let { k ->
                viewModel.onIntent(MainScreenIntent.OnGetResult(
                    CombinationsRequest(n = n, k = k,
                        allowRepetitions = screenState.allowRepetitions.value)
                ))
            }
        }
    }

    private fun handleUrnScheme(screenState: MainScreenState) {
        screenState.n.value?.let { n ->
            screenState.k.value?.let { k ->
                screenState.m.value?.let { m ->
                    viewModel.onIntent(MainScreenIntent.OnGetResult(
                        UrnSchemeRequest(
                            n = n, m = m, k = k, r = screenState.r.value
                        )
                    ))
                }
            }
        }
    }

    @Composable
    fun FormulasDropdown(
        modifier: Modifier = Modifier,
        items: List<Formula>,
        screenState: MainScreenState
    ) {
        BaseDropdownMenu(
            modifier = modifier,
            items = items,
            onSelected = { formula -> screenState.formula.value = formula }
        )
    }

    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        screenState: MainScreenState
    ) {
        when(screenState.formula.value) {
            Formula.PLACEMENTS -> PlacementsInputSection(modifier, screenState)
            Formula.PERMUTATIONS -> PermutationsInputSection(modifier, screenState)
            Formula.COMBINATIONS -> CombinationsInputSection(modifier, screenState)
            Formula.URN_SCHEME -> UrnSchemeInputSection(modifier, screenState)
        }
    }

    @Composable
    fun PlacementsInputSection(
        modifier: Modifier = Modifier,
        screenState: MainScreenState
    ) {
        val onInputN: (String) -> Unit = { n ->
            if (n.isNotBlank() && n.isDigitsOnly()) screenState.n.value = n.toInt()
            else screenState.n.value = null
        }
        val onInputK: (String) -> Unit = { k ->
            if (k.isNotBlank() && k.isDigitsOnly()) screenState.k.value = k.toInt()
            else screenState.k.value = null
        }

        LaunchedEffect(Unit) {
            screenState.clearVariables()
        }

        Column(
            modifier = modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            AllowRepetitions(
                checked = screenState.allowRepetitions.value,
                onChecked = { checked -> screenState.allowRepetitions.value = checked }
            )

            Spacer(modifier = Modifier.height(4.dp))

            ResultAndLatex(
                latexCondition = screenState.allowRepetitions.value,
                latexSatisfy = R.string.placements_latex_repetitions,
                latexNotSatisfy = R.string.placements_latex_no_repetitions)

            InputSectionN(modifier = Modifier.padding(4.dp), onInput = onInputN)
            InputSectionK(modifier = Modifier.padding(4.dp), onInput = onInputK)
        }
    }

    @Composable
    fun PermutationsInputSection(
        modifier: Modifier = Modifier,
        screenState: MainScreenState
    ) {
        val onInputN: (String) -> Unit = { n ->
            if (n.isNotBlank() && n.isDigitsOnly()) screenState.n.value = n.toInt()
            else screenState.n.value = null
        }
        val onInputNVars: (String) -> Unit = { nVars ->
            if (nVars.isNotBlank()) {
                val temp = mutableListOf<Int>()
                var valid = true
                nVars.split(",").forEach { n ->
                    if (n.isNotBlank() && n.isDigitsOnly()) temp.add(n.toInt())
                    else if (n.isNotBlank()) {
                        showToast(R.string.invalid_arguments)
                        valid = false
                    }
                }
                if (valid) {
                    screenState.nVars.value = temp
                }
            } else screenState.nVars.value = null
        }

        LaunchedEffect(Unit) {
            screenState.clearVariables()
        }

        Column(
            modifier = modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AllowRepetitions(
                checked = screenState.allowRepetitions.value,
                onChecked = { checked -> screenState.allowRepetitions.value = checked }
            )

            Spacer(modifier = Modifier.height(4.dp))

            ResultAndLatex(
                latexCondition = screenState.allowRepetitions.value,
                latexSatisfy = R.string.permutations_latex_repetitions,
                latexNotSatisfy = R.string.permutations_latex_no_repetitions)

            InputSectionN(modifier = Modifier.padding(4.dp), onInput = onInputN)
            if (screenState.allowRepetitions.value)
                InputSectionNVars(modifier = Modifier.padding(4.dp), onInput = onInputNVars)
        }
    }

    @Composable
    fun CombinationsInputSection(
        modifier: Modifier = Modifier,
        screenState: MainScreenState
    ) {
        val onInputN: (String) -> Unit = { n ->
            if (n.isNotBlank() && n.isDigitsOnly()) screenState.n.value = n.toInt()
            else screenState.n.value = null
        }
        val onInputK: (String) -> Unit = { k ->
            if (k.isNotBlank() && k.isDigitsOnly()) screenState.k.value = k.toInt()
            else screenState.k.value = null
        }

        LaunchedEffect(Unit) {
            screenState.clearVariables()
            viewModel.onIntent(MainScreenIntent.OnClearResult)
        }

        Column(
            modifier = modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AllowRepetitions(
                checked = screenState.allowRepetitions.value,
                onChecked = { checked -> screenState.allowRepetitions.value = checked }
            )
            Spacer(modifier = Modifier.height(4.dp))

            ResultAndLatex(
                latexCondition = screenState.allowRepetitions.value,
                latexSatisfy = R.string.combinations_latex_repetitions,
                latexNotSatisfy = R.string.combinations_latex_no_repetitions)

            InputSectionN(modifier = Modifier.padding(4.dp), onInput = onInputN)
            InputSectionK(modifier = Modifier.padding(4.dp), onInput = onInputK)
        }
    }

    @Composable
    fun UrnSchemeInputSection(
        modifier: Modifier = Modifier,
        screenState: MainScreenState
    ) {
        val onInputN: (String) -> Unit = { n ->
            if (n.isNotBlank() && n.isDigitsOnly()) screenState.n.value = n.toInt()
            else screenState.n.value = null
        }
        val onInputK: (String) -> Unit = { k ->
            if (k.isNotBlank() && k.isDigitsOnly()) screenState.k.value = k.toInt()
            else screenState.k.value = null
        }
        val onInputM: (String) -> Unit = { m ->
            if (m.isNotBlank() && m.isDigitsOnly()) screenState.m.value = m.toInt()
            else screenState.m.value = null
        }
        val onInputR: (String) -> Unit = { r ->
            if (r.isNotBlank() && r.isDigitsOnly()) screenState.r.value = r.toInt()
            else screenState.r.value = null
        }

        LaunchedEffect(Unit) {
            screenState.clearVariables()
            viewModel.onIntent(MainScreenIntent.OnClearResult)
        }

        Column(
            modifier = modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UrnSchemeRadioGroup(screenState = screenState)
            Spacer(modifier = Modifier.height(4.dp))

            ResultAndLatex(
                latexCondition = screenState.urnSchemeRItems.value,
                latexSatisfy = R.string.urn_scheme_latex_r,
                latexNotSatisfy = R.string.urn_scheme_latex_all)

            InputSectionN(modifier = Modifier.padding(4.dp), onInput = onInputN)
            InputSectionK(modifier = Modifier.padding(4.dp), onInput = onInputK)
            InputSectionM(modifier = Modifier.padding(4.dp), onInput = onInputM)
            if (screenState.urnSchemeRItems.value)
                InputSectionR(modifier = Modifier.padding(4.dp), onInput = onInputR)
        }
    }

    @Composable
    fun UrnSchemeRadioGroup(
        modifier: Modifier = Modifier,
        screenState: MainScreenState
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = !screenState.urnSchemeRItems.value,
                    onClick = { screenState.urnSchemeRItems.value = false }
                )
                Text(
                    text = stringResource(id = R.string.all_items),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = screenState.urnSchemeRItems.value,
                    onClick = { screenState.urnSchemeRItems.value = true }
                )
                Text(
                    text = stringResource(id = R.string.r_items),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    @Composable
    fun AllowRepetitions(
        modifier: Modifier = Modifier,
        checked: Boolean,
        onChecked: (Boolean) -> Unit = {}
    ) {
        Row(modifier = modifier,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.allow_repetitions))
            Checkbox(checked = checked,
                onCheckedChange = onChecked
            )
        }
    }

    @Composable
    fun ResultAndLatex(
        modifier: Modifier = Modifier,
        latexCondition: Boolean = true,
        @StringRes latexSatisfy: Int,
        @StringRes latexNotSatisfy: Int? = null
    ) {
        val result by viewModel.formulaResultFlow.collectAsState()
        val scrollState = rememberScrollState()
        val clipboardManager = LocalClipboardManager.current

        Row(modifier = modifier,
            verticalAlignment = Alignment.CenterVertically) {
            if (latexCondition) {
                Latex(latex = stringResource(id = latexSatisfy))
            } else {
                latexNotSatisfy?.let {
                    Latex(latex = stringResource(id = latexNotSatisfy))
                }
            }
            if (result != null && result is MainViewModel.FormulaResult.Success) {
                Column(modifier = Modifier
                    .heightIn(max = 120.dp)
                    .verticalScroll(scrollState)) {
                    SelectionContainer {
                        Text(
                            text = (result as MainViewModel.FormulaResult.Success).getValue().toString(),
                            modifier = Modifier.semantics {
                                this.onClick {
                                    clipboardManager.setText(AnnotatedString(
                                        (result as MainViewModel.FormulaResult.Success).getValue().toString()
                                    ))
                                    true
                                }
                            }
                        )
                    }
                }
            }
        }

    }
    
    @Composable
    fun InputSectionN(modifier: Modifier = Modifier, onInput: (String) -> Unit = {}) {
        InputSection(
            modifier = modifier,
            prefix = stringResource(id = R.string.var_n),
            onInput = onInput
        )
    }

    @Composable
    fun InputSectionK(modifier: Modifier = Modifier, onInput: (String) -> Unit = {}) {
        InputSection(
            modifier = modifier,
            prefix = stringResource(id = R.string.var_k),
            onInput = onInput
        )
    }

    @Composable
    fun InputSectionM(modifier: Modifier = Modifier, onInput: (String) -> Unit = {}) {
        InputSection(
            modifier = modifier,
            prefix = stringResource(id = R.string.var_m),
            onInput = onInput
        )
    }

    @Composable
    fun InputSectionR(modifier: Modifier = Modifier, onInput: (String) -> Unit = {}) {
        InputSection(
            modifier = modifier,
            prefix = stringResource(id = R.string.var_r),
            onInput = onInput
        )
    }

    @Composable
    fun InputSectionNVars(modifier: Modifier = Modifier, onInput: (String) -> Unit = {}) {
        var text by remember { mutableStateOf("") }

        TextField(
            value = text, onValueChange = { value ->
                text = value
                onInput(value)
            },
            modifier = modifier,
            prefix = { Latex(latex = stringResource(id = R.string.n_vars_latex)) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.LightGray,
                unfocusedContainerColor = Color.LightGray,
                cursorColor = Color.Gray,
                focusedIndicatorColor = Color.Gray
            )
        )
    }

    companion object {
        const val START_FRAGMENT_TAG = "MAIN_FRAGMENT"
    }
}
