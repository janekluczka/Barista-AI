package com.luczka.baristaai.ui.screens.home

import com.luczka.baristaai.domain.model.BrewMethod

fun BrewMethod.toFilterUiState(): FilterUiState {
    return FilterUiState(
        id = id,
        label = name
    )
}
