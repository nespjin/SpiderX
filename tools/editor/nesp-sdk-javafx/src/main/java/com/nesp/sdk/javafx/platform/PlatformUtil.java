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

package com.nesp.sdk.javafx.platform;

/**
 * ProjectName: NespManager
 * Author: jinzhaolu
 * Date: 19-2-15
 * Time: 上午2:44
 * FileName: Platform
 * <p>
 * Description:
 */
public class PlatformUtil {

    public static Platform getPlatform() {
        final String osName = System.getProperty("os.name", "");
        if (osName.startsWith("Mac OS")) {
            return Platform.MAC_OSX;
        } else if (osName.startsWith("Windows")) {
            return Platform.WINDOWS;
        } else if (osName.startsWith("Linux")) {
            return Platform.LINUX;
        } else {
            return Platform.UNKNOWN;
        }
    }

}
