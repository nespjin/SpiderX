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
        return 1;
    }

    if (ret != 0) {
        printf("getaddrinfo failed with error: %d\n", ret);
        WSACleanup();
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

    if (connectSocket == INVALID_SOCKET) {
        printf("Unable to connect to server!\n");
        tcp_client_close();
        return 1;
    }
    return 0;
}

int tcp_client_send(const char *sendbuf) {
    // Send an initial buffer
    int ret = send(connectSocket, sendbuf, (int) strlen(sendbuf), 0);
    if (ret == SOCKET_ERROR) {
        printf("%s: send failed with error: %d\n", TAG, WSAGetLastError());
        tcp_client_close();
        return 1;
    }

    printf("%s: bytes Sent: %ld\n", TAG, ret);

    // Receive until the peer closes the connection
    do {
        char recvbuf[TCP_BUFFER_SIZE];
        int recvbuflen = TCP_BUFFER_SIZE;
        ret = recv(connectSocket, recvbuf, recvbuflen, 0);
        if (ret > 0)
            printf("%s: bytes received: %d\n", TAG, ret);
        else if (ret == 0)
            printf("%s: connection closed\n", TAG);
        else
            printf("%s: recv failed with error: %d\n", TAG, WSAGetLastError());

    } while (ret > 0);

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