//
// Created by jin on 2022/4/12.
//

#include <iostream>
#include "core.h"

/**
 * print help information
 */
void printHelp() {
    std::cout << "fdb pm install <plugin file path> \n"
                 "fdb pm uninstall <plugin id> \n"
              << std::endl;
}

void printError(const std::string &tag, const std::string &msg) {
    std::cout << "Error: " << tag << ": " << msg << std::endl;
}

void printErrorDebug(const std::string &tag, const std::string &msg) {
#if DEBUG
    std::cout << "DEBUG: " << "Error: " << tag << ": " << msg << std::endl;
#endif
}

void printInfo(const std::string &tag, const std::string &msg) {
    std::cout << "Info: " << tag << ": " << msg << std::endl;
}

void printInfoDebug(const std::string &tag, const std::string &msg) {
#if DEBUG
    std::cout << "DEBUG: " << "Info: " << tag << ": "<< msg << std::endl;
#endif
}
