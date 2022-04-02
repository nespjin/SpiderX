package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.PluginUtil
import com.nesp.fishplugin.core.Result
import com.nesp.fishplugin.core.data.Page2
import com.nesp.fishplugin.core.data.Plugin2
import com.nesp.fishplugin.tools.code.JsMinifier
import org.apache.logging.log4j.LogManager
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
    fun compile(plugin: Plugin2): CompileResult {
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
    private fun doCompile(plugin: Plugin2, pluginDir: File? = null): CompileResult {

        val pluginDirPath = if (pluginDir == null) "" else pluginDir.absolutePath

        // Check parent
        if (plugin.parent != null && plugin.parent !is Plugin2) {
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
                val deviceTypes = Environment.allDeviceTypes().plus(-1)
                for (deviceTypeItem in deviceTypes) {
                    val deviceType = if (deviceTypeItem < 0) null else deviceTypeItem

                    if (!page.getRefUrl(deviceType).isNullOrEmpty()) {
                        val loadPageFromUrl = Loader.loadPageFromUrl(page.getUrl(deviceType)!!)
                        if (loadPageFromUrl.code != Result.CODE_SUCCESS) {
                            return CompileResult(Result.CODE_FAILED, loadPageFromUrl.message)
                        }

                        if (page.id.isEmpty()) {
                            page.id = loadPageFromUrl.data!!.id
                        }

                        if (page.getUrl(deviceType).isNullOrEmpty()) {
                            page.setUrl(loadPageFromUrl.data!!.getUrl(deviceType) ?: "", deviceType)
                        }

                        if (page.getJs(deviceType).isNullOrEmpty()) {
                            page.setJs(loadPageFromUrl.data!!.getJs(deviceType) ?: "", deviceType)
                        }

                        if (page.getDsl(deviceType) == null
                            || (page.getDsl(deviceType) is Map<*, *> && (page.getDsl(deviceType) as Map<*, *>).isEmpty())
                        ) {
                            page.setDsl(loadPageFromUrl.data!!.getDsl(deviceType) ?: "", deviceType)
                        }
                    }

                    if (!page.getJs(deviceType).isNullOrEmpty()) {
                        if (page.getJs(deviceType)!!.startsWith(Page2.JS_PATH_PREFIX)) {

                            val loadJsFromDisk = Loader.loadJsFromDisk(
                                Path(
                                    pluginDirPath,
                                    page.getJs(deviceType)!!.substring(Page2.JS_PATH_PREFIX.length)
                                ).toString()
                            )
                            if (loadJsFromDisk.code != Result.CODE_SUCCESS) {
                                return CompileResult(Result.CODE_FAILED, loadJsFromDisk.message)
                            }
                            page.setJs(loadJsFromDisk.data!!, deviceType)
                        } else if (page.getJs(deviceType)!!.startsWith(Page2.JS_URL_PREFIX)) {
                            val loadJsFromUrl = Loader.loadJsFromUrl(page.getJs(deviceType)!!
                                .substring(Page2.JS_URL_PREFIX.length))
                            if (loadJsFromUrl.code != Result.CODE_SUCCESS) {
                                return CompileResult(Result.CODE_FAILED, loadJsFromUrl.message)
                            }
                            page.setJs(loadJsFromUrl.data!!, deviceType)
                        }
                    }
                }
            }
        }

        // Remove parent if compile success
        plugin.parent = null

        // Remove ref if compile success
        plugin.ref = null

        // Check Result
        val grammarCheckResult1 = Grammar.checkGrammar(plugin)
        if (grammarCheckResult1.level == Grammar.GrammarCheckResult.LEVEL_ERROR) {
            return CompileResult(Result.CODE_FAILED, grammarCheckResult1.message)
        }
        if (grammarCheckResult1.level == Grammar.GrammarCheckResult.LEVEL_WARNING) {
            println("Warning: Grammar Check: " + grammarCheckResult1.message)
        }

        try {
            compressJsCode(plugin)
        } catch (e: Exception) {
            return CompileResult(
                Result.CODE_FAILED,
                message = "Error when compress js code:\n${e.stackTraceToString()}"
            )
        }
        // TODO: Compress plugin
        compressPlugin()

        return CompileResult(Result.CODE_SUCCESS, data = plugin)
    }

    private fun compressJsCode(plugin: Plugin2) {
        for (page in plugin.pages) {
            val deviceTypes = Environment.allDeviceTypes().plus(-1)
            for (deviceTypeItem in deviceTypes) {
                val deviceType = if (deviceTypeItem < 0) null else deviceTypeItem
                LogManager.getLogger(Compiler::class.java)
                    .info("compressJsCode deviceType = $deviceType, page.js:\n" +
                            page.getJs(deviceType))
                page.setJs(JsMinifier().minify(page.getJs(deviceType)), deviceType)
            }

        }
    }

    private fun compressPlugin() {

    }

    /**
     * Lookup all variable reference and replace it with value.
     * @return error if failed.
     */
    private fun lookupAndApplyVariable(plugin: Plugin2): String {
        if (plugin.parent != null) {
            val lookupAndApplyVariableOfParent = lookupAndApplyVariable(plugin.parent as Plugin2)
            if (lookupAndApplyVariableOfParent.isNotEmpty()) {
                return lookupAndApplyVariableOfParent
            }
        }

        // Name
        var lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_NAME)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // id
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_ID)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Author
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_AUTHOR)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Version
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_VERSION)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Runtime
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_RUNTIME)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Time
        lookupAndApplyVariableResult = lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_TIME)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Introduction
        lookupAndApplyVariableResult =
            lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_INTRODUCTION)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        // Pages
        lookupAndApplyVariableResult =
            lookupAndApplyVariable(plugin, Plugin2.FIELD_NAME_PAGES)
        if (lookupAndApplyVariableResult.isNotEmpty()) {
            return lookupAndApplyVariableResult
        }

        return ""
    }

    private fun lookupAndApplyVariable(plugin: Plugin2, fieldName: String): String {
        val fieldValue = plugin.getFieldValue(fieldName)

        if (fieldValue == null || (fieldValue is String && fieldValue.isEmpty())) {
//            return "Not support for field $fieldName"
            return ""
        }

        if (fieldName == Plugin2.FIELD_NAME_NAME
            || fieldName == Plugin2.FIELD_NAME_ID
            || fieldName == Plugin2.FIELD_NAME_AUTHOR
            || fieldName == Plugin2.FIELD_NAME_VERSION
            || fieldName == Plugin2.FIELD_NAME_RUNTIME
            || fieldName == Plugin2.FIELD_NAME_TIME
            || fieldName == Plugin2.FIELD_NAME_INTRODUCTION
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
        } else if (fieldName == Plugin2.FIELD_NAME_PAGES) {
            for (page in plugin.pages) {
                var result = lookupAndApplyVariable(plugin, page, Page2.FIELD_NAME_REF_URL)
                if (result.isNotEmpty()) return result
                result = lookupAndApplyVariable(plugin, page, Page2.FIELD_NAME_URL)
                if (result.isNotEmpty()) return result
                result = lookupAndApplyVariable(plugin, page, Page2.FIELD_NAME_JS)
                if (result.isNotEmpty()) return result
                result = lookupAndApplyVariable(plugin, page, Page2.FIELD_NAME_DSL)
                if (result.isNotEmpty()) return result
            }
        } else {
            return "Variable in $fieldName is not supported"
        }
        return ""
    }

    private fun lookupAndApplyVariable(plugin: Plugin2, page: Page2, fieldName: String): String {
        if (fieldName == Page2.FIELD_NAME_ID) {
            return "Not support for field $fieldName"
        }

        val fieldsToAdd = hashMapOf<String, Any>()

        val deviceTypes = Environment.allDeviceTypes().plus(-1)

        for (deviceTypeItem in deviceTypes) {
            val deviceType = if (deviceTypeItem < 0) null else deviceTypeItem

            val fieldValue = page.getFieldValue(fieldName, deviceType)

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

                    var fieldNameToAdd = ""
                    var variableValueToAdd: Any? = null
                    val deviceTypes1 = Environment.allDeviceTypes()
                    for (deviceType1 in deviceTypes1) {
                        plugin.findRefVariable(variable.name, deviceType1)?.let {
                            variableValueToAdd = it
                            if ((page.getFieldValue(fieldName, deviceType1) == null
                                        && !fieldsToAdd.containsKey(
                                    PluginUtil.getFieldNameWithDeviceType(fieldName, deviceType1)))
                            ) {
                                fieldNameToAdd =
                                    PluginUtil.getFieldNameWithDeviceType(fieldName, deviceType1)
                            }

                        }
                    }

                    if (fieldNameToAdd.isNotEmpty() && variableValueToAdd != null) {
                        val variableNameAndValue = hashMapOf<String, String>()
                        variableNameAndValue[variable.name] = variableValueToAdd!!.toString()
                        fieldsToAdd[fieldNameToAdd] =
                            Grammar.applyVariableValue(fieldValue, variableNameAndValue)
                    }

                    variable.value = plugin.findRefVariable(variable.name, deviceType)

                    if (variable.value == null) {
                        variable.value = plugin.findRefVariable(variable.name)
                    }

                    if (variable.value == null && variableValueToAdd == null) {
                        return "Variable ${variable.name} not exits in ref"
                    }

                    if (fieldName == Page2.FIELD_NAME_REF_URL
                        || fieldName == Page2.FIELD_NAME_URL
                        || fieldName == Page2.FIELD_NAME_JS
                    ) {
                        if (variable.value !is String && variableValueToAdd !is String) {
                            return "Only support string variable for field $fieldName"
                        }

                        if (variable.value != null) {
                            val variableNameAndValue = hashMapOf<String, String>()
                            variableNameAndValue[variable.name] = variable.value!!.toString()
                            page.setFieldValue(
                                fieldName,
                                Grammar.applyVariableValue(fieldValue, variableNameAndValue)
                            )
                        }

                        if (fieldNameToAdd.isNotEmpty()) {
                            val variableNameAndValue = hashMapOf<String, String>()
                            variableNameAndValue[variable.name] = variableValueToAdd!!.toString()
                            fieldsToAdd[fieldNameToAdd] =
                                Grammar.applyVariableValue(fieldValue, variableNameAndValue)
                        }

                    } else if (fieldName == Page2.FIELD_NAME_DSL) {
                        if (variable.value !is Map<*, *> && variableValueToAdd !is Map<*, *>) {
                            return "Only support Map variable for field $fieldName"
                        }
                        val map = variable.value as Map<*, *>?
                        if (!map.isNullOrEmpty()) page.setDsl(map, deviceType)

                        if (fieldNameToAdd.isNotEmpty()) {
                            val map1 = variableValueToAdd as Map<*, *>?
                            if (!map1.isNullOrEmpty()) fieldsToAdd[fieldNameToAdd] = map1
                        }
                    } else {
                        return "Variable in $fieldName is not supported"
                    }
                }
            }
        }

        return ""
    }

    class CompileResult(
        code: Int,
        message: String = "",
        data: Plugin2? = null,
    ) : Result<Plugin2>(code, message, data)
}