package ru.kpfu.itis.paramonov.combinatorika.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import ru.kpfu.itis.paramonov.combinatorika.databinding.FragmentMainBinding
import ru.kpfu.itis.paramonov.combinatorika.util.FormulaHandler

class MainFragment : Fragment() {
    private val formulas = arrayOf("Placements", "Permutations", "Combinations", "Urn scheme")

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private var formula : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        with(binding) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, formulas)
            adapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
            spinnerFormulas.adapter = adapter

            val handler = FormulaHandler(context, binding)
            spinnerFormulas.onItemSelectedListener = getOnItemSelectedListener(handler)

        }
    }

    private fun getOnItemSelectedListener(formulaHandler: FormulaHandler) = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            rollback()
            formula = parent?.getItemAtPosition(pos) as String?
            formula?.let {
                formulaHandler.handle(it)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun rollback() {
        with(binding) {
            varN.visibility = View.GONE
            etN.setText("")
            varK.visibility = View.GONE
            etK.setText("")
            varM.visibility = View.GONE
            etM.setText("")
            varR.visibility = View.GONE
            etR.setText("")
            varN1Nk.visibility = View.GONE
            etN1ToNk.setText("")

            tvRes.text = ""

            radioGrpUrn.visibility = View.GONE
            radioBtnAll.isChecked = false
            radioBtnR.isChecked = false

            chkBoxRepetitions.visibility = View.GONE
            chkBoxRepetitions.setOnClickListener {  }
            chkBoxRepetitions.isChecked = false

            ivLatex.setImageDrawable(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val START_FRAGMENT_TAG = "MAIN_FRAGMENT"
    }
}
