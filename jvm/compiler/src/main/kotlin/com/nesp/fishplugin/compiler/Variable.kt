package com.nesp.fishplugin.compiler

data class Variable(
    var name: String,
    var value: Any?,
    var range: IntRange,
) {

    companion object {

        /**
         * Whether exits variable which named [variableName] in [variables]
         */
        fun exitsVariable(variableName: String, variables: List<Variable>): Boolean {
            return variables.find { it.name == variableName } != null
        }

        fun isPrimitiveType(variableValue: Any): Boolean {
            return variableValue is String
                    || variableValue is Boolean
                    || variableValue is Int
                    || variableValue is Long
                    || variableValue is Byte
                    || variableValue is Short
        }
    }
}
