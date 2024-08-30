package ru.kpfu.itis.paramonov.combinatorika.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> BaseDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<T>,
    onSelected: (T) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Surface(
        modifier = modifier.wrapContentSize(Alignment.TopCenter),
        shape = RoundedCornerShape(4.dp),
        color = Color.LightGray
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .clickable(onClick = { expanded = !expanded }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = items[selectedIndex].toString(),
                modifier = Modifier.width(128.dp),
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .rotate(rotationAngle)
            )
        }
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopCenter)
                .animateContentSize()
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentSize(Alignment.TopCenter)
            ) {
                items.forEachIndexed { index, _ ->
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex = index
                            expanded = false
                            onSelected(items[selectedIndex])
                        },
                        text = {
                            Text(items[index].toString())
                        }
                    )
                }
            }
        }
    }
}