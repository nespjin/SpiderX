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

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 4/11/2022 1:42 AM
 * Description:
 **/
public class FdbServerThread extends Thread {

    private static final String TAG = "FdbServerThread";

    private final int port;
    private final ExecutorService executor = Executors.newFixedThreadPool(100);

    public FdbServerThread(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        super.run();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(TAG + " :FdbServerThread start fail: serverSocket create fail!");
            e.printStackTrace();
            return;
        }

        Socket socket;

        while (!isInterrupted()) {
            try {
                socket = serverSocket.accept();
                executor.submit(new ClientSocketThread(socket));
                System.out.println(TAG + " :FdbServerThread start success: clientSocketThread addr = " + socket.getInetAddress().getHostAddress() + " start success!");
            } catch (IOException e) {
                System.out.println(TAG + " :FdbServerThread receive data fail!");
                e.printStackTrace();
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(TAG + " :FdbServerThread close serverSocket fail");
            e.printStackTrace();
        }
    }


}
