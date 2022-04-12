//
// Created by jin on 2022/4/12.
//

#include <iostream>
#include "core/core.h"
#include "tcp_client.h"

static void *onReceive(char *data, int len) {
    printf(data);
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

    int ret;
    ret = tcp_client_init(7000);
    if (ret) {
        // the fdb server may not be started
        // try start the fdb server
        printf("start fdb server\n");

        // init tcp client again when the fdb server is started
        ret = tcp_client_init(7000);
    }

    if (ret != 0) {
        std::cout << "tcp client init failed" << std::endl;
        return -1;
    }

    auto *sendData = new std::string();
    for (int i = 1; i < argc; ++i) {
        if (i > 0)*sendData += " ";
        *sendData += argv[i];
    }

    ret = tcp_client_send((*sendData).c_str(), true, onReceive);

    delete sendData;

    if (ret != 0) {
        std::cout << "tcp client send failed" << std::endl;
        return -1;
    }

    ret = tcp_client_close();
    if (ret != 0) {
        std::cout << "tcp client close failed" << std::endl;
        return -1;
    }
    return 0;
}
