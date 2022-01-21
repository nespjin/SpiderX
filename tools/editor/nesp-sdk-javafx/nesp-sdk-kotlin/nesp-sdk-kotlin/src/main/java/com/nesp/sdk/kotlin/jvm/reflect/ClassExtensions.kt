/*
 * Copyright (C) 2021 The NESP Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.kotlin.jvm.reflect

import java.lang.reflect.Field

/**
 *
 *
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
* Time: Created 2021/4/21 1:03
 * Project: NespAndroidSdkSample
 **/

fun Class<*>.getFieldValue(instance: Any, fieldName: String): Any? {
    try {
        val field: Field = this.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(instance)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
    return null
}

fun Class<*>.setFieldValue(instance: Any, fieldName: String, value: Any) {
    try {
        val field: Field = this.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(instance, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}