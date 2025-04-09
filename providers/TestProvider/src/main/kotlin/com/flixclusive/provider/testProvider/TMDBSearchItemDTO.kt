package com.flixclusive.provider.testProvider

import com.flixclusive.model.film.Film
//import com.flixclusive.model.film.util.FilmType
import com.flixclusive.model.film.FilmSearchItem
import com.flixclusive.model.film.Genre
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
    fun toFilmSearchItem(film: Film): FilmSearchItem {
        val movie = Genre(id=1, name = "Movie")
        val tv = Genre(id=2, name = "TV Show")

//        val genreName = when (mediaType) {
//            FilmType.MOVIE.type -> movie //"Movie"
//            FilmType.TV_SHOW.type -> tv //"TV Show"
//            else -> movie
//        }

        val genreNames = listOf<Genre>(movie, tv)

        return FilmSearchItem(
            id = null,
            providerId = "TMDB",
            filmType = film.filmType,
            homePage = homePage,
            title = title,
            posterImage = posterImage,
            tmdbId = tmdbId,
            genres = genreNames
        )
    }
}