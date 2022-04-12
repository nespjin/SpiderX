//
// Created by jin on 2022/4/12.
//

#ifndef FDB_TCP_CLIENT_H
#define FDB_TCP_CLIENT_H

#include "tcp_common.h"

int tcp_client_init(int port);

int tcp_client_send(const char *sendbuf, bool isOnce, OnReceiveListener onReceiveListener);

int tcp_client_close();

#endif //FDB_TCP_CLIENT_H
