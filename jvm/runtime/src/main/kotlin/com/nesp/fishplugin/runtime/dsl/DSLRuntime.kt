package com.nesp.fishplugin.runtime.dsl

import com.nesp.fishplugin.runtime.IRuntime
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

abstract class DSLRuntime : IRuntime {

    ///////////////////////////////////////////////////////////////////////////
    // DSL API
    ///////////////////////////////////////////////////////////////////////////

    private fun processStr(str: String, processor: String): String {
        if (processor.startsWith("replace")) {
            val params = processor.split("replace(", limit = 2)[1].split(")")[0]
            return if (params.contains(",")) {
                str.replace(params.split(",")[0], params.split(",")[1])
            } else {
                str.replace(params, "")
            }
        } else if (processor.startsWith("split")) {
            val params = processor.substring(processor.indexOf("(") + 1, processor.length - 1)
            return if (params.contains(",")) {
                try {
                    str.split(params.split(",")[0])[params.split(",")[1].toInt()]
                } catch (e: Exception) {
                    str.split(params.split(",")[0])[0]
                }
            } else {
                str.split(params)[0]
            }
        } else if (processor.startsWith("concat")) {
            val params = processor.substring(processor.indexOf("(") + 1, processor.length - 1)
            return if (params.startsWith("!")) {
                params.substring(1, params.length) + str
            } else {
                str + params
            }
        } else if (processor.startsWith("trim")) {
            return str.trim()
        } else if (processor.startsWith("trimStart")) {
            return str.trimStart()
        } else if (processor.startsWith("trimEnd")) {
            return str.trimEnd()
        }
        return str
    }


    /**
     * class.main.0/
     */
    protected fun getElementsByDsl(parent: Element, dsl: String): Elements {
        val dslNodes = if (dsl.contains("/")) dsl.split("/") else listOf(dsl)
        var lastElementNode = parent

        return when {
            dslNodes.size >= 2 -> {
                for (i in 0..dslNodes.size - 2) {
                    lastElementNode = getNodeElementsByDsl(lastElementNode, dslNodes[i])[0]
                }

                // The last
                getNodeElementsByDsl(lastElementNode, dslNodes[dslNodes.size - 1])
            }
            dslNodes.size == 1 -> getNodeElementsByDsl(lastElementNode, dslNodes[0])
            else -> Elements()
        }
    }

    /**
     *  class.main.0
     */
    protected fun getNodeElementsByDsl(parent: Element, nodeDsl: String): Elements {
        val nodeInfo = nodeDsl.trimStart().trimEnd().split(".")

        val key: String
        val value: String
        var index: Int = -1

        when (nodeInfo.size) {
            3 -> {
                key = nodeInfo[0]
                value = nodeInfo[1]
                index = nodeInfo[2].toIntOrNull() ?: -1
            }
            2 -> {
                key = nodeInfo[0]
                value = nodeInfo[1]
            }
            else -> {
                throw RuntimeException("dsl format error! nodeInfoSize = ${nodeInfo.size}")
            }
        }

        when (key) {
            "class" -> {
                return if (index != -1) {
                    // begin with 1
                    val realIndex = if (index == 0) 0 else index - 1
                    Elements(parent.getElementsByClass(value)[realIndex])
                } else {
                    parent.getElementsByClass(value)
                }
            }
            "tag" -> {
                return if (index != -1) {
                    // begin with 1
                    val realIndex = if (index == 0) 0 else index - 1
                    Elements(parent.getElementsByTag(value)[realIndex])
                } else {
                    parent.getElementsByTag(value)
                }
            }
            "id" -> {
                return Elements(parent.getElementById(value))
            }
            else -> {
                return Elements()
            }
        }
    }

    protected fun getValue(element: Element?, key: String): String {
        if (element == null) return ""
        return when (key) {
            "textContent" -> element.text()
            "text" -> element.ownText()
            "href", "data-src", "src" -> element.absUrl(key)
            else -> element.attr(key)
        }.trimStart().trimEnd()
    }
}