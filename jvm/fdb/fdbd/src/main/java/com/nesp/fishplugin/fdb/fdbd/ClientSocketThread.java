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

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 4/11/2022 1:59 AM
 * Description:
 **/
public class ClientSocketThread extends Thread {

    private static final String TAG = "ClientSocketThread";

    private final Socket clientSocket;

    public ClientSocketThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = clientSocket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String rev = bufferedReader.readLine();
            System.out.println(TAG + ":Server rev:" + rev);

            System.out.println("start reply");
            outputStream = clientSocket.getOutputStream();
            outputStream.write("Hello, I am fdbd.\n".getBytes(StandardCharsets.UTF_8));
            System.out.println(TAG + ":Server replay");

        } catch (Exception e) {
            System.out.println(TAG + ": Server error.");
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
