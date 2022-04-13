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

package com.nesp.fishplugin.fdb.fdbd;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 4/11/2022 1:38 AM
 * Description:
 **/
public class Main {

    /**
     * @param args index 0: fdb server port,default:6001
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            new FdbdServer(Integer.parseInt(args[0])).start();
        } else {
            new FdbdServer(6001).start();
        }
    }
}
