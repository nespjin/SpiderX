package com.nesp.fishplugin.runtime.js

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:47
 * Description:
 **/
interface IEventJsRuntimeInterface {

    fun sendPage2Platform(page: String)

    fun sendError2Platform(errorMsg: String)

    fun printHtml(html: String)

}