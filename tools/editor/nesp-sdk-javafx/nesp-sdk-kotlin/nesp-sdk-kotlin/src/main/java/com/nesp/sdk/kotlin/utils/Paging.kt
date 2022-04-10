/*
 * Copyright (c) 2021-2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.kotlin.utils

import kotlin.math.ceil

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/10 下午2:23
 * Description:
 *
 * 分页组件
 * @param T 数据类型
 **/
class Paging<T>(
    /**
     * 每页的数量
     */
    val pageDataSize: Int,
) {

    var onPageChangedListener: OnLoadPageListener? = null

    private val data: ArrayList<T> = arrayListOf()

    private var needNotify = true

    /**
     * 清空所有数据
     */
    fun clear() {
        data.clear()
    }

    fun addAll(elements: Collection<T>) {
        data.addAll(elements)
    }

    fun add(element: T) {
        data.add(element)
    }

    fun add(index: Int, element: T) {
        data.add(index, element)
    }

    fun dataItemAt(index: Int) = data[index]

    val isEmpty: Boolean
        get() {
            return data.isEmpty()
        }


    val isNotEmpty: Boolean
        get() {
            return data.isNotEmpty()
        }

    /**
     * 总数量
     */
    var totalCount = 0
        set(value) {
            field = value
            totalPageCount = totalPageCount2(value, pageDataSize)
            if (value > 0 && currentPageNumber <= 0) {
                needNotify = false
                firstPage()
            }
        }

    /**
     * 当前页码, 从0开始
     */
    var currentPageNumber = 0
        set(value) {
            field = value
            notifyOnPageChanged()
        }

    private fun notifyOnPageChanged() {
        if (!needNotify) {
            needNotify = true
            return
        }
        onPageChangedListener?.loadPage(currentPageNumber, pageDataSize)
    }

    /**
     * 当前页数据的数量 <= [pageDataSize]
     */
    val currentPageDataSize: Int
        get() = data.size

    /**
     * 总页数
     */
    var totalPageCount = 0
        private set(value) {
            field = value
            if (isLastPage) {
                needNotify = false
                lastPage()
            }
        }

    /**
     * 是否有下一页
     */
    val hasNextPage: Boolean
        get() {
            return currentPageNumber < totalPageCount - 1
        }

    /**
     * 是否是最后一页
     */
    val isLastPage: Boolean
        get() {
            return currentPageNumber >= totalPageCount - 1
        }

    /**
     * 导航到最后一页
     */
    fun lastPage() {
        currentPageNumber = totalPageCount - 1
    }

    /**
     * 是否有上一页
     */
    val hasPreviousPage: Boolean
        get() {
            return currentPageNumber > 0
        }

    /**
     * 导航到上一页, 如果不存在上一页则不进行操作
     */
    fun previous() {
        if (hasPreviousPage) {
            currentPageNumber--
        }
    }

    /**
     * 导航到下一页, 如果不存在下一页则不进行操作
     */
    fun next() {
        if (hasNextPage) {
            currentPageNumber++
        }
    }

    /**
     * 是否是第一页
     */
    val isFirstPage: Boolean
        get() {
            return currentPageNumber <= 0
        }

    /**
     * 导航到第一页
     */
    fun firstPage() {
        currentPageNumber = 0
    }

    interface OnLoadPageListener {

        fun loadPage(currentPageNumber: Int, pageCount: Int)

    }

    companion object {

        /**
         * 获取总页数
         *
         * @param totalCount 总数量
         * @param pageDataSize 每页的最大数量
         */
        @JvmStatic
        fun totalPageCount(totalCount: Int, pageDataSize: Int): Int {
            return if (totalCount % pageDataSize == 0) totalCount / pageDataSize
            else totalCount / pageDataSize + 1
        }

        /**
         * 获取总页数
         *
         * @param totalCount 总数量
         * @param pageDataSize 每页的最大数量
         */
        @JvmStatic
        fun totalPageCount2(totalCount: Int, pageDataSize: Int): Int {
            return (totalCount + pageDataSize - 1) / pageDataSize
        }

        /**
         * 获取总页数
         *
         * @param totalCount 总数量
         * @param pageDataSize 每页的最大数量
         */
        @JvmStatic
        fun totalPageCount3(totalCount: Int, pageDataSize: Int): Int {
            return ceil(totalCount.toDouble() / pageDataSize.toDouble()).toInt()
        }

    }
}