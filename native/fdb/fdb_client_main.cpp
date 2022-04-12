//
// Created by jin on 2022/4/12.
//

#include <iostream>
#include "core/core.h"
#include "tcp_client.h"
#include <unistd.h>
#include "./cmake-build-debug/config.h"

#if IS_WIN

#include <winsock2.h>

#endif

static void *onReceive(void *serverOrClient, char *data, int len) {
    if (data == nullptr) {
        printf("error occurs with %d\n", len);
    } else {
        printf(data);
    }
    return nullptr;
}

/**
 * The entry point of the fdb client.
 * Usage:
 *  fdb pm install <plugin file path>
 *  fdb pm uninstall <plugin id>
 *  fdb
 *
 * @param argc the count for arguments
 * @param argv the arguments
 * @return
 */
int main(int argc, char **argv) {
    if (argc <= 1) {
        printHelp();
        return 0;
    }

    TcpClient tcpClient{
            (unsigned long long) (~0)
    };

    InitConfig initConfig = {
            (char *) "127.0.0.1",
            FDB_SERVER_PORT,
            3000,
            3000 * 10
    };

    int ret;
    ret = tcp_client_init(tcpClient, initConfig);
    if (ret) {
        // the fdb server may not be started
        // try start the fdb server
        printf("start fdb server\n");

        // init tcp client again when the fdb server is started
        ret = tcp_client_init(tcpClient, initConfig);
    }

    if (ret != 0) {
        std::cout << "tcp client init failed" << std::endl;
        return 1;
    }

    auto *sendData = new std::string();
    for (int i = 1; i < argc; ++i) {
        if (i > 0)*sendData += " ";
        *sendData += argv[i];
    }

    ret = tcp_client_send(tcpClient, (*sendData).c_str(), true, onReceive);

    delete sendData;

    if (ret != 0) {
        std::cout << "tcp client send failed" << std::endl;
        return 1;
    }

    ret = tcp_client_close(tcpClient);
    if (ret != 0) {
        std::cout << "tcp client close failed" << std::endl;
        return 1;
    }
    return 0;
}
