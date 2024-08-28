package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.databinding.FragmentMainBinding
import ru.kpfu.itis.paramonov.combinatorika.presentation.model.Formula
import ru.kpfu.itis.paramonov.combinatorika.util.MathHelper
import ru.noties.jlatexmath.JLatexMathDrawable
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val formulas = arrayOf(
        Formula.PLACEMENTS, Formula.PERMUTATIONS, Formula.COMBINATIONS, Formula.URN_SCHEME
    )

    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

    private var formula : Formula? = null

    @Inject
    lateinit var mathHelper: MathHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        with(binding) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, formulas)
            adapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
            spinnerFormulas.adapter = adapter
            spinnerFormulas.onItemSelectedListener = getOnItemSelectedListener()

        }
    }

    private fun getOnItemSelectedListener() = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            rollback()
            formula = parent?.getItemAtPosition(pos) as Formula?
            formula?.let { handleFormula(it) }
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

    //TODO("Refactor to view model")

    fun handleFormula(formula : Formula) {
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
                val nVars = etN1ToNk.text.toString().split(",")

                if (n.isEmpty() || (chkBoxRepetitions.isChecked && nVars.isEmpty())) showToast(R.string.missing_variables)
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
                radioBtnAll.isChecked = false
                drawLatex(latexR)
            }

            radioBtnAll.setOnClickListener {
                varR.visibility = View.GONE
                tvRes.setText(R.string.empty_str)
                radioBtnR.isChecked = false
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
                val r = etR.text.toString()

                if (n.isEmpty() || m.isEmpty() || k.isEmpty() || (radioBtnR.isChecked && r.isEmpty()))
                    showToast(R.string.missing_variables)
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

    private fun showToast(message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val START_FRAGMENT_TAG = "MAIN_FRAGMENT"
    }
}
