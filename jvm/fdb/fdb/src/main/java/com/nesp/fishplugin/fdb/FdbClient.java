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

package com.nesp.fishplugin.fdb;

import java.io.*;
import java.net.Socket;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 4/11/2022 1:02 AM
 * Description:
 **/
public final class FdbClient {

    private static final String TAG = "FdbClient";

    private final Socket socket;

    public FdbClient() throws IOException {
        socket = new Socket("localhost", 6000);
    }

    private void send(String data) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write(data);
        printWriter.flush();

        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        socket.shutdownInput();

        bufferedReader.close();
        inputStream.close();
        outputStream.close();
        printWriter.close();
        socket.close();
    }

    public static void checkAndStartFdbServer() {

    }

    public static void install(String path) {

    }

    public static void uninstall(String pluginId) {

    }


}
