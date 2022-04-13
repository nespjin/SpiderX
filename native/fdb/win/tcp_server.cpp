//
// Created by jin on 2022/4/12.
//
#include <cstdio>
#include <winsock2.h>
#include <pthread.h>
#include "../tcp_server.h"
#include "../core/core.h"

//#pragma comment (lib, "ws2_32.lib")  //load ws2_32.dll // using cmake

static const char *TAG = "tcp_server";

bool isStop = false;

int tcp_server_init(TcpServer &server, InitConfig initConfig) {
    isStop = false;
    int ret;

    WSADATA wsaData;
    ret = WSAStartup(MAKEWORD(2, 2), &wsaData);
    if (ret != NO_ERROR) {
        printError(TAG, "WSAStartup failed with error: " + std::to_string(ret));
        return 1;
    }

    server.listenSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (server.listenSocket == INVALID_SOCKET) {
        printError(TAG, "error at listenSocket: " + std::to_string(WSAGetLastError()));
        tcp_server_stop(server);
        return 1;
    }

    // config connect socket
    setsockopt(server.listenSocket, SOL_SOCKET, SO_RCVTIMEO,
               (char *) &initConfig.receiveTimeout, sizeof initConfig.receiveTimeout);
    setsockopt(server.listenSocket, SOL_SOCKET, SO_SNDTIMEO, (char *) &initConfig.sendTimeout,
               sizeof initConfig.sendTimeout);

    SOCKADDR_IN service;
    memset(&service, 0, sizeof(service));

    service.sin_family = PF_INET;
//    service.sin_addr.s_addr = htonl(INADDR_ANY);
    service.sin_addr.s_addr = inet_addr(initConfig.addr);
    service.sin_port = htons(initConfig.port);

    printInfo(TAG, "server init at addr=" + std::string(inet_ntoa(service.sin_addr))
                   + ", port=" + std::to_string(initConfig.port));

    ret = bind(server.listenSocket, (SOCKADDR *) &service, sizeof(service));
    if (ret == SOCKET_ERROR) {
        printError(TAG, "bind function failed with error " + std::to_string(WSAGetLastError()));
        tcp_server_stop(server);
        return 1;
    }
    return 0;
}

void *tcp_server_handleClientThreadRunnable(void *arg) {
    auto args = *((HandleClientThreadArgs *) arg);

    printInfo(TAG, "handleClientThreadRunnable start tid = " + std::to_string(pthread_self()));
    if (args.server.clientSocket == INVALID_SOCKET) {
        printError(TAG, "accept failed with error: " + std::to_string(WSAGetLastError()));
        tcp_server_stop(args.server);
        return (void *) 1;
    }

    printInfo(TAG, "accepted connection from " + std::string(args.addr));

    int recvBufLen = TCP_BUFFER_SIZE;
    char recvBuf[TCP_BUFFER_SIZE];
    int recvLen;
    while (true) {
        recvLen = recv(args.server.clientSocket, recvBuf, recvBufLen, 0);
        if (recvLen == 0) {
            printError(TAG, "connection closed");
            break;
        } else if (recvLen == SOCKET_ERROR) {
            printError(TAG, "recv failed with error: " + std::to_string(WSAGetLastError()));
            break;
        } else {
            char *outbuf = new char[recvLen + 1];
            memcpy(outbuf, recvBuf, recvLen);
            outbuf[recvLen] = 0;
            args.onReceiveListener(&args.server, outbuf, recvLen + 1);
            delete[] outbuf;
        }
    }

    return nullptr;
}

int tcp_server_run(TcpServer &server, OnReceiveListener onReceiveListener) {
    if (server.listenSocket == 0) {
        printError(TAG, "listen socket is null");
        return 1;
    }

    if (listen(server.listenSocket, SOMAXCONN) == SOCKET_ERROR) {
        printError(TAG, "listen listenSocket failed with error: " + std::to_string(WSAGetLastError()));
    }

    printInfo(TAG, "listening on socket...");

    SOCKADDR_IN clientAddr;
    int addrLen = sizeof(clientAddr);

    auto *handleClientThreadArgs = new HandleClientThreadArgs{
            .addr = inet_ntoa(clientAddr.sin_addr),
            .server = server,
            .onReceiveListener = onReceiveListener,
    };

    while (!isStop) {
        printInfo(TAG, "waiting to connect...");
        server.clientSocket = accept(server.listenSocket, (SOCKADDR *) &clientAddr, &addrLen);
        pthread_t tid;
        printInfo(TAG, "main thread id: " + std::to_string(pthread_self()));
        int ret1 = pthread_create(&tid, nullptr, tcp_server_handleClientThreadRunnable, handleClientThreadArgs);
        if (ret1) {
            printError(TAG, "pthread_create failed with error: " + std::to_string(ret1));
        }
    }

    delete handleClientThreadArgs;

    return 0;
}

int tcp_server_send(TcpServer &server, const char *data, int len) {
    send(server.clientSocket, data, len, 0);
    return 0;
}

int tcp_server_stop(TcpServer &server) {
    isStop = true;
    int ret = 0;
    if (server.listenSocket != INVALID_SOCKET) {
        if (closesocket(server.listenSocket) == SOCKET_ERROR) {
            printError(TAG, "close listenSocket failed with error: " + std::to_string(WSAGetLastError()));
            ret = 1;
        }
    }

    if (server.clientSocket != INVALID_SOCKET) {
        if (closesocket(server.clientSocket) == SOCKET_ERROR) {
            printError(TAG, "close clientSocket failed with error: " + std::to_string(WSAGetLastError()));
            ret = 1;
        }
    }

    WSACleanup();
    return ret;
}
