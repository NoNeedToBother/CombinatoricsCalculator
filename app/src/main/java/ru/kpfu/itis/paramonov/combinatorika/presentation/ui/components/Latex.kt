package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import ru.noties.jlatexmath.JLatexMathDrawable

@Composable
fun Latex(latex: String) {
    Image(bitmap = JLatexMathDrawable
        .builder(latex)
        .textSize(40f)
        .align(JLatexMathDrawable.ALIGN_LEFT)
        .build()
        .toBitmap()
        .asImageBitmap(),
        contentDescription = null)
}