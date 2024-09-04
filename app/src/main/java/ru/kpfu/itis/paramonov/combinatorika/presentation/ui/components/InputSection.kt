package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

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
        prefix = { Latex(latex = prefix) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.LightGray,
            unfocusedContainerColor = Color.LightGray,
            cursorColor = Color.Gray,
            focusedIndicatorColor = Color.Gray
        )
    )
}