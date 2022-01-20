package com.nesp.fishplugin.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:31
 * Description:
 **/
interface IRunnableRuntimeTask : Runnable, IRuntimeTask {

    override fun interrupt() {
        Thread.currentThread().interrupt()
    }
}