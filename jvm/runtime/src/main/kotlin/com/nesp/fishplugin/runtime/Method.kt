package com.nesp.fishplugin.runtime

class Method<R>(
    var name: String,
    val parameters: Array<Parameter>,
    var r: R,
)