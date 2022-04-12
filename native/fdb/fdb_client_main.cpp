//
// Created by jin on 2022/4/12.
//

#include <iostream>
#include "core/core.h"
#include "tcp_client.h"

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
//    if (argc == 1) {
//        printHelp();
//        return 0;
//    }

    tcp_client_init(7000);
    char *data = "hello world";
    tcp_client_send(data);
    tcp_client_close();

    printf("argc = %d, argv = %d", argc, *argv[0]);

    std::cout << "Hello, World!" << std::endl;
    return 0;
}
