package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.data.DSL
import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Plugin

object Compiler {

    fun compileFromDisk(path: String): CompileResult {
        // Load plugin
        val loadResult = Loader.loadPluginFromDisk(path)
        if (loadResult.state != Loader.LoadResult.STATE_SUCCESS) {
            return CompileResult(CompileResult.CODE_FAILED, loadResult.message)
        }
        return doCompile(loadResult.plugin!!)
    }

    fun compileFromUrl(url: String): CompileResult {
        // Load plugin
        val loadResult = Loader.loadPluginFromUrl(url)
        if (loadResult.state != Loader.LoadResult.STATE_SUCCESS) {
            return CompileResult(CompileResult.CODE_FAILED, loadResult.message)
        }
        return doCompile(loadResult.plugin!!)
    }

    fun compile(plugin: Plugin): CompileResult {
        // Load plugin
        val loadResult = Loader.load(plugin)
        if (loadResult.state != Loader.LoadResult.STATE_SUCCESS) {
            return CompileResult(CompileResult.CODE_FAILED, loadResult.message)
        }
        return doCompile(plugin)
    }

    /**
     * Compile plugin
     */
    private fun doCompile(plugin: Plugin): CompileResult {

        // Check parent
        if (plugin.parent != null && plugin.parent !is Plugin) {
            return CompileResult(CompileResult.CODE_FAILED, "prent is not supported")
        }

        // Check plugin grammar
        val grammarCheckResult = Grammar.checkGrammar(plugin)
        if (grammarCheckResult.level == Grammar.GrammarCheckResult.LEVEL_ERROR) {
            return CompileResult(CompileResult.CODE_FAILED, grammarCheckResult.message)
        }
        if (grammarCheckResult.level == Grammar.GrammarCheckResult.LEVEL_WARNING) {
            println("Warning: Grammar Check: " + grammarCheckResult.message)
        }

        // Lookup and apply variable
        val lookupAndApplyVariableErrorMsg = lookupAndApplyVariable(plugin)
        if (lookupAndApplyVariableErrorMsg.isNotEmpty()) {
            return CompileResult(CompileResult.CODE_FAILED, lookupAndApplyVariableErrorMsg)
        }

        return CompileResult(CompileResult.CODE_SUCCESS)
    }

    /**
     * Lookup all variable reference and replace it with value.
     * @return error if failed.
     */
    private fun lookupAndApplyVariable(plugin: Plugin): String {
        if (plugin.parent != null) {
            val lookupAndApplyVariableOfParent = lookupAndApplyVariable(plugin.parent as Plugin)
            if (lookupAndApplyVariableOfParent.isNotEmpty()) {
                return lookupAndApplyVariableOfParent
            }
        }

        // Name
        var lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin.FILED_NAME_NAME)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Version
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin.FILED_NAME_VERSION)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Runtime
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin.FILED_NAME_RUNTIME)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Time
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin.FILED_NAME_TIME)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Introduction
        lookupAndApplyVariableResult =
            lookupAndApplyVariable(plugin, Plugin.FILED_NAME_INTRODUCTION)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Pages
        lookupAndApplyVariableResult =
            lookupAndApplyVariable(plugin, Plugin.FILED_NAME_PAGES)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        return ""
    }

    private fun lookupAndApplyVariable(plugin: Plugin, fieldName: String): String {
        val fieldValue = plugin.getFieldValue(fieldName)

        if (fieldValue == null || (fieldValue is String && fieldValue.isEmpty())) {
            return "Not support for field $fieldName"
        }

        if (fieldValue !is String) return ""

        val variablesOfField = Grammar.lookupVariables(fieldValue)
        if (variablesOfField.isNotEmpty()) {
            if (Variable.exitsVariable(fieldName, variablesOfField)) {
                return "The $fieldName field contains itself"
            }

            if (plugin.ref.isNullOrEmpty()) {
                return "Cant find any variable on $fieldName"
            }

            for (variable in variablesOfField) {
                if (variable.name.trim().isEmpty()) {
                    return "Exits empty variable on $fieldName"
                }

                variable.value = plugin.findRefVariable(variable.name)
                    ?: return "Variable $fieldName not exits in ref"

                if (fieldName == Plugin.FILED_NAME_NAME
                    || fieldName == Plugin.FILED_NAME_VERSION
                    || fieldName == Plugin.FILED_NAME_RUNTIME
                    || fieldName == Plugin.FILED_NAME_TIME
                    || fieldName == Plugin.FILED_NAME_INTRODUCTION
                ) {
                    if (!Variable.isPrimitiveType(variable.value!!)) {
                        return "Only support primitive type variable for field $fieldName"
                    }

                    val variableNameAndValue = hashMapOf<String, String>()
                    variableNameAndValue[variable.name] = variable.value!!.toString()
                    plugin.setFieldValue(
                        fieldName,
                        Grammar.applyVariableValue(fieldValue, variableNameAndValue)
                    )
                } else if (fieldName == Plugin.FILED_NAME_PAGES) {
                    for (page in plugin.pages) {
                        lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_REF_URL)
                        lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_URL)
                        lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_JS)
                        lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_DSL)
                    }
                } else {
                    return "Variable in $fieldName is not supported"
                }
            }
        }
        return ""
    }

    private fun lookupAndApplyVariable(plugin: Plugin, page: Page, fieldName: String): String {
        if (fieldName == Page.FIELD_NAME_ID) {
            return "Not support for field $fieldName"
        }

        val fieldValue = page.getFieldValue(fieldName)

        if (fieldValue == null || (fieldValue is String && fieldValue.isEmpty())) {
            return "Not support for field $fieldName"
        }

        if (fieldValue !is String) return ""

        val variablesOfField = Grammar.lookupVariables(fieldValue)
        if (variablesOfField.isNotEmpty()) {
            if (Variable.exitsVariable(fieldName, variablesOfField)) {
                return "The $fieldName field contains itself"
            }

            if (plugin.ref.isNullOrEmpty()) {
                return "Cant find any variable on $fieldName"
            }

            for (variable in variablesOfField) {
                if (variable.name.trim().isEmpty()) {
                    return "Exits empty variable on $fieldName"
                }

                variable.value = plugin.findRefVariable(variable.name)
                    ?: return "Variable $fieldName not exits in ref"

                if (fieldName == Page.FIELD_NAME_REF_URL
                    || fieldName == Page.FIELD_NAME_URL
                    || fieldName == Page.FIELD_NAME_JS
                ) {
                    if (variable.value !is String) {
                        return "Only support string variable for field $fieldName"
                    }

                    val variableNameAndValue = hashMapOf<String, String>()
                    variableNameAndValue[variable.name] = variable.value!!.toString()
                    page.setFieldValue(
                        fieldName,
                        Grammar.applyVariableValue(fieldValue, variableNameAndValue)
                    )
                } else if (fieldName == Page.FIELD_NAME_DSL) {
                    if (variable.value !is DSL) {
                        return "Only support DSL variable for field $fieldName"
                    }
                    page.dsl = variable.value!! as Map<String, Any>
                } else {
                    return "Variable in $fieldName is not supported"
                }
            }
        }

        return ""
    }

    data class CompileResult(
        var code: Int,
        var message: String = ""
    ) {
        companion object {
            const val CODE_FAILED = -1
            const val CODE_SUCCESS = 0
        }
    }
}