//
// Created by jin on 2022/4/12.
//
#define WIN32_LEAN_AND_MEAN

#include "../tcp_client.h"
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <stdlib.h>
#include <stdio.h>


// Need to link with Ws2_32.lib, Mswsock.lib, and Advapi32.lib
#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Mswsock.lib")
#pragma comment (lib, "AdvApi32.lib")

static const char *TAG = "tcp_client";

static SOCKET connectSocket = INVALID_SOCKET;

int tcp_client_init(int port) {
    int ret;

    WSADATA wsaData;
    // Initialize Winsock
    ret = WSAStartup(MAKEWORD(2, 2), &wsaData);
    if (ret != 0) {
        printf("%s: WSAStartup failed with error: %d\n", TAG, ret);
        tcp_client_close();
        return 1;
    }

    // Create a SOCKET for connecting to server
    connectSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (connectSocket == INVALID_SOCKET) {
        printf("socket failed with error: %ld\n", WSAGetLastError());
        tcp_client_close();
        return 1;
    }

    SOCKADDR_IN service;
    memset(&service, 0, sizeof(service));

    service.sin_family = PF_INET;
//    service.sin_addr.s_addr = htonl(INADDR_ANY);
    service.sin_addr.s_addr = inet_addr("127.0.0.1");
    service.sin_port = htons(port);

    // Connect to server.
    ret = connect(connectSocket, (SOCKADDR *) &service, sizeof(service));
    if (ret == SOCKET_ERROR) {
        printf("%s: connect failed with error: %d\n", TAG, WSAGetLastError());
        tcp_client_close();
        return 1;
    }
    return 0;
}

/**
 *
 * @param onReceiveListener
 * @return len of data received
 */
int doSend(OnReceiveListener onReceiveListener) {
    int ret;
    char recvBuf[TCP_BUFFER_SIZE];
    int recvBufLen = TCP_BUFFER_SIZE;
    ret = recv(connectSocket, recvBuf, recvBufLen, 0);
    if (ret > 0) {
        printf("%s: bytes received: %d\n", TAG, ret);
        if (onReceiveListener != nullptr) {
            char *outbuf = new char[ret + 1];
            memcpy(outbuf, recvBuf, ret);
            outbuf[ret] = 0;
            onReceiveListener(outbuf, ret);
            delete[] outbuf;
        }
    } else if (ret == 0) {
        printf("%s: connection closed\n", TAG);
    } else {
        printf("%s: recv failed with error: %d\n", TAG, WSAGetLastError());
    }
    return ret;
}

int tcp_client_send(const char *sendbuf, bool isOnce, OnReceiveListener onReceiveListener) {
    // Send an initial buffer
    int ret = send(connectSocket, sendbuf, (int) strlen(sendbuf), 0);
    if (ret == SOCKET_ERROR) {
        printf("%s: send failed with error: %d\n", TAG, WSAGetLastError());
        tcp_client_close();
        return 1;
    }

    printf("%s: bytes Sent: %ld\n", TAG, ret);

    if (isOnce) {
        doSend(onReceiveListener);
    } else {
        // Receive until the peer closes the connection
        int recvLen;
        do {
            recvLen = doSend(onReceiveListener);
        } while (recvLen > 0);
    }

    return 0;
}

int tcp_client_close() {
    int ret = 0;
    if (connectSocket != INVALID_SOCKET) {
        // shutdown the connection since no more data will be sent
        ret = shutdown(connectSocket, SD_SEND);
        if (ret == SOCKET_ERROR) {
            printf("%s: shutdown failed with error: %d\n", TAG, WSAGetLastError());
            ret = 1;
        }
        // cleanup
        if (closesocket(connectSocket) == SOCKET_ERROR) {
            printf("%s: close connectSocket failed with error: %d\n", TAG, WSAGetLastError());
            ret = 1;
        }
        connectSocket = INVALID_SOCKET;
    }
    WSACleanup();
    return ret;
}