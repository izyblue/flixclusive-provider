package com.flixclusive.provider.blue_links

import com.flixclusive.core.util.coroutines.AppDispatchers.Companion.withIOContext
import com.flixclusive.core.util.network.jsoup.asJsoup
import com.flixclusive.core.util.network.okhttp.request
import com.flixclusive.model.film.Film
import com.flixclusive.model.film.FilmMetadata
import com.flixclusive.model.film.FilmSearchItem
import com.flixclusive.model.film.SearchResponseData
import com.flixclusive.model.film.common.tv.Episode
//import com.flixclusive.model.film.util.FilmType
import com.flixclusive.model.provider.ProviderCatalog
import com.flixclusive.model.provider.link.Flag
import com.flixclusive.model.provider.link.MediaLink
import com.flixclusive.model.provider.link.Stream
import com.flixclusive.provider.Provider
import com.flixclusive.provider.ProviderApi
//import com.flixclusive.provider.filter.Filter
//import com.flixclusive.provider.filter.FilterGroup
import com.flixclusive.provider.filter.FilterList
import okhttp3.OkHttpClient
import java.net.URLDecoder

//
//private const val TMDB_FILTER_LABEL = "Media type"
//internal const val FILTER_ALL = 0
//internal const val FILTER_TV_SHOW = 1
//internal const val FILTER_MOVIE = 2
//
//class TmdbFilters(
//    options: List<String>
//) : FilterGroup(
//    name = TMDB_FILTER_LABEL,
//    Filter.Select(
//        name = TMDB_FILTER_LABEL,
//        options = options,
//        state = FILTER_ALL
//    ),
//) {
//    companion object {
//        // For instant instantiation
//        fun getTmdbFilters(): FilterList
//                = FilterList(
//            TmdbFilters(
//                options = listOf("All", "TV Show", "Movie")
//            ),
//        )
//    }
//
//    fun getMediaTypeFilter(): Int
//            = first().state as Int
//}
//
//data class TMDBSearchDataDTO(
//    val page: Int,
//    val results: List<TMDBSearchItemDTO>,
//    val totalPages: Int
//)


class BlueLinksProviderApi(
    client: OkHttpClient,
    provider: Provider
) : ProviderApi(client, provider) {
    override val baseUrl: String get() = super.baseUrl
    override val testFilm: FilmMetadata get() = super.testFilm
    override val catalogs: List<ProviderCatalog> get() = super.catalogs
    override val filters: FilterList get() = super.filters

    override suspend fun getCatalogItems(
        catalog: ProviderCatalog,
        page: Int
    ): SearchResponseData<FilmSearchItem>
        = super.getCatalogItems(catalog, page)


    override suspend fun getMetadata(film: Film): FilmMetadata
        = super.getMetadata(film)

    override suspend fun getLinks(
        watchId: String,
        film: FilmMetadata,
        episode: Episode?,
        onLinkFound: (MediaLink) -> Unit
    ) {
        val mediaType = film.filmType.type
        val id = film.tmdbId

        require(mediaType == "movie" || mediaType == "tv") {
            "Invalid media type: $mediaType"
        }

        val response = withIOContext {
            client.request(
                url = "https://vidsrc.net/embed/${mediaType}?tmdb=${id}"
            ).execute()
        }

        val html = response.asJsoup()

        html.select("div.ott_provider li a").forEach { element ->
            val href = element.attr("href")
            val title = element.attr("title")
            val logoUrl = element.select("img").attr("src")

            var providerName = title.split(" on ").lastOrNull()?.trim()
            if (providerName == null) {
                providerName = "Unknown Provider"
            }

            // Extract the URL from the 'r' parameter in the href
            val url = href.split("&r=").getOrNull(1)
                ?.split("&")
                ?.firstOrNull()

            if (url != null) {
                val decodedUrl = URLDecoder.decode(url, "UTF-8")
                onLinkFound(
                    Stream(
                        name = providerName,
                        description = title,
                        url = decodedUrl,
                        flags = setOf(
                            Flag.Trusted(
                                name = providerName,
                                logo = logoUrl
                            )
                        )
                    )
                )
            }
        }
    }

    override suspend fun search(
        title: String,
        page: Int,
        id: String?,
        imdbId: String?,
        tmdbId: Int?,
        filters: FilterList,
    ): SearchResponseData<FilmSearchItem>
        = super.search(title, page, id, imdbId, tmdbId, filters)

//    override suspend fun getMetadata(film: Film): FilmMetadata {
//        val endpoint = when (film.filmType) {
//            FilmType.MOVIE -> "https://api.themoviedb.org/3/movie"
//            FilmType.TV_SHOW -> "https://api.themoviedb.org/3/tv"
//        }
//
//        val response = client
//            .request(url = buildTmdbRequestUrl(endpoint, title, page))
//            .execute()
//
//        val errorMessage = "Couldn't parse response data!"
//        val result: FilmMetadata = when (film.filmType) {
//            FilmType.MOVIE -> {
//                response
//                    .fromJson<TMDBMovieDTO>(errorMessage = errorMessage)
//                    .toMovie()
//            }
//            FilmType.TV_SHOW -> {
//                response
//                    .fromJson<TMDBTvShowDTO>(errorMessage = errorMessage)
//                    .toTvShow()
//            }
//        }
//
//        return result
//    }

//    override suspend fun search(
//        title: String,
//        page: Int,
//        id: String?,
//        imdbId: String?,
//        tmdbId: Int?,
//        filters: FilterList,
//    ): SearchResponseData<FilmSearchItem> {
//        val mediaType = (filters.first() as TmdbFilters).getMediaTypeFilter()
//        val endpoint = when (mediaType) {
//            FILTER_ALL -> "https://api.themoviedb.org/3/search/multi"
//            FILTER_TV_SHOW -> "https://api.themoviedb.org/3/search/tv"
//            FILTER_MOVIE -> "https://api.themoviedb.org/3/search/movie"
//            else -> "N/A"
//        }
//
//        val dtoResponse = client
//            .request(url = buildTmdbRequestUrl(endpoint, title, page))
//            .execute()
//            .fromJson<TMDBSearchDataDTO>(
//                errorMessage = "Couldn't parse response data!"
//            )
//
//        val mappedResults = dtoResponse.results
//            .map { searchItemDto ->
//                searchItemDto.toFilmSearchItem()
//            }
//
//        return SearchResponseData(
//            page = dtoResponse.page,
//            results = mappedResults,
//            hasNextPage = dtoResponse.page < dtoResponse.total_pages,
//            totalPages = dtoResponse.total_pages
//        )
//    }
//
//    private fun buildTmdbRequestUrl(endpoint: kotlin.String, title: kotlin.String, page: kotlin.Int) {
//
//    }

}

//data class TMDBMovieDTO(
//    val tmdbId: Int,
//    val title: String,
//    val pPath: String? = null,
//    val homepage: String? = null,
//    // ... other needed info
//) {
//    fun toMovie(): Movie {
//        return Movie(
//            id = null,
//            title = title,
//            posterImage = pPath,
//            homePage = homepage,
//            tmdbId = tmdbId,
//            providerName = "TMDB"
//            // ...
//        )
//    }
//}
//
//data class TMDBTvShowDTO(
//    val tmdbId: Int,
//    val title: String,
//    val pPath: String? = null,
//    val homepage: String? = null,
//    val noe: Int,
//    val nos: Int,
//    val seasons: List<Season>,
//    // ... other needed info
//) {
//    fun toTvShow(): TvShow {
//        return TvShow(
//            id = null,
//            title = title,
//            posterImage = pPath,
//            homePage = homepage,
//            totalEpisodes = noe,
//            totalSeasons = nos,
//            seasons = seasons,
//            tmdbId = tmdbId,
//            providerName = "TMDB"
//            // ...
//        )
//    }
//}

