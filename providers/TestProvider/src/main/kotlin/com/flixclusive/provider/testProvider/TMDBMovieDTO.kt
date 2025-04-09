package com.flixclusive.provider.testProvider

import com.flixclusive.model.film.Movie

data class TMDBMovieDTO(
    val tmdbId: Int,
    val title: String,
    val posterPath: String? = null,
    val homepage: String? = null,
    // ... other needed info
) {
    fun toMovie(): Movie {
        return Movie(
            id = null,
            title = title,
            posterImage = posterPath,
            homePage = homepage,
            tmdbId = tmdbId,
            providerId = "TMDB"
            // ...
        )
    }
}