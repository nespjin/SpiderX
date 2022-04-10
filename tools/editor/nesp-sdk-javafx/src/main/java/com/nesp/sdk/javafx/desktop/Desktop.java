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

package com.nesp.sdk.javafx.desktop;

import java.lang.reflect.Method;

/**
 * ProjectName: NespManager
 * Author: jinzhaolu
 * Date: 19-2-15
 * Time: 上午2:33
 * FileName: Desktop
 * <p>
 * Description:
 */
public class Desktop {
    public static void browse(String url) throws Exception {
        // 获取操作系统的名字
        String osName = System.getProperty("os.name", "");
        if (osName.startsWith("Mac OS")) {
            // 苹果的打开方式
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL",
                    new Class[]{String.class});
            openURL.invoke(null, new Object[]{url});
        } else if (osName.startsWith("Windows")) {
            // windows的打开方式。
            Runtime.getRuntime().exec(
                    "rundll32 url.dll,FileProtocolHandler " + url);
        } else {
            // Unix or Linux的打开方式
            String[] browsers = {"google-chrome", "browser360", "firefox", "opera", "konqueror", "epiphany",
                    "mozilla", "netscape"};
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
                // 执行代码，在browser有值后跳出，
                // 这里是如果进程创建成功了，==0是表示正常结束。
                if (Runtime.getRuntime()
                        .exec(new String[]{"which", browsers[count]})
                        .waitFor() == 0)
                    browser = browsers[count];
            if (browser == null)
                throw new Exception("Could not find web browser");
            else
                // 这个值在上面已经成功的得到了一个进程。
                Runtime.getRuntime().exec(new String[]{browser, url});
        }
    }

}
