package com.nesp.fishplugin.runtime.js

import com.nesp.fishplugin.core.utils.LruCache
import com.nesp.fishplugin.runtime.AbsRuntime
import java.lang.Runtime as JavaRuntime

abstract class JsRuntime : AbsRuntime() {

    ///////////////////////////////////////////////////////////////////////////
    // Html
    ///////////////////////////////////////////////////////////////////////////

    protected val htmlDocumentStringCache: LruCache<String/*Url*/, String/*Html*/>

    init {
        val maxSize = JavaRuntime.getRuntime().maxMemory() / 8
        htmlDocumentStringCache = LruCache(maxSize.toInt())
    }

}