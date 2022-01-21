package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.data.Plugin

object Grammar {

    /**
     * Checks plugin grammar
     */
    @JvmStatic
    fun checkGrammar(plugin: Plugin, isParent: Boolean = false): GrammarCheckResult {
        if (plugin.parent != null) {
            if (plugin.parent !is String && plugin.parent !is Plugin) {
                return GrammarCheckResult(
                    GrammarCheckResult.LEVEL_ERROR,
                    "The type of parent plugin is not supported"
                )
            }

            if (plugin.parent is Plugin) {
                val checkParentGrammar = checkGrammar(plugin.parent as Plugin, true)
                if (checkParentGrammar.level != GrammarCheckResult.LEVEL_NONE) {
                    return checkParentGrammar
                }
            }
        }

        if (plugin.name.trim().isEmpty() && !isParent) {
            return GrammarCheckResult(GrammarCheckResult.LEVEL_ERROR, "name cannot empty")
        }

        if (plugin.id.trim().isEmpty() && !isParent) {
            return GrammarCheckResult(GrammarCheckResult.LEVEL_ERROR, "id cannot empty")
        }

        if (plugin.version.trim().isEmpty()) {
            if (!isParent) {
                return GrammarCheckResult(GrammarCheckResult.LEVEL_ERROR, "version cannot empty")
            }
        } else {
            val plusCount = plugin.version.count { it == '+' }
            if (plusCount < 0 || plusCount > 1) {
                return GrammarCheckResult(
                    GrammarCheckResult.LEVEL_ERROR, """
                    wrong version format: ${plugin.version}
                    The sample: 1.0.0+1
                """.trimIndent()
                )
            }
        }

        if (plugin.runtime.trim().isEmpty() && !isParent) {
            return GrammarCheckResult(GrammarCheckResult.LEVEL_ERROR, "runtime cannot empty")
        }

        // do not check time
        // do not check tag

        if (plugin.deviceFlags !in 0x01..0x07) {
            if (!isParent || plugin.deviceFlags != -1) {
                return GrammarCheckResult(
                    GrammarCheckResult.LEVEL_ERROR,
                    "deviceFlags(${plugin.deviceFlags}) parse error"
                )
            }
        }

        return GrammarCheckResult(GrammarCheckResult.LEVEL_NONE, "Grammar check passed")
    }

    data class GrammarCheckResult(
        var level: Int, // 0 pass, 1 warning, 2 error
        var message: String = ""
    ) {
        companion object {
            const val LEVEL_NONE = 0
            const val LEVEL_WARNING = 1
            const val LEVEL_ERROR = 2
        }
    }

    /**
     * Replace variable name to value in [variableNameValue] in [text]
     *
     * Example:
     *
     * applyVariableValue("i like {{var}}", {"var":"her"}) = "i like her"
     */
    fun applyVariableValue(text: String, variableNameValue: Map<String, String>): String {
        val variables = lookupVariables(text)
        var result = text
        val replacedText = arrayListOf<String>()
        for (variable in variables) {
            if (replacedText.contains(variable.name)) continue
            if (variableNameValue.containsKey(variable.name)) {
                variable.value = variableNameValue[variable.name]
                result = result.replace("{{${variable.name}}}", variable.value.toString())
                replacedText.add(variable.name)
            }
        }
        return result
    }

    /**
     * Lookups all variable in [text], the variable wrapped with {{ and }}
     *
     * Example:
     * variable named a
     * {{a}}
     */
    fun lookupVariables(text: String): List<Variable> {
        val result = mutableListOf<Variable>()
        val regex = getVariableRegex()
        val resultSequence = regex.findAll(text)
        for (matchResult in resultSequence) {
            result.add(Variable(matchResult.value, null, matchResult.range))
        }
        return result
    }

    /**
     * Whether it has variable in [text]
     */
    fun hasVariable(text: String): Boolean {
        return getVariableRegex().matches(text)
    }

    private fun getVariableRegex(): Regex {
        val pattern = "(?<=\\{\\{).*?(?=}})"
        return Regex(pattern)
    }

}