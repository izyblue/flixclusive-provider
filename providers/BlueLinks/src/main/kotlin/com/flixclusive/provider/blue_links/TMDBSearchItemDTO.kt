package com.flixclusive.provider.blue_links

import com.flixclusive.model.film.util.FilmType
import com.flixclusive.model.film.FilmSearchItem
import com.google.gson.annotations.SerializedName

data class TMDBSearchItemDTO(
    @SerializedName("name", alternate = ["title"]) val title: String,
    @SerializedName("homepage") val homePage: String? = null,
    @SerializedName("poster_path") val posterImage: String? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("id") val tmdbId: Int,
    // ... other needed info
) {
    /**
     * An instance mapper method
     */
    fun toFilmSearchItem(): FilmSearchItem {
        val genreName = when (mediaType) {
            FilmType.MOVIE.type -> "Movie"
            FilmType.TV_SHOW.type -> "TV Show"
            else -> "N/A"
        }

        return FilmSearchItem(
            id = null,
            providerId = "TMDB",
            filmType = filmType,
            homePage = homePage,
            title = title,
            posterImage = posterImage,
            tmdbId = tmdbId
        )
    }
}