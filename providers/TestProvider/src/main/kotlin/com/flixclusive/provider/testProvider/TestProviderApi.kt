package com.flixclusive.provider.testProvider

import com.flixclusive.core.util.coroutines.AppDispatchers.Companion.withIOContext
import com.flixclusive.core.util.network.json.fromJson
import com.flixclusive.core.util.network.jsoup.asJsoup
import com.flixclusive.core.util.network.okhttp.request
import com.flixclusive.model.film.Film
import com.flixclusive.model.film.FilmMetadata
import com.flixclusive.model.film.FilmSearchItem
import com.flixclusive.model.film.SearchResponseData
import com.flixclusive.model.film.common.tv.Episode
import com.flixclusive.model.film.util.FilmType
import com.flixclusive.model.provider.ProviderCatalog
import com.flixclusive.model.provider.link.Flag
import com.flixclusive.model.provider.link.MediaLink
import com.flixclusive.model.provider.link.Stream
import com.flixclusive.provider.Provider
import com.flixclusive.provider.ProviderApi
import com.flixclusive.provider.filter.FilterList
import com.flixclusive.provider.testProvider.TmdbFilters.Companion.getTmdbFilters
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLDecoder


/**
 * An inheritance class for a [ProviderApi]. This will serve as the [Provider] api instance.
 *
 */

class TestProviderApi(
    client: OkHttpClient,
    provider: Provider
) : ProviderApi(client, provider) {

    val popularMovies = ProviderCatalog(
        name = "Trending Movies",
        url = "https://api.themoviedb.org/3/discover/movie" ,
        canPaginate = true,
        providerId = "Test Provider"
    )

    // A catalog with an image/logo
    val popularTvShows = ProviderCatalog(
        name = "Popular TV Shows",
        url = "https://api.themoviedb.org/3/discover/tv",
        canPaginate = true,
        image = "https://api.themoviedb.org/sample_icon.png",
        providerId = "Test Provider"
    )

    override val baseUrl: String get() = super.baseUrl
    override val testFilm: FilmMetadata get() = super.testFilm

    override val filters: FilterList get() = getTmdbFilters() //super.filters
    override val catalogs: List<ProviderCatalog>
        get() = listOf(
            popularMovies,
            popularTvShows
        )  //super.catalogs

//    override suspend fun getCatalogItems(
//        catalog: ProviderCatalog,
//        page: Int
//    ): SearchResponseData<FilmSearchItem> {
//        val dtoResponse = client
//            .request(url = parseCatalogUrl(catalog.url, page))
//            .execute()
//            .fromJson<TMDBSearchDataDTO>(
//                errorMessage = "Couldn't parse response data!"
//            )
//
//        val mappedResults = dtoResponse.results
//            .map { searchItemDto: TMDBSearchItemDTO ->
//                searchItemDto.toFilmSearchItem()
//            }
//
//        return SearchResponseData(
//            page = dtoResponse.page,
//            results = mappedResults,
//            hasNextPage = dtoResponse.page < dtoResponse.totalPages,
//            totalPages = dtoResponse.totalPages
//        )
//    }

    private fun parseCatalogUrl(url: String, page: Int): String {
        return url.plus("?").plus("page=").plus(page)
    }
    //= super.getCatalogItems(catalog, page)


    override suspend fun getMetadata(film: Film): FilmMetadata {
        val endpoint = when (film.filmType) {
            FilmType.MOVIE -> "https://api.themoviedb.org/3/movie/"
            FilmType.TV_SHOW -> "https://api.themoviedb.org/3/tv/"
        }

        val response = client
            .request(url = buildTmdbRequestUrl(endpoint, film.id))
            .execute()

        val errorMessage = "Couldn't parse response data!"

        val result: FilmMetadata = when (film.filmType) {
            FilmType.MOVIE -> {
                response
                    .fromJson<TMDBMovieDTO>(errorMessage = errorMessage)
                    .toMovie()
            }
            FilmType.TV_SHOW -> {
                response
                    .fromJson<TMDBTvShowDTO>(errorMessage = errorMessage)
                    .toTvShow()
            }
        }

        return result
    }
//        = super.getMetadata(film)

    private fun buildTmdbRequestUrl(url: String, id: String?): String {
        val token = token
        val newUrl = Request.Builder()
            .url(url.plus(id))
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ".plus(token))
            .build()
        return newUrl.toString()
    }

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
                url = "https://www.themoviedb.org/${mediaType}/${id}/watch?locale=PH"
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
        // = super.getLinks(watchId, film, episode, onLinkFound)

//    override suspend fun search(
//        title: String,
//        page: Int,
//        id: String?,
//        imdbId: String?,
//        tmdbId: Int?,
//        filters: FilterList
//    ): SearchResponseData<FilmSearchItem> {
//        val mediaType = (filters.first() as TmdbFilters).getMediaTypeFilter()
//        val endpoint = when (mediaType) {
//            FILTER_ALL -> "https://api.themoviedb.org/3/search/multi"
//            FILTER_TV_SHOW -> "https://api.themoviedb.org/3/search/tv"
//            FILTER_MOVIE -> "https://api.themoviedb.org/3/search/movie"
//            else -> "https://vidsrc.xyz/embed/movie"
//        }
//
//        val dtoResponse = client
//            .request(url = buildTmdbRequestUrl(endpoint, id))
//            .execute()
//            .fromJson<TMDBSearchDataDTO>(
//                errorMessage = "Couldn't parse response data!"
//            )
//
//        val mappedResults = dtoResponse.results
//            .map { searchItemDto: TMDBSearchItemDTO ->
//                searchItemDto.toFilmSearchItem()
//            }
//
//        return SearchResponseData(
//            page = dtoResponse.page,
//            results = mappedResults,
//            hasNextPage = dtoResponse.page < dtoResponse.totalPages,
//            totalPages = dtoResponse.totalPages
//        )
//    }
//        = super.search(title, page, id, imdbId, tmdbId, filters)

}