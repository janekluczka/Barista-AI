package com.luczka.baristaai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonWithLoader(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    val textStyle = LocalTextStyle.current
    val loaderSize = rememberLoaderSize(textStyle)

    val textAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        label = "textAlpha"
    )
    val loaderAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        label = "loaderAlpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors,
        contentPadding = contentPadding
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                modifier = Modifier.alpha(textAlpha)
            )
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(loaderSize)
                    .alpha(loaderAlpha)
            )
        }
    }
}

@Composable
private fun rememberLoaderSize(textStyle: TextStyle): Dp {
    val density = LocalDensity.current
    return remember(textStyle, density) {
        with(density) {
            textStyle.lineHeight.toDp()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonWithLoaderPreview() {
    MaterialTheme {
        ButtonWithLoader(
            text = "Log in",
            onClick = {},
            isLoading = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonWithLoaderLoadingPreview() {
    MaterialTheme {
        ButtonWithLoader(
            text = "Log in",
            onClick = {},
            isLoading = true
        )
    }
}
