//
// Created by jin on 2022/4/12.
//

#include "tcp_server.h"

/**
 *
 * @return
 */
int main() {
    tcp_server_init(7000);
    tcp_server_run();
    tcp_server_stop();
    return 0;
}