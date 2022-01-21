/*
 *
 *   Copyright (c) 2021  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.sdk.kotlin.lang

import com.google.gson.Gson
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-28 下午6:41
 **/

///////////////////////////////////////////////////////////////////////////
// Boolean
///////////////////////////////////////////////////////////////////////////

fun Boolean.toInt() = if (this) 1 else 0

fun Boolean.toBooleanString() = if (this) "true" else "false"

///////////////////////////////////////////////////////////////////////////
// String
///////////////////////////////////////////////////////////////////////////

fun String.toBoolean() = this == "true"

fun String.isUrl() = this.startsWith("http://", true) || this.startsWith("https://", true)

fun String.removeUnusedDecimalPoint(): String {
    var str = this
    if (str.indexOf(".") > 0) {
        str = str.replace("0+?$".toRegex(), "")
        str = str.replace("[.]$".toRegex(), "")
    }
    return str
}

fun String.getCharAt(string: String, showTimes: Int): Int {
    //这里是获取"/"符号的位置
    val slashMatcher: Matcher = Pattern.compile(string).matcher(this)
    var mIdx = 0
    while (slashMatcher.find()) {
        mIdx++
        //当"/"符号第三次出现的位置
        if (mIdx == showTimes) {
            break
        }
    }
    return slashMatcher.start()
}

///////////////////////////////////////////////////////////////////////////
// ArrayList<>
///////////////////////////////////////////////////////////////////////////

fun <E> List<E>.toArrayString(): String {
    var result = ""
    for (i in this) {
        result += i
        if (this.indexOf(i) != this.lastIndex) result += ","
    }
    return result
}

///////////////////////////////////////////////////////////////////////////
// MutableList<>
///////////////////////////////////////////////////////////////////////////

fun <E> MutableList<E>.toArrayList(): ArrayList<E> {
    val result = arrayListOf<E>()
    result.addAll(this)
    return result
}

///////////////////////////////////////////////////////////////////////////
// Int
///////////////////////////////////////////////////////////////////////////

fun Int.toBoolean() = this == 1

///////////////////////////////////////////////////////////////////////////
// Object
///////////////////////////////////////////////////////////////////////////

val <T : Any> T.TAG: String
    get() {
        return javaClass.simpleName
    }

fun <T : Any?> T.toJson(): String {
    return Gson().toJson(this) as String
}

fun <T : Any> T.fromJson(json: String): T {
    return Gson().fromJson(json, this::class.java)
}

/**
 * 三目
 */
fun <T : Any> threeEyes(condition: Boolean, ifTrue: T, ifFalse: T): T =
    if (condition) ifTrue else ifFalse

fun <T : Any> T.threeEyes(condition: Boolean, ifTrue: T.() -> Unit, ifFalse: T.() -> Unit): T {
    if (condition) ifTrue() else ifFalse()
    return this
}

inline fun <T : Any> T.threeEyes(
    condition: T.() -> Boolean, noinline ifTrue: (T.() -> Unit)? = null,
    noinline ifFalse: (T.() -> Unit)? = null
): T {
    if (condition()) {
        ifTrue?.invoke(this)
    } else {
        ifFalse?.invoke(this)
    }
    return this
}

inline fun <T : Any?> T.ifNotNull(run: (T) -> Unit): T {
    if (this != null) run(this)
    return this
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Any.cast(): T {
    return this as T
}


@Suppress("UNCHECKED_CAST")
fun <T : Any> Any?.castIfNull(ifNull: T): T {
    if (this == null) return ifNull
    return this as T
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Any?.castNullable(): T? {
    if (this == null) return null
    return this as T
}