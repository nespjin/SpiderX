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

package com.nesp.fishplugin.fdb.server;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 4/11/2022 1:39 AM
 * Description:
 **/
public class FdbServer {

    private final FdbServerThread fdbServerThread;

    public FdbServer(int port) {
        fdbServerThread = new FdbServerThread(port);
    }

    public void start() {
        fdbServerThread.start();
        System.out.println("FdbServer start success on port: " + fdbServerThread.getPort());
    }

    public void stop() {
        fdbServerThread.interrupt();
    }

}

