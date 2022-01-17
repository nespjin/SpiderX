package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

data class MovieCategoryGroup(
    val movieClasses: MutableList<MovieCategory> = mutableListOf(),
    val title: String = ""
) : Serializable