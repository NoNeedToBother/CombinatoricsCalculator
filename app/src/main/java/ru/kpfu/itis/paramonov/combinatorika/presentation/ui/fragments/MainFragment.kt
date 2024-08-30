package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.fragments

import android.annotation.SuppressLint
import android.view.View
import android.widget.CheckBox
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.databinding.FragmentMainBinding
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

    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

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
    @Preview(showBackground = true)
    fun MainScreen() {
        
        val screenState by remember {
            mutableStateOf(MainScreenState(Formula.PLACEMENTS))
        }

        val formulas = listOf(
            Formula.PLACEMENTS, Formula.PERMUTATIONS, Formula.COMBINATIONS, Formula.URN_SCHEME
        )
        Scaffold(
            modifier = Modifier.padding(12.dp),
            topBar = { FormulasDropdown(
                items = formulas,
                screenState = screenState
            ) }
        ) { innerPadding ->
            Content( modifier = Modifier.padding(innerPadding.calculateTopPadding()))
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
            onSelected = { formula -> screenState.formula = formula }
        )
    }

    @Composable
    fun Content(modifier: Modifier = Modifier) {

    }

    private fun rollback() {
        with(binding) {
            varN.visibility = View.GONE
            etN.text.clear()
            varK.visibility = View.GONE
            etK.text.clear()
            varM.visibility = View.GONE
            etM.text.clear()
            varR.visibility = View.GONE
            etR.text.clear()
            varN1Nk.visibility = View.GONE
            etN1ToNk.text.clear()

            tvRes.setText(R.string.empty_str)

            radioGrpUrn.visibility = View.GONE
            radioBtnAll.isChecked = false
            radioBtnR.isChecked = false

            chkBoxRepetitions.visibility = View.GONE
            chkBoxRepetitions.setOnClickListener {  }
            chkBoxRepetitions.isChecked = false

            ivLatex.setImageDrawable(null)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handlePlacements() {
        with(binding) {
            varN.visibility = View.VISIBLE
            varK.visibility = View.VISIBLE
            chkBoxRepetitions.visibility = View.VISIBLE

            val latexNoRepetitions = getString(R.string.placements_latex_no_repetitions)
            val latexRepetitions = getString(R.string.placements_latex_repetitions)

            drawLatex(latexNoRepetitions)

            chkBoxRepetitions.setOnClickListener {
                tvRes.text = ""
                it as CheckBox
                if (it.isChecked) drawLatex(latexRepetitions)
                else drawLatex(latexNoRepetitions)
            }

            btnRes.setOnClickListener {
                val n = etN.text.toString()
                val k = etK.text.toString()

                if (n.isEmpty() || k.isEmpty()) showToast(R.string.missing_variables)
                else {
                    viewModel.onIntent(MainScreenIntent.OnGetResult(
                        PlacementsRequest(n.toInt(), k.toInt(), chkBoxRepetitions.isChecked)
                    ))
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handlePermutations() {
        with(binding) {
            varN.visibility = View.VISIBLE
            chkBoxRepetitions.visibility = View.VISIBLE

            val latexNoRepetitions = getString(R.string.permutations_latex_no_repetitions)
            val latexRepetitions = getString(R.string.permutations_latex_repetitions)
            drawLatex(latexNoRepetitions)

            chkBoxRepetitions.setOnClickListener {
                tvRes.text = ""
                it as CheckBox
                if (it.isChecked) {
                    varN1Nk.visibility = View.VISIBLE
                    drawLatex(latexRepetitions)
                }
                else {
                    drawLatex(latexNoRepetitions)
                    varN1Nk.visibility = View.GONE
                }
            }

            btnRes.setOnClickListener {
                val n = etN.text.toString()
                val nVars =
                    if (chkBoxRepetitions.isChecked)
                        etN1ToNk.text.toString().split(",").map { str -> str.toInt() }
                    else null

                if (n.isEmpty() || (chkBoxRepetitions.isChecked && nVars?.isEmpty() == true)) showToast(R.string.missing_variables)
                else {
                    viewModel.onIntent(MainScreenIntent.OnGetResult(
                        PermutationsRequest(n.toInt(), nVars, chkBoxRepetitions.isChecked)
                    ))
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleCombinations() {
        with(binding) {
            varN.visibility = View.VISIBLE
            varK.visibility = View.VISIBLE
            chkBoxRepetitions.visibility = View.VISIBLE

            val latexRepetitions = getString(R.string.combinations_latex_repetitions)
            val latexNoRepetitions = getString(R.string.combinations_latex_no_repetitions)

            drawLatex(latexNoRepetitions)

            chkBoxRepetitions.setOnClickListener {
                tvRes.text = ""
                it as CheckBox
                if (it.isChecked) drawLatex(latexRepetitions)
                else drawLatex(latexNoRepetitions)
            }

            btnRes.setOnClickListener {
                val n = etN.text.toString()
                val k = etK.text.toString()

                if (n.isEmpty() || k.isEmpty()) showToast(R.string.missing_variables)
                else {
                    viewModel.onIntent(MainScreenIntent.OnGetResult(
                        CombinationsRequest(n.toInt(), k.toInt(), chkBoxRepetitions.isChecked)
                    ))
                }
            }
        }
    }

    private fun handleUrnScheme() {
        with(binding) {
            radioGrpUrn.visibility = View.VISIBLE
            varN.visibility = View.VISIBLE
            varM.visibility = View.VISIBLE
            varK.visibility = View.VISIBLE

            val latexAll = getString(R.string.urn_scheme_latex_all)
            val latexR = getString(R.string.urn_scheme_latex_r)

            radioBtnR.setOnClickListener {
                varR.visibility = View.VISIBLE
                tvRes.setText(R.string.empty_str)
                drawLatex(latexR)
            }

            radioBtnAll.setOnClickListener {
                varR.visibility = View.GONE
                tvRes.setText(R.string.empty_str)
                drawLatex(latexAll)
            }

            btnRes.setOnClickListener {
                if (!radioBtnR.isChecked && !radioBtnAll.isChecked) {
                    showToast(R.string.option_not_chosen)
                    return@setOnClickListener
                }

                val n = etN.text.toString()
                val m = etM.text.toString()
                val k = etK.text.toString()
                val r = if (radioBtnR.isChecked) etR.text.toString()
                    else null

                if (n.isEmpty() || m.isEmpty() || k.isEmpty() ||
                    (radioBtnR.isChecked && r?.isEmpty() == true))
                    showToast(R.string.missing_variables)
                else {
                    viewModel.onIntent(MainScreenIntent.OnGetResult(
                        UrnSchemeRequest(n.toInt(), m.toInt(), k.toInt(), r?.toInt())
                    ))
                }
            }
        }
    }

    private fun drawLatex(latex : String) {
        val drawable = JLatexMathDrawable.builder(latex)
            .textSize(40f)
            .align(JLatexMathDrawable.ALIGN_LEFT)
            .build()

        binding.ivLatex.setImageDrawable(drawable)
    }

    companion object {
        const val START_FRAGMENT_TAG = "MAIN_FRAGMENT"
    }
}
