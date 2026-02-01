package com.luczka.baristaai.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AddIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add",
        modifier = modifier,
        tint = tint
    )
}

@Preview(showBackground = true)
@Composable
private fun AddIconPreview() {
    AddIcon()
}
