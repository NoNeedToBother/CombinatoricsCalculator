package ru.kpfu.itis.paramonov.combinatorika.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import ru.kpfu.itis.paramonov.combinatorika.R
import ru.kpfu.itis.paramonov.combinatorika.databinding.FragmentMainBinding
import ru.noties.jlatexmath.JLatexMathDrawable

class FormulaHandler(private val context : Context?, private val binding: FragmentMainBinding) {
    fun handle(formula : String) {
        when(formula) {
            "Placements" -> handlePlacements()
            "Permutations" -> handlePermutations()
            "Combinations" -> handleCombinations()
            "Urn scheme" -> handleUrnScheme()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handlePlacements() {
        with(binding) {
            varN.visibility = View.VISIBLE
            varK.visibility = View.VISIBLE
            chkBoxRepetitions.visibility = View.VISIBLE

            val latexNoRepet = "\\mathnormal{A}_{n}^{k}: \\frac{n!}{(n - k)!} = "
            val latexRepet = "\\bar{\\mathnormal{A}}_{n}^{k}: n^{k} = "

            drawLatex(latexNoRepet)

            chkBoxRepetitions.setOnClickListener {
                tvRes.text = ""
                it as CheckBox
                if (it.isChecked) drawLatex(latexRepet)
                else drawLatex(latexNoRepet)
            }

            btnRes.setOnClickListener {
                val n = etN.text.toString()
                val k = etK.text.toString()

                if (n.isEmpty() || k.isEmpty()) showToast(R.string.missing_variables)
                else if (n.toInt() < 0 || k.toInt() < 0) showToast(R.string.negative_vars)
                else if ((n.toInt() < k.toInt()) && !chkBoxRepetitions.isChecked) showToast(R.string.invalid_arguments)
                else {
                    if (chkBoxRepetitions.isChecked) {
                        val res = MathHelper.placements(n.toInt(), k.toInt(), true)
                        tvRes.text = res.toString()
                    } else {
                        val res = MathHelper.placements(n.toInt(), k.toInt(), false)
                        tvRes.text = res.toString()
                    }

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handlePermutations() {
        with(binding) {
            varN.visibility = View.VISIBLE
            chkBoxRepetitions.visibility = View.VISIBLE

            val latexNoRepet = "\\mathnormal{P}_{n}: n! = "
            val latexRepet = "\\mathnormal{P}_{n}(n_{1},...n_{k}): \\frac{n!}{n_{1}!...n_{k}!} = "
            drawLatex(latexNoRepet)

            chkBoxRepetitions.setOnClickListener {
                tvRes.text = ""
                it as CheckBox
                if (it.isChecked) {
                    varN1Nk.visibility = View.VISIBLE
                    drawLatex(latexRepet)
                }
                else {
                    drawLatex(latexNoRepet)
                    varN1Nk.visibility = View.GONE
                }
            }

            btnRes.setOnClickListener {
                val n = etN.text.toString()
                val nVars = etN1ToNk.text.toString().split(",")

                if (n.isEmpty() || (chkBoxRepetitions.isChecked && nVars.isEmpty())) showToast(R.string.missing_variables)
                else if (n.toInt() < 0) showToast(R.string.negative_vars)
                else {
                    if (chkBoxRepetitions.isChecked) {
                        val nIntVars = checkNVars(n.toInt(), nVars) ?: return@setOnClickListener
                        val res = MathHelper.permutations(n.toInt(), true, *nIntVars)
                        tvRes.text = res.toString()
                    } else {
                        val res = MathHelper.permutations(n.toInt(), false)
                        tvRes.text = res.toString()
                    }

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

            val latexRepet = "\\bar{\\mathnormal{C}}_{n}^{k}: \\mathrm{C}_{n+k-1}^{k} = "
            val latexNoRepet = "\\mathnormal{C}_{n}^{k}: \\frac{n!}{k!(n-k)!} = "

            drawLatex(latexNoRepet)

            chkBoxRepetitions.setOnClickListener {
                tvRes.text = ""
                it as CheckBox
                if (it.isChecked) drawLatex(latexRepet)
                else drawLatex(latexNoRepet)
            }

            btnRes.setOnClickListener {
                val n = etN.text.toString()
                val k = etK.text.toString()

                if (n.isEmpty() || k.isEmpty()) showToast(R.string.missing_variables)
                else if (n.toInt() < 0 || k.toInt() < 0) showToast(R.string.negative_vars)
                else if ((n.toInt() < k.toInt()) && !(chkBoxRepetitions.isChecked)) showToast(R.string.invalid_arguments)
                else {
                    if (chkBoxRepetitions.isChecked) {
                        val res = MathHelper.combinations(n.toInt(), k.toInt(), true)
                        tvRes.text = res.toString()
                    } else {
                        val res = MathHelper.combinations(n.toInt(), k.toInt(), false)
                        tvRes.text = res.toString()
                    }
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

            val latexAll = "\\mathnormal{P(A)} : \\frac{\\mathnormal{C}_{m}^{k}}{\\mathnormal{C}_{n}^{k}} = "
            val latexR = "\\mathnormal{P(A)} : \\frac{\\mathnormal{C}_{m}^{r}\\mathnormal{C}_{n-m}^{k-r}}{\\mathnormal{C}_{n}^{k}} = "

            radioBtnR.setOnClickListener {
                varR.visibility = View.VISIBLE
                tvRes.text = ""
                radioBtnAll.isChecked = false
                drawLatex(latexR)
            }

            radioBtnAll.setOnClickListener {
                varR.visibility = View.GONE
                tvRes.text = ""
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

                if (n.isEmpty() || m.isEmpty() || k.isEmpty() || (radioBtnR.isChecked && r.isEmpty())) showToast(R.string.missing_variables)
                else if (n.toInt() < 0 || m.toInt() < 0 || k.toInt() < 0 || (radioBtnR.isChecked && r.toInt() < 0)) showToast(R.string.negative_vars)
                else {
                    if (k.toInt() >= m.toInt() || m.toInt() >= n.toInt()) {
                        showToast(R.string.invalid_arguments)
                        return@setOnClickListener
                    }
                    if (radioBtnR.isChecked) {
                        if (r.toInt() >= k.toInt()) {
                            showToast(R.string.invalid_arguments)
                            return@setOnClickListener
                        }
                        val res = MathHelper.urnScheme(n.toInt(), m.toInt(), k.toInt(), r.toInt())
                        tvRes.text = res.toPlainString()
                    }
                    else {
                        val res = MathHelper.urnScheme(n.toInt(), m.toInt(), k.toInt())
                        tvRes.text = res.toPlainString()
                    }
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

    private fun checkNVars(n : Int, nVars : List<String>) : IntArray? {
        for (nVar in nVars) {
            if (!nVar.isDigitsOnly()) {
                showToast(R.string.not_number)
                return null
            }
        }

        val nIntVars = ArrayList<Int>()
        for (nVar in nVars) {
            nIntVars.add(nVar.toInt())
        }

        var sumNVars = 0

        for (nVar in nIntVars) {
            sumNVars += nVar
        }

        if (sumNVars != n) {
            showToast(R.string.vars_n_not_summing_up)
            return null
        }
        else {
            val intArray = IntArray(nIntVars.size)
            for (i in nIntVars.indices) {
                intArray[i] = nIntVars[i]
            }
            return intArray
        }
    }
    private fun showToast(message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}