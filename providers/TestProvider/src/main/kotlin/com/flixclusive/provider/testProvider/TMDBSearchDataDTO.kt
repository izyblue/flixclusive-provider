package com.flixclusive.provider.testProvider

data class TMDBSearchDataDTO(
    val page: Int,
    val results: List<TMDBSearchItemDTO>,
    val totalPages: Int
)