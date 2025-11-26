/*
 * Copyright (c) 2022.  NESP Technology.
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
package com.nesp.spiderx.runtime.android;

import android.os.Handler
import android.os.Looper
import com.nesp.spiderx.runtime.EmptyJniWebView

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class AndroidEmptyWebView : EmptyJniWebView() {
    private val mainHandler = Handler(Looper.getMainLooper(), null)

    override fun dispatchMainThread(task: Runnable) {
        mainHandler.post(task)
    }

    override fun isMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

}
