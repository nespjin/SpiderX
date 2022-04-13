//
// Created by jin on 2022/4/12.
//

#ifndef FDB_CORE_H
#define FDB_CORE_H

#include <string>

extern const int FDB_SERVER_PORT;
extern const int FDBD_SERVER_PORT;

extern const char *COMMAND_CONNECT;
extern const char *COMMAND_DISCONNECT;
extern const char *COMMAND_APPS;

void printHelp();

void printError(const std::string &tag, const std::string &msg);

void printErrorDebug(const std::string &tag, const std::string &msg);

void printInfo(const std::string &tag, const std::string &msg);

void printInfoDebug(const std::string &tag, const std::string &msg);

#endif //FDB_CORE_H
