package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.Result
import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Plugin
import java.io.File
import kotlin.io.path.Path

object Compiler {

    @JvmStatic
    fun compileFromDisk(path: String): CompileResult {
        // Load plugin
        val loadResult = Loader.loadPluginFromDisk(path)
        if (loadResult.code != Result.CODE_SUCCESS) {
            return CompileResult(Result.CODE_FAILED, loadResult.message)
        }
        return doCompile(loadResult.data!!, File(path).parentFile)
    }

    @JvmStatic
    fun compileFromUrl(url: String): CompileResult {
        // Load plugin
        val loadResult = Loader.loadPluginFromUrl(url)
        if (loadResult.code != Result.CODE_SUCCESS) {
            return CompileResult(Result.CODE_FAILED, loadResult.message)
        }
        return doCompile(loadResult.data!!)
    }

    @JvmStatic
    fun compile(plugin: Plugin): CompileResult {
        // Load plugin
        val loadResult = Loader.load(plugin)
        if (loadResult.code != Result.CODE_SUCCESS) {
            return CompileResult(Result.CODE_FAILED, loadResult.message)
        }
        return doCompile(plugin)
    }

    /**
     * Compile plugin
     */
    private fun doCompile(plugin: Plugin, pluginDir: File? = null): CompileResult {

        val pluginDirPath = if (pluginDir == null) "" else pluginDir.absolutePath

        // Check parent
        if (plugin.parent != null && plugin.parent !is Plugin) {
            return CompileResult(Result.CODE_FAILED, "prent is not supported")
        }

        // Check plugin grammar
        val grammarCheckResult = Grammar.checkGrammar(plugin)
        if (grammarCheckResult.level == Grammar.GrammarCheckResult.LEVEL_ERROR) {
            return CompileResult(Result.CODE_FAILED, grammarCheckResult.message)
        }
        if (grammarCheckResult.level == Grammar.GrammarCheckResult.LEVEL_WARNING) {
            println("Warning: Grammar Check: " + grammarCheckResult.message)
        }

        // Lookup and apply variable
        val lookupAndApplyVariableErrorMsg = lookupAndApplyVariable(plugin)
        if (lookupAndApplyVariableErrorMsg.isNotEmpty()) {
            return CompileResult(Result.CODE_FAILED, lookupAndApplyVariableErrorMsg)
        }

        // Compile pages
        if (plugin.pages.isNotEmpty()) {
            for (page in plugin.pages) {
                if (!page.refUrl.isNullOrEmpty()) {
                    val loadPageFromUrl = Loader.loadPageFromUrl(page.refUrl!!)
                    if (loadPageFromUrl.code != Result.CODE_SUCCESS) {
                        return CompileResult(Result.CODE_FAILED, loadPageFromUrl.message)
                    }

                    if (page.id.isEmpty()) {
                        page.id = loadPageFromUrl.data!!.id
                    }

                    if (page.url.isEmpty()) {
                        page.url = loadPageFromUrl.data!!.url
                    }

                    if (page.js.isEmpty()) {
                        page.js = loadPageFromUrl.data!!.js
                    }

                    if (page.dsl == null || (page.dsl is Map<*, *> && (page.dsl as Map<*, *>).isEmpty())) {
                        page.dsl = loadPageFromUrl.data!!.dsl
                    }
                }

                if (page.js.isNotEmpty()) {
                    if (page.js.startsWith(Page.JS_PATH_PREFIX)) {

                        val loadJsFromDisk = Loader.loadJsFromDisk(
                            Path(
                                pluginDirPath,
                                page.js.substring(Page.JS_PATH_PREFIX.length)
                            ).toString()
                        )
                        if (loadJsFromDisk.code != Result.CODE_SUCCESS) {
                            return CompileResult(Result.CODE_FAILED, loadJsFromDisk.message)
                        }
                        page.js = loadJsFromDisk.data!!
                    } else if (page.js.startsWith(Page.JS_URL_PREFIX)) {
                        val loadJsFromUrl =
                            Loader.loadJsFromUrl(page.js.substring(Page.JS_URL_PREFIX.length))
                        if (loadJsFromUrl.code != Result.CODE_SUCCESS) {
                            return CompileResult(Result.CODE_FAILED, loadJsFromUrl.message)
                        }
                        page.js = loadJsFromUrl.data!!
                    }
                }
            }
        }

        // Remove parent if compile success
        plugin.parent = null

        // Remove ref if compile success
        plugin.ref = null

        // TODO: Compress Js code
        compressJsCode()
        // TODO: Compress plugin
        compressPlugin()

        return CompileResult(Result.CODE_SUCCESS, data = plugin)
    }

    private fun compressJsCode() {

    }

    private fun compressPlugin() {

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

        // id
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin.FILED_NAME_ID)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Author
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin.FILED_NAME_AUTHOR)
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
//            return "Not support for field $fieldName"
            return ""
        }

        if (fieldName == Plugin.FILED_NAME_NAME
            || fieldName == Plugin.FILED_NAME_ID
            || fieldName == Plugin.FILED_NAME_AUTHOR
            || fieldName == Plugin.FILED_NAME_VERSION
            || fieldName == Plugin.FILED_NAME_RUNTIME
            || fieldName == Plugin.FILED_NAME_TIME
            || fieldName == Plugin.FILED_NAME_INTRODUCTION
        ) {
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
                        ?: return "Variable ${variable.name} not exits in ref"

                    if (!Variable.isPrimitiveType(variable.value!!)) {
                        return "Only support primitive type variable for field $fieldName"
                    }

                    val variableNameAndValue = hashMapOf<String, String>()
                    variableNameAndValue[variable.name] = variable.value!!.toString()
                    plugin.setFieldValue(
                        fieldName,
                        Grammar.applyVariableValue(fieldValue, variableNameAndValue)
                    )
                }
            }
        } else if (fieldName == Plugin.FILED_NAME_PAGES) {
            for (page in plugin.pages) {
                var result = lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_REF_URL)
                if (result.isNotEmpty()) return result
                result = lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_URL)
                if (result.isNotEmpty()) return result
                result = lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_JS)
                if (result.isNotEmpty()) return result
                result = lookupAndApplyVariable(plugin, page, Page.FIELD_NAME_DSL)
                if (result.isNotEmpty()) return result
            }
        } else {
            return "Variable in $fieldName is not supported"
        }
        return ""
    }

    private fun lookupAndApplyVariable(plugin: Plugin, page: Page, fieldName: String): String {
        if (fieldName == Page.FIELD_NAME_ID) {
            return "Not support for field $fieldName"
        }

        val fieldValue = page.getFieldValue(fieldName)

        if (fieldValue == null || (fieldValue is String && fieldValue.isEmpty())) {
//            return "Not support for field $fieldName"
            return ""
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
                    ?: return "Variable ${variable.name} not exits in ref"

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
                    if (variable.value !is Map<*, *>) {
                        return "Only support Map variable for field $fieldName"
                    }
                    val map = variable.value as Map<*, *>?
                    if (!map.isNullOrEmpty()) page.dsl = map
                } else {
                    return "Variable in $fieldName is not supported"
                }
            }
        }
        return ""
    }

    class CompileResult(
        code: Int,
        message: String = "",
        data: Plugin? = null
    ) : Result<Plugin>(code, message, data)
}