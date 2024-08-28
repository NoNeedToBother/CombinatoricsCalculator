package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.fragments

import android.annotation.SuppressLint
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.databinding.FragmentMainBinding
import ru.kpfu.itis.paramonov.combinatorika.presentation.base.BaseFragment
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.CombinationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PermutationsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.PlacementsRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.UrnSchemeRequest
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.intents.MainScreenIntent
import ru.kpfu.itis.paramonov.combinatorika.presentation.ui.viewmodel.MainViewModel
import ru.noties.jlatexmath.JLatexMathDrawable

@AndroidEntryPoint
class MainFragment: BaseFragment() {

    private val formulas = arrayOf(
        Formula.PLACEMENTS, Formula.PERMUTATIONS, Formula.COMBINATIONS, Formula.URN_SCHEME
    )

    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

    private val viewModel: MainViewModel by viewModels()

    override fun composeView(): ComposeView {
        return ComposeView(requireContext()).apply {
            setContent {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                fontSize = 20.sp,
                text = "Hello world!"
            )
        }
    }

    override fun initView() {
        with(binding) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, formulas)
            adapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
            spinnerFormulas.adapter = adapter
            spinnerFormulas.onItemSelectedListener = getOnItemSelectedListener()
        }
    }

    override fun observeData() {
        viewModel.currentFormulaFlow.onEach { formula ->
            formula?.let { handleFormula(formula) }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.formulaResultFlow.onEach { result ->
            result?.let {
                when (result) {
                    is MainViewModel.FormulaResult.Success -> binding.tvRes.text = result.getValue().toString()
                    is MainViewModel.FormulaResult.Failure -> showToast(result.getException().message ?:
                        getString(R.string.computations_fail))
                }
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun getOnItemSelectedListener() = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            rollback()
            (parent?.getItemAtPosition(pos) as Formula?)?.let { formula ->
                viewModel.onIntent(MainScreenIntent.OnFormulaChosen(formula))
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun handleFormula(formula : Formula) {
        when(formula) {
            Formula.PLACEMENTS -> handlePlacements()
            Formula.PERMUTATIONS -> handlePermutations()
            Formula.COMBINATIONS -> handleCombinations()
            Formula.URN_SCHEME -> handleUrnScheme()
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
