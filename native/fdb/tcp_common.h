//
// Created by jin on 2022/4/12.
//

#ifndef FDB_TCP_COMMON_H
#define FDB_TCP_COMMON_H

#define TCP_BUFFER_SIZE 1024

typedef struct InitConfig {
    char *addr;
    unsigned int port;
    int sendTimeout;
    int receiveTimeout;
} InitConfig;

typedef struct TcpClient {
    unsigned long long connSocket;
} TcpClient;

typedef struct TcpServer {
    unsigned long long listenSocket;
    unsigned long long clientSocket;
} TcpServer;

typedef void *(*OnReceiveListener)(void *clientOrServer, char * /* data or nullptr when error */,
                                   int /* len or error code */);

#endif //FDB_TCP_COMMON_H
