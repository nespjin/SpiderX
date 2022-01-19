package com.nesp.fishplugin.core.data

/**
 * The Dom selector language
 */
open class DSL {

    companion object {
        const val PREFIX_FISH_DSL = "fdsl:"
        const val PREFIX_SELECTOR = "selector:"
        const val PREFIX_XPATH = "xpath:"
        const val PREFIX_REGEX = "regex:"

        const val DSL_TYPE_FISH_DSL = 0
        const val DSL_TYPE_SELECTOR = 1
        const val DSL_TYPE_XPATH = 2
        const val DSL_TYPE_REGEX = 3

        fun getDslType(dsl: String): Int {
            when {
                dsl.startsWith(PREFIX_FISH_DSL) -> return DSL_TYPE_FISH_DSL
                dsl.startsWith(PREFIX_SELECTOR) -> return DSL_TYPE_SELECTOR
                dsl.startsWith(PREFIX_XPATH) -> return DSL_TYPE_XPATH
                dsl.startsWith(PREFIX_REGEX) -> return DSL_TYPE_REGEX
            }
            return -1
        }

        fun removeDslTypePrefix(dsl: String): String {
            val len = when {
                dsl.startsWith(PREFIX_FISH_DSL) -> PREFIX_FISH_DSL.length
                dsl.startsWith(PREFIX_SELECTOR) -> PREFIX_SELECTOR.length
                dsl.startsWith(PREFIX_XPATH) -> PREFIX_XPATH.length
                dsl.startsWith(PREFIX_REGEX) -> PREFIX_REGEX.length
                else -> 0
            }
            return dsl.substring(len)
        }
    }
}