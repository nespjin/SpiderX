//
// Created by jin on 2022/4/12.
//

#include <cstdio>
#include "tcp_server.h"
#include "./utils/str_util.h"

static const char *TAG = "tcp_server_main";

void *onReceive(char *data, int len) {
    std::vector<std::string> args;
    stringSplit(std::string(data), ' ', args);
    for (int i = 0; i < args.size(); ++i) {
        printf("index = %d, %s\n", i, args[i].c_str());
    }
    return nullptr;
}

/**
 *
 * @return
 */
int main() {
    int ret;

    ret = tcp_server_init(7000);
    if (ret != 0) {
        printf("%s: tcp_server_init failed\n", TAG);
        return 1;
    }

    printf("%s: fdb server start...\n", TAG);

    ret = tcp_server_run(onReceive);
    if (ret != 0) {
        printf("%s: tcp_server_run failed\n", TAG);
        return 1;
    } else {
        printf("%s: fdb server finish\n", TAG);
    }

    ret = tcp_server_stop();
    if (ret != 0) {
        printf("%s: tcp_server_stop failed\n", TAG);
        return 1;
    }

    return 0;
}