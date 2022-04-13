//
// Created by jin on 2022/4/12.
//

#ifndef FDB_CORE_H
#define FDB_CORE_H

#include <string>

static const int FDB_SERVER_PORT = 10000;
static const int FDBD_SERVER_PORT = 10001;

static const char *COMMAND_CONNECT = "connect";
static const char *COMMAND_DISCONNECT = "disconnect";
static const char *COMMAND_APPS = "apps";

void printHelp();

void printError(const std::string &tag, const std::string &msg);

void printErrorDebug(const std::string &tag, const std::string &msg);

void printInfo(const std::string &tag, const std::string &msg);

void printInfoDebug(const std::string &tag, const std::string &msg);

#endif //FDB_CORE_H
