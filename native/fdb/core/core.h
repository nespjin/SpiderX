//
// Created by jin on 2022/4/12.
//

#ifndef FDB_CORE_H
#define FDB_CORE_H

static const int FDB_SERVER_PORT = 10000;
static const int FDBD_SERVER_PORT = 10001;

static const char *COMMAND_CONNECT = "connect";
static const char *COMMAND_DISCONNECT = "disconnect";
static const char *COMMAND_APPS = "apps";

void printHelp();

#endif //FDB_CORE_H
