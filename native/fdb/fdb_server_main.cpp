//
// Created by jin on 2022/4/12.
//
#include "./core/core.h"
#include <cstring>
#include "tcp_server.h"
#include "./utils/str_util.h"
#include "core/core.h"
#include "tcp_client.h"

static const char *TAG = "tcp_server_main";

/**
 * The application which run fdbd.
 */
typedef struct TargetApp {
    char *addr;
    TcpClient &client;
} TargetApp;

typedef std::vector<TargetApp> ConnectedAppList;
ConnectedAppList connectedAppList;

void fdb_server_clearConnectLostApps() {
    for (ConnectedAppList::iterator iterator = connectedAppList.begin(); iterator != connectedAppList.end();) {
        if ((*iterator).client.connSocket == ~0) {
//            iterator = connectedAppList.erase(iterator);
        } else {
            iterator++;
        }
    }
    connectedAppList.shrink_to_fit();
}

int fdb_server_connectToApp(TcpServer &server, const char *ip, int port, TcpClient *client) {
    InitConfig initConfig = {
            (char *) ip,
            (unsigned int) port,
            0,
            0
    };

    int ret;

    ret = tcp_client_init(*client, initConfig);
    if (ret) {
        printErrorDebug(TAG, "fdb_server_connectToApp failed");
        return 1;
    }

    /* char sendData[] = {0x01};

     char revData[TCP_BUFFER_SIZE];
     int revLen;
     int error;

     ret = tcp_client_send(client, sendData, (int) 1, revData, &revLen, &error);
     if (ret) {
         printf("fdb_server_connectToApp failed\n");
         return 1;
     }*/

    std::string addr(ip);
    addr.append(std::to_string(port));

    TargetApp targetApp = {
            .addr = (char *) addr.c_str(),
            .client = *client
    };

    connectedAppList.push_back(targetApp);
    printInfoDebug(TAG, "fdb_server_connectToApp success");

    return 0;
}

void *onReceiveFromClient(void *serverOrClient, char *data, int len) {
    fdb_server_clearConnectLostApps();

    TcpServer server = *((TcpServer *) serverOrClient);

    std::vector<std::string> args;
    stringSplit(std::string(data), ' ', args);
    char *commandArgs[args.size()];
    for (int i = 0; i < args.size(); ++i) {
        commandArgs[i] = (char *) args[i].c_str();
    }

#if DEBUG
    for (int i = 0; i < args.size(); ++i) {
        printInfoDebug(TAG, "index = " + std::to_string(i) + ", " + commandArgs[i]);
    }
#endif

    std::string response("0");

    int commandArgsLen = (int) args.size();

    // connect 192.168.1:200
    // parse command line
    // TODO:replace with getopt_long();
    bool isDisconnect = true;
    if (commandArgsLen < 1) {
        printError(TAG, "command parse fail");
        return reinterpret_cast<void *>(1);
    }

    if (strcmp(commandArgs[0], COMMAND_CONNECT) == 0) {
        // connect to app
        if (args.size() < 2) {
            response = "please input ip and port, sample as: 192.168.0.3:6666";
        } else {
            const char *addr = commandArgs[1];
            std::string addrString(addr);
            std::vector<std::string> ipAndPort;
            stringSplit(std::string(addr), ':', ipAndPort);
            int port = strtol(ipAndPort[1].c_str(), nullptr, 10);
            TcpClient tcpClient{
                    (unsigned long long) (~0)
            };
            int ret1 = fdb_server_connectToApp(server, ipAndPort[0].c_str(), port, &tcpClient);
            if (ret1) {
                response = "connect to app " + addrString + " failed";
            } else {
                response = "connect to app " + addrString + " success";
                tcp_server_send(server, (char *) response.c_str(), (int) response.length());

                printInfoDebug(TAG, "the socket is " + std::to_string(tcpClient.connSocket));

                char *data = (char *) "i am from fdb server\n";
                char data2[TCP_BUFFER_SIZE];
                int len2;
                int err;
                tcp_client_send(tcpClient, data, data2, &len2, &err);

                if (len2 > 0) {
                    response = "receive from fdbd " + std::string(data2) + " success\n";
                } else {
                    response = "receive from fdbd failed\n";
                }
            }
        }
    } else if (strcmp(commandArgs[0], COMMAND_DISCONNECT) == 0) {
        // connect to app
        if (args.size() < 2) {
            response = "please input ip and port, sample as: 192.168.0.3:6666";
        } else {
            const char *addr = commandArgs[1];
            size_t c = connectedAppList.size();
            size_t targetAppIndex = -1;
            for (int i = 0; i < c; ++i) {
                if (strcmp(connectedAppList[c].addr, addr) == 0) {
                    targetAppIndex = c;
                    break;
                }
            }

            if (targetAppIndex >= 0) {
                if (tcp_client_close(connectedAppList[targetAppIndex].client)) {
                    response = "disconnect failed";
                } else {
                    response = "disconnect success";
                }
            } else {
                response = "the target app is not connected";
            }

        }
    } else if (strcmp(commandArgs[0], COMMAND_APPS) == 0) {
        // list apps
        std::string deviceList("\n");
        size_t c = connectedAppList.size();
        for (int i = 0; i < c; ++i) {
            deviceList.append(connectedAppList[c].addr);
            deviceList.append("\n");
        }
        response = "List the apps connected" + deviceList;
    } else {
        // send data to fdbd
        if (connectedAppList.empty()) {
            response = "Please connect to app at first";
        } else if (connectedAppList.size() == 1) {

            char revData[TCP_BUFFER_SIZE];
            int revLen;
            int error;

            int ret = tcp_client_send(connectedAppList[0].client, data, revData, &revLen, &error);
            if (ret) {
                response = error ? std::to_string(error) : "Exec failed";
            } else {
                char *outbuf = new char[revLen + 1];
                memcpy(outbuf, revData, revLen);
                outbuf[revLen] = 0;
                response.append(outbuf);
                delete[] outbuf;
            }
        } else {
            response = "Cant supported multiple apps";
        }
    }

    // send response
    tcp_server_send(server, response.c_str(), (int) response.size());

    /* if (isDisconnect) {
         tcp_server_stop(server);
     }*/

    return nullptr;
}

/**
 *
 * @return
 */
int main() {
    int ret;

    TcpServer server{
            (unsigned long long) (~0),
            (unsigned long long) (~0)
    };

    InitConfig initConfig = {
            (char *) "127.0.0.1",
            FDB_SERVER_PORT,
            0,
            0
    };

    ret = tcp_server_init(server, initConfig);
    if (ret != 0) {
        printError(TAG, "tcp_server_init failed");
        return 1;
    }

    printInfo(TAG, "fdb server start...");

    ret = tcp_server_run(server, onReceiveFromClient);
    if (ret != 0) {
        printError(TAG, "tcp_server_run failed");
        return 1;
    } else {
        printInfo(TAG, "fdb server finish");
    }

    ret = tcp_server_stop(server);
    if (ret != 0) {
        printError(TAG, "tcp_server_stop failed");
        return 1;
    }

    return 0;
}