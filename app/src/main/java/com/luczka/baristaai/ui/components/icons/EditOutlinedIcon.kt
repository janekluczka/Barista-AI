package com.luczka.baristaai.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EditOutlinedIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = Icons.Outlined.Edit,
        contentDescription = "Edit",
        modifier = modifier,
        tint = tint
    )
}

@Preview(showBackground = true)
@Composable
private fun EditOutlinedIconPreview() {
    EditOutlinedIcon()
}
