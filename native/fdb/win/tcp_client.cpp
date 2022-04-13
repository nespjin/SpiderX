//
// Created by jin on 2022/4/12.
//

#include "../tcp_client.h"
#include "../core/core.h"
#include <winsock2.h>
#include <windows.h>
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
        printError(TAG, "WSAStartup failed with error: " + std::to_string(ret));
        tcp_client_close(client);
        return 1;
    }

    // Create a SOCKET for connecting to server
    client.connSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (client.connSocket == INVALID_SOCKET) {
        printError(TAG, "socket failed with error: " + std::to_string(WSAGetLastError()));
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
        printError(TAG, "connect failed with error: " + std::to_string(WSAGetLastError()));
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
        printInfo(TAG, "%s: bytes received: " + std::to_string(ret));
        memcpy(data, recvBuf, ret);
    } else if (ret == 0) {
        printError(TAG, "connection closed");
    } else {
        *error = WSAGetLastError();
        printError(TAG, "recv failed with error: " + std::to_string(*error));
    }
    return ret;
}

int tcp_client_send(TcpClient &client, const char *sendbuf, OnReceiveListener onReceiveListener) {
    // Send an initial buffer
    int ret = send(client.connSocket, sendbuf, (int) strlen(sendbuf), 0);
    if (ret == SOCKET_ERROR) {
        printError(TAG, "send failed with error: " + std::to_string(WSAGetLastError()));
        tcp_client_close(client);
        return 1;
    }

    printInfo(TAG, "bytes Sent: " + std::to_string(ret));

    // Receive until the peer closes the connection
    int recvLen;
    do {
        char recvBuf[TCP_BUFFER_SIZE];
        int recvBufLen = TCP_BUFFER_SIZE;
        recvLen = recv(client.connSocket, recvBuf, recvBufLen, 0);
        if (recvLen > 0) {
            printInfo(TAG, "bytes received: " + std::to_string(recvLen));
            if (onReceiveListener != nullptr) {
                char *outbuf = new char[recvLen + 1];
                memcpy(outbuf, recvBuf, recvLen);
                outbuf[recvLen] = 0;
                onReceiveListener(&client, outbuf, ret);
                delete[] outbuf;
            }
        } else if (recvLen == 0) {
            printInfo(TAG, "connection closed");
        } else {
            int error = WSAGetLastError();
            if (onReceiveListener != nullptr) {
                onReceiveListener(&client, nullptr, error);
            }
            printError(TAG, "recv failed with error:  " + std::to_string(error));
        }
    } while (recvLen > 0);

    return 0;
}

int tcp_client_send(TcpClient &client, const char *sendbuf, char *dataRec, int *recLen, int *error) {
    if (client.connSocket == INVALID_SOCKET) {
        printError(TAG, "the connect socket is invalid");
        return 1;
    }

    int ret = send(client.connSocket, sendbuf, (int) strlen(sendbuf), 0);
    if (ret == SOCKET_ERROR) {
        printError(TAG, "send failed with error: " + std::to_string(WSAGetLastError()));
        tcp_client_close(client);
        return 1;
    }

    printInfo(TAG, "bytes Sent: " + std::to_string(ret));

    // Receive until the peer closes the connection
    int recvLen;
//    do {
    char recvBuf[TCP_BUFFER_SIZE];
    int recvBufLen = TCP_BUFFER_SIZE;
    recvLen = recv(client.connSocket, recvBuf, recvBufLen, 0);

    *recLen = recvLen;

    if (recvLen > 0) {
        printInfo(TAG, "bytes received: " + std::to_string(recvLen));
        memcpy(dataRec, recvBuf, recvLen);
    } else if (recvLen == 0) {
        printInfo("%s: connection closed\n", TAG);
    } else {
        *error = WSAGetLastError();
        printError(TAG, "recv failed with error: " + std::to_string(*error));
    }
//    } while (recvLen > 0);

    return 0;
}

int tcp_client_close(TcpClient &client) {
    int ret = 0;
    if (client.connSocket != INVALID_SOCKET) {
        // shutdown the connection since no more data will be sent
        ret = shutdown(client.connSocket, SD_SEND);
        if (ret == SOCKET_ERROR) {
            printError(TAG, "shutdown failed with error: " + std::to_string(WSAGetLastError()));
            ret = 1;
        }
        // cleanup
        if (closesocket(client.connSocket) == SOCKET_ERROR) {
            printError(TAG, "close connectSocket failed with error: " + std::to_string(WSAGetLastError()));
            ret = 1;
        }
        client.connSocket = INVALID_SOCKET;
    }
    WSACleanup();
    return ret;
}