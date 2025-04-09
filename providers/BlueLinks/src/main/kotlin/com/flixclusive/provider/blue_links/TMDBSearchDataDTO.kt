package com.flixclusive.provider.blue_links

data class TMDBSearchDataDTO(
    val page: Int,
    val results: List<TMDBSearchItemDTO>,
    val totalPages: Int
)