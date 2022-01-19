package com.nesp.fishplugin.runtime.dsl

import com.nesp.fishplugin.core.data.DSL
import com.nesp.fishplugin.runtime.IRuntime
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements

abstract class DSLRuntime : IRuntime {

    fun select(parentElement: Element, dsl: String): Elements {
        val dslWithoutTypePrefix = DSL.removeDslTypePrefix(dsl)
        val s = (if (dslWithoutTypePrefix.contains("@")) {
            dslWithoutTypePrefix.split("@")[0]
        } else {
            dslWithoutTypePrefix
        }).trim()
        return when (DSL.getDslType(dsl.trim())) {
            DSL.DSL_TYPE_FISH_DSL -> {
                getElementsByFishDsl(parentElement, s)
            }
            DSL.DSL_TYPE_SELECTOR -> {
                parentElement.select(s)
            }
            DSL.DSL_TYPE_XPATH -> {
                parentElement.selectXpath(s)
            }
            else -> {
                // Default
                getElementsByFishDsl(parentElement, s)
            }
        }
    }

    fun getValueByDsl(parentElement: Element, dsl: String): String {
        val dslType = DSL.getDslType(dsl.trim())

        val infos = DSL.removeDslTypePrefix(dsl).split("@")
        val s = infos[0].trim()

        val result: String = try {
            when (dslType) {
                DSL.DSL_TYPE_SELECTOR -> {
                    if (infos.size < 2) return ""
                    getValueBySelector(parentElement, s, infos[1])
                }
                DSL.DSL_TYPE_XPATH -> {
                    val key = if (infos.size < 2) return "" else infos[1]
                    getValueByXPath(parentElement, s, key)
                }
                DSL.DSL_TYPE_FISH_DSL -> {
                    if (infos.size < 2) return ""
                    getValueByFishDsl(parentElement, s, infos[1])
                }
                else -> {
                    if (infos.size < 2) return ""
                    getValueBySelector(parentElement, s, infos[1])
                }
            }
        } catch (e: Exception) {
            println("DSLRuntime.getValueByDsl: $dsl \n" + e.localizedMessage)
            ""
        }

        // Run dsl methods
        var methodsIndex = -1
        if (infos.size == 3) {
            methodsIndex = 2
        } else if (infos.size == 2) {
            if (dslType == DSL.DSL_TYPE_XPATH) methodsIndex = 1
        }

        if (methodsIndex != -1) return runDslMultiMethod(result, infos[methodsIndex])

        return result
    }

    private fun getValueByFishDsl(parentElement: Element, s: String, key: String): String {
        val element = if (s != "") {
            getElementsByFishDsl(parentElement, s)[0]
        } else {
            parentElement
        }
        return getValue(element, key)
    }

    private fun getValueByXPath(parentElement: Element, s: String, key: String): String {
        val element: Element?

        try {
            val textNodes = parentElement.selectXpath(s, TextNode::class.java)
            if (!textNodes.isNullOrEmpty()) {
                return textNodes[0].text()
            }
        } catch (ignored: Exception) {
        }

        if (key.isEmpty()) return ""

        val elements = parentElement.selectXpath(s)
        element = if (elements.isEmpty()) {
            parentElement
        } else {
            elements[0]
        }
        val element1 = if (s != "") {
            element
        } else {
            parentElement
        }
        return getValue(element1, key)
    }

    private fun getValueBySelector(parentElement: Element, s: String, key: String): String {
        if (key.isEmpty()) return ""
        val element = if (s != "") {
            parentElement.selectFirst(s)
        } else {
            parentElement
        }
        return getValue(element, key)
    }

    ///////////////////////////////////////////////////////////////////////////
    // DSL API
    ///////////////////////////////////////////////////////////////////////////

    protected fun runDslMultiMethod(`$this`: String, methods: String): String {
        var result = `$this`
        if (methods.contains(";")) {
            val methodList = methods.split(";")
            for (method in methodList) {
                result = runDslMethod(result, method)
            }
        } else {
            result = runDslMethod(`$this`, methods)
        }
        return result
    }

    protected fun runDslMethod(`$this`: String, method: String): String {
        if (method.startsWith("replace")) {
            val params = method.split("replace(", limit = 2)[1].split(")")[0]
            return if (params.contains(",")) {
                `$this`.replace(params.split(",")[0], params.split(",")[1])
            } else {
                `$this`.replace(params, "")
            }
        } else if (method.startsWith("split")) {
            val params = method.substring(method.indexOf("(") + 1, method.length - 1)
            return if (params.contains(",")) {
                try {
                    `$this`.split(params.split(",")[0])[params.split(",")[1].toInt()]
                } catch (e: Exception) {
                    `$this`.split(params.split(",")[0])[0]
                }
            } else {
                `$this`.split(params)[0]
            }
        } else if (method.startsWith("concat")) {
            val params = method.substring(method.indexOf("(") + 1, method.length - 1)
            return if (params.startsWith("!")) {
                params.substring(1, params.length) + `$this`
            } else {
                `$this` + params
            }
        } else if (method.startsWith("trim")) {
            return `$this`.trim()
        } else if (method.startsWith("trimStart")) {
            return `$this`.trimStart()
        } else if (method.startsWith("trimEnd")) {
            return `$this`.trimEnd()
        }
        return `$this`
    }

    /**
     * class.main.0/
     */
    protected fun getElementsByFishDsl(parent: Element, dsl: String): Elements {
        val dslNodes = if (dsl.contains("/")) dsl.split("/") else listOf(dsl)
        var lastElementNode = parent

        return when {
            dslNodes.size >= 2 -> {
                for (i in 0..dslNodes.size - 2) {
                    lastElementNode = getNodeElementsByFishDsl(lastElementNode, dslNodes[i])[0]
                }

                // The last
                getNodeElementsByFishDsl(lastElementNode, dslNodes[dslNodes.size - 1])
            }
            dslNodes.size == 1 -> getNodeElementsByFishDsl(lastElementNode, dslNodes[0])
            else -> Elements()
        }
    }

    /**
     *  class.main.0
     */
    protected fun getNodeElementsByFishDsl(parent: Element, nodeDsl: String): Elements {
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