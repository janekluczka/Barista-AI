package com.luczka.baristaai.ui.screens.home

data class FilterUiState(
    val id: String,
    val label: String
) {
    companion object {
        const val ALL_FILTER_ID: String = "all"
    }
}
