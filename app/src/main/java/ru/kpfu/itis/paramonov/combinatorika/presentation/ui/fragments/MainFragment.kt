package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.fragments

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.presentation.base.BaseFragment
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.CombinationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PlacementsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.UrnSchemeRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components.BaseDropdownMenu
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.intents.MainScreenIntent
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.state.MainScreenState
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.theme.CombinatoricsTheme
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.viewmodel.MainViewModel
import ru.noties.jlatexmath.JLatexMathDrawable

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
                allowRepetitions = mutableStateOf(false)
            ))
        }

        LaunchedEffect(screenState.allowRepetitions.value, screenState.n.value,
            screenState.k.value) {
            viewModel.onIntent(MainScreenIntent.OnClearResult)
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
            Formula.COMBINATIONS -> handleCombinations(screenState)
            Formula.URN_SCHEME -> handleUrnScheme(screenState)
            else -> {}
        }
    }

    private fun handlePlacements(screenState: MainScreenState, onFail: () -> Unit = {}) {
        screenState.n.value?.let { n ->
            screenState.k.value?.let { k ->
                viewModel.onIntent(MainScreenIntent.OnGetResult(
                    PlacementsRequest(n = n, k = k,
                        allowRepetitions = screenState.allowRepetitions.value)
                ))
            }
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
            screenState.cleatVariables()
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
            screenState.cleatVariables()
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
                Text(text = (result as MainViewModel.FormulaResult.Success).getValue().toString())
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
    fun InputSection(
        modifier: Modifier = Modifier,
        prefix: String,
        onInput: (String) -> Unit = {}
    ) {
        var text by remember { mutableStateOf("") }

        TextField(
            modifier = modifier,
            value = text,
            onValueChange = { txt: String ->
                text = txt
                onInput(txt)
            },
            visualTransformation = getInputSectionVisualTransformation(prefix),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.LightGray,
                unfocusedContainerColor = Color.LightGray,
                cursorColor = Color.Gray,
                focusedIndicatorColor = Color.Gray
            )
        )
    }

    private fun getInputSectionVisualTransformation(prefix: String): VisualTransformation {
        return object : VisualTransformation {
            override fun filter(text: AnnotatedString): TransformedText {
                val annotatedString = AnnotatedString.Builder().run {
                    append(prefix)
                    append(text)
                    toAnnotatedString()
                }

                val prefixOffsetTranslator = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return offset + prefix.length
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        return if (offset >= prefix.length) offset - prefix.length else 0
                    }
                }

                return TransformedText(annotatedString, prefixOffsetTranslator)
            }
        }
    }

    @Composable
    fun Latex(latex: String) {
        Image(bitmap = JLatexMathDrawable
            .builder(latex)
            .textSize(40f)
            .align(JLatexMathDrawable.ALIGN_LEFT)
            .build()
            .toBitmap()
            .asImageBitmap(), contentDescription = null)
    }


    companion object {
        const val START_FRAGMENT_TAG = "MAIN_FRAGMENT"
    }
}
