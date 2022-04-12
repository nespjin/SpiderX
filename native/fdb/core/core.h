//
// Created by jin on 2022/4/12.
//

#ifndef FDB_CORE_H
#define FDB_CORE_H

#define HOST_OS_LINUX "linux"
#define HOST_OS_OSX "osx"
#define HOST_OS_WIN "win"

struct Command {
    int argc;
    char **argv;
};

void printHelp();

#endif //FDB_CORE_H
