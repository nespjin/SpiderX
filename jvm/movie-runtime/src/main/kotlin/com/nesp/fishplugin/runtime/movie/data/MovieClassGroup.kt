package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

data class MovieClassGroup(
    val movieClasses: MutableList<MovieClass> = mutableListOf()
) : Serializable