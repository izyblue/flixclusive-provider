package com.flixclusive.provider.testProvider

import com.flixclusive.model.film.TvShow
import com.flixclusive.model.film.common.tv.Season

data class TMDBTvShowDTO(
    val tmdbId: Int,
    val title: String,
    val posterPath: String? = null,
    val homepage: String? = null,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val seasons: List<Season>,
    // ... other needed info
) {
    fun toTvShow(): TvShow {
        return TvShow(
            id = null,
            title = title,
            posterImage = posterPath,
            homePage = homepage,
            totalEpisodes = numberOfEpisodes,
            totalSeasons = numberOfSeasons,
            seasons = seasons,
            tmdbId = tmdbId,
            providerId = "TMDB"
            // ...
        )
    }
}