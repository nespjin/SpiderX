//
// Created by jin on 2022/4/12.
//

#include "core/core.h"
#include "tcp_client.h"

static void *onReceive(void *serverOrClient, char *data, int len) {
    if (data == nullptr) {
        printError("", "error occurs with " + std::to_string(len));
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
            (unsigned int) FDB_SERVER_PORT,
            0,
            0,
    };

    int ret;
    ret = tcp_client_init(tcpClient, initConfig);
    if (ret) {
        // the fdb server may not be started
        // try start the fdb server
        printInfo("", "start fdb server");

        // init tcp client again when the fdb server is started
        ret = tcp_client_init(tcpClient, initConfig);
    }

    if (ret != 0) {
        printError("", "tcp client init failed");
        return 1;
    }

    auto *sendData = new std::string();
    for (int i = 1; i < argc; ++i) {
        if (i > 0)*sendData += " ";
        *sendData += argv[i];
    }

    ret = tcp_client_send(tcpClient, (*sendData).c_str(), onReceive);

    delete sendData;

    if (ret != 0) {
        printError("", "tcp client send failed");
        return 1;
    }

    ret = tcp_client_close(tcpClient);
    if (ret != 0) {
        printError("", "tcp client close failed");
        return 1;
    }
    return 0;
}
