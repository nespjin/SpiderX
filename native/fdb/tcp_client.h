//
// Created by jin on 2022/4/12.
//

#ifndef FDB_TCP_CLIENT_H
#define FDB_TCP_CLIENT_H

#include "tcp_common.h"

int tcp_client_init(TcpClient &client, InitConfig initConfig);

int tcp_client_send(TcpClient &client, const char *sendbuf, bool isOnce, char *dataRec, int *recLen, int *error);

int tcp_client_send(TcpClient &client, const char *sendbuf, bool isOnce, OnReceiveListener onReceiveListener);

int tcp_client_close(TcpClient &client);

#endif //FDB_TCP_CLIENT_H
