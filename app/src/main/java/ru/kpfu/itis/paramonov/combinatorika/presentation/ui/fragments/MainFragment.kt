package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.fragments

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.paramonov.combinatorika.presentation.base.BaseFragment
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.CombinationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PermutationsRequest
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
                allowRepetitions = false
            ))
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
            Button(onClick = { /*TODO*/ }) {}
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
            Formula.PLACEMENTS -> PlacementsInputSection()
            Formula.PERMUTATIONS -> PermutationsInputSection()
            Formula.COMBINATIONS -> CombinationsInputSection()
            Formula.URN_SCHEME -> UrnSchemeInputSection()
        }
    }
    
    @Composable
    fun PlacementsInputSection() {
        var allowRepetitions by remember {
            mutableStateOf(false)
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Allow repetitions")
                Checkbox(checked = false, onCheckedChange = { checked -> allowRepetitions = checked})
            }
        }
    }

    @Composable
    fun PermutationsInputSection() {

    }

    @Composable
    fun CombinationsInputSection() {

    }

    @Composable
    fun UrnSchemeInputSection() {

    }
    
    @Composable
    fun InputSectionN() {
        
    }

    @Composable
    fun InputSection(
        modifier: Modifier = Modifier,
        name: String,
        onInput: (String) -> Unit
    ) {
        

    }



    private fun latex(latex : String): Drawable {
        return JLatexMathDrawable.builder(latex)
            .textSize(40f)
            .align(JLatexMathDrawable.ALIGN_LEFT)
            .build()
    }

    companion object {
        const val START_FRAGMENT_TAG = "MAIN_FRAGMENT"
    }
}
