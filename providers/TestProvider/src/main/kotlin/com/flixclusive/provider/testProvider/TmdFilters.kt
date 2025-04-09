package com.flixclusive.provider.testProvider

import com.flixclusive.provider.filter.Filter
import com.flixclusive.provider.filter.FilterGroup
import com.flixclusive.provider.filter.FilterList
import kotlin.collections.first


private const val TMDB_FILTER_LABEL = "Media type"
internal const val FILTER_ALL = 0
internal const val FILTER_MOVIE = 1
internal const val FILTER_TV_SHOW = 2


class TmdbFilters(
    options: List<String>
) : FilterGroup(
    name = TMDB_FILTER_LABEL,
    Filter.Select(
        name = TMDB_FILTER_LABEL,
        options = options,
        state = FILTER_MOVIE
    ),
) {
    companion object {
        // For instant instantiation
        fun getTmdbFilters(): FilterList
                = FilterList(
            TmdbFilters(
                options = listOf("Movie", "TV Show")
            ),
        )
    }

    fun getMediaTypeFilter(): Int
            = first().state as Int
}