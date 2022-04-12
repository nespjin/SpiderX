//
// Created by jin on 2022/4/12.
//

#include "../tcp_client.h"
#include <windows.h>
#include <winsock2.h>
#include <cstdio>


// Need to link with Ws2_32.lib, Mswsock.lib, and Advapi32.lib
#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Mswsock.lib")
#pragma comment (lib, "AdvApi32.lib")

static const char *TAG = "tcp_client";

int tcp_client_init(TcpClient &client, InitConfig initConfig) {
    int ret;

    WSADATA wsaData;
    // Initialize Winsock
    ret = WSAStartup(MAKEWORD(2, 2), &wsaData);
    if (ret != 0) {
        printf("%s: WSAStartup failed with error: %d\n", TAG, ret);
        tcp_client_close(client);
        return 1;
    }

    // Create a SOCKET for connecting to server
    client.connSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (client.connSocket == INVALID_SOCKET) {
        printf("socket failed with error: %ld\n", WSAGetLastError());
        tcp_client_close(client);
        return 1;
    }

    // config connect socket
    setsockopt(client.connSocket, SOL_SOCKET, SO_RCVTIMEO, (char *) &initConfig.receiveTimeout,
               sizeof initConfig.receiveTimeout);
    setsockopt(client.connSocket, SOL_SOCKET, SO_SNDTIMEO, (char *) &initConfig.sendTimeout,
               sizeof initConfig.sendTimeout);

    SOCKADDR_IN service;
    memset(&service, 0, sizeof(service));

    service.sin_family = PF_INET;
//    service.sin_addr.s_addr = htonl(INADDR_ANY);
    service.sin_addr.s_addr = inet_addr(initConfig.addr);
    service.sin_port = htons(initConfig.port);

    // Connect to server.
    ret = connect(client.connSocket, (SOCKADDR *) &service, sizeof(service));
    if (ret == SOCKET_ERROR) {
        printf("%s: connect failed with error: %d\n", TAG, WSAGetLastError());
        tcp_client_close(client);
        return 1;
    }
    return 0;
}

/**
 *
 * @param onReceiveListener
 * @return len of data received
 */
int doSend(TcpClient &client, char *data, int *error) {
    int ret;
    char recvBuf[TCP_BUFFER_SIZE];
    int recvBufLen = TCP_BUFFER_SIZE;
    ret = recv(client.connSocket, recvBuf, recvBufLen, 0);
    if (ret > 0) {
        printf("%s: bytes received: %d\n", TAG, ret);
        memcpy(data, recvBuf, ret);
    } else if (ret == 0) {
        printf("%s: connection closed\n", TAG);
    } else {
        *error = WSAGetLastError();
        printf("%s: recv failed with error: %d\n", TAG, error);
    }
    return ret;
}

int tcp_client_send(TcpClient &client, const char *sendbuf, bool isOnce, OnReceiveListener onReceiveListener) {
    char recvBuf[TCP_BUFFER_SIZE];
    int recvLen;
    int error = -1;
    int ret = tcp_client_send(client, sendbuf, isOnce, recvBuf, &recvLen, &error);
    if (ret) {
        if (onReceiveListener != nullptr) {
            onReceiveListener(&client, nullptr, error);
        }
        return 1;
    } else {
        if (onReceiveListener != nullptr) {
            char *outbuf = new char[recvLen + 1];
            memcpy(outbuf, recvBuf, recvLen);
            outbuf[recvLen] = 0;
            onReceiveListener(&client, outbuf, ret);
            delete[] outbuf;
        }
    }
    return 0;
}

int tcp_client_send(TcpClient &client, const char *sendbuf, bool isOnce, char *dataRec, int *recLen, int *error) {
    // Send an initial buffer
    int ret = send(client.connSocket, sendbuf, (int) strlen(sendbuf), 0);
    if (ret == SOCKET_ERROR) {
        printf("%s: send failed with error: %d\n", TAG, WSAGetLastError());
        tcp_client_close(client);
        return 1;
    }

    printf("%s: bytes Sent: %d\n", TAG, ret);

    if (isOnce) {
        *recLen = doSend(client, dataRec, error);
    } else {
        // Receive until the peer closes the connection
        int recvLen;
        do {
            recvLen = doSend(client, dataRec, error);
            *recLen = recvLen;
        } while (recvLen > 0);
    }
    return 0;
}

int tcp_client_close(TcpClient &client) {
    int ret = 0;
    if (client.connSocket != INVALID_SOCKET) {
        // shutdown the connection since no more data will be sent
        ret = shutdown(client.connSocket, SD_SEND);
        if (ret == SOCKET_ERROR) {
            printf("%s: shutdown failed with error: %d\n", TAG, WSAGetLastError());
            ret = 1;
        }
        // cleanup
        if (closesocket(client.connSocket) == SOCKET_ERROR) {
            printf("%s: close connectSocket failed with error: %d\n", TAG, WSAGetLastError());
            ret = 1;
        }
        client.connSocket = INVALID_SOCKET;
    }
    WSACleanup();
    return ret;
}