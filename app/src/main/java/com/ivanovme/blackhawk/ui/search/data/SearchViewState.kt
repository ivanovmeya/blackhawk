package com.ivanovme.blackhawk.ui.search.data

data class SearchViewState(
    val searchQuery: String,
    val isLoading: Boolean,
    val searchResults: List<CityData>,
    val error: String
) {
    companion object {
        fun empty(): SearchViewState = SearchViewState(
            searchQuery = "",
            isLoading = false,
            searchResults = emptyList(),
            error = ""
        )
    }
}